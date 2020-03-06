package com.eee.app;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONObject;

import com.eee.app.wlchllyn.entities.PatientInfo;
import com.eee.app.wlchllyn.entities.PatientVitalSignDTO;
import com.eee.app.wlchllyn.entities.PrsPatientResponseDto;

import simplexml.SimpleXml;
import simplexml.model.Element;

// Each Client Connection will be managed in a dedicated Thread
public class HttpRequestHandler implements Runnable {

	static final File WEB_ROOT = new File(".");
	static final String DEFAULT_FILE = "index.html";
	static final String FILE_NOT_FOUND = "404.html";
	static final String METHOD_NOT_SUPPORTED = "not_supported.html";

	// PrsPatientResponseDto prsPatientResponseDto;
	String name = null;
	String surname = null;
	String middlename = null;
	String birthdate = null; // 1993-07-30T00:00:00
	String secondLastName = null;
	String room = null;
	String bed = null;

	// verbose mode
	static final boolean verbose = true;

	// Client Connection via Socket Class
	private Socket clientSocket;

	public HttpRequestHandler(Socket c) {
		clientSocket = c;
	}

	@Override
	public void run() {
		// we manage our particular client connection
		BufferedReader in = null;
		PrintWriter out = null;
		BufferedOutputStream dataOut = null;
		String fileRequested = null;

		try {
			// we read characters from the client via input stream on the socket
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			// we get character output stream to client (for headers)
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			// get binary output stream to client (for requested data)
			dataOut = new BufferedOutputStream(clientSocket.getOutputStream());

			// get first line of the request from the client
			String input = in.readLine();
			StringBuilder requestString = new StringBuilder();
			String strCurrentLine = "";

			while ((strCurrentLine = in.readLine()) != null) {
				System.out.println(strCurrentLine);
				requestString.append(strCurrentLine);
				if (strCurrentLine.contains("</soap:Envelope>"))
					break;
			}

			System.out.println(requestString.toString());

			if (requestString.toString().contains("patientInfo")) {
				try {
					sendPatientInfoPost(requestString.toString());
					// writeXMLToFile(requestString.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					sendPost(requestString.toString());
					// writeXMLToFile(requestString.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// we parse the request with a string tokenizer
			StringTokenizer parse = null;
			String method = null;
			if (input == null) {
				method = "GET"; // we get the HTTP method of the client
				fileRequested = ".";
			} else {
				parse = new StringTokenizer(input);
				method = parse.nextToken().toUppeeease(); // we get the HTTP method of the client
				// we get file requested
				fileRequested = parse.nextToken().toLoweeease();
			}

			System.out.println("Request is arrived");

			// we support only GET and HEAD methods, we check
			if (method.equals("GET") && method.equals("HEAD")) {
				if (verbose) {
					System.out.println("501 Not Implemented : " + method + " method.");
				}

				// we return the not supported file to the client
//				File file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED);
//				int fileLength = (int) file.length();
				String contentMimeType = "text/html";
				// read content to return to client
				// byte[] fileData = readFileData(file, fileLength);

				// we send HTTP Headers with data to client
				out.println("HTTP/1.1 501 Not Implemented");
				out.println("Server: Java HTTP Server from SSaurel : 1.0");
				out.println("Date: " + new Date());
				out.println("Content-type: " + contentMimeType);
				// out.println("Content-length: " + fileLength);
				out.println(); // blank line between headers and content, very important !
				out.flush(); // flush character output stream buffer

				// file
				// dataOut.write(fileData, 0, fileLength);
				// dataOut.flush();

			} else {
				// GET or HEAD method
				if (fileRequested.endsWith("/")) {
					fileRequested += DEFAULT_FILE;
				}

				// File file = new File(WEB_ROOT, fileRequested);
				// File file = new
				// File(getClass().getClassLoader().getResource("index.html").getFile());
				// int fileLength = (int) file.length();
				String content = getContentType(fileRequested);

				if (method.equals("GET")) { // GET method so we return content
					// byte[] fileData = readFileData(file, fileLength);
					out.println("HTTP/1.1 501 Not Implemented");
					out.println("Server: Java HTTP Server from SSaurel : 1.0");
					out.println("Date: " + new Date());
					out.println("Content-type: " + content);
					out.println("Content-length: ");
					out.println(); // blank line between headers and content, very important !
					out.println(
							"<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><soap:Body><patientInfoResponse xmlns=\"http://temp.org/\"><patientInfoResult><FirstName>NUROL</FirstName><LastName>GENÇYILMAZ</LastName><Gender>M</Gender><DateofBirth>1983-05-20T00:00:00</DateofBirth><Success>true</Success></patientInfoResult></patientInfoResponse></soap:Body></soap:Envelope>");
					out.flush(); // flush character output stream buffer

					// dataOut.write(fileData, 0, fileLength);
					// dataOut.flush();

				} else if (method.equals("POST")) {
//					room = "123";
//					bed = "123";
					out.println("HTTP/1.1 200 OK");
					out.println("Cache-Control: private, max-age=0");
					out.println("Content-Type: text/xml; charset=utf-8");
					out.println("Server: Avicenna");
					out.println("X-Powered-By: eee Group Engineering");
					out.println("Content-Length: 504");
					out.println(); // blank line between headers and content, very important !

					out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
					out.println(
							"<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><soap:Body><patientInfoResponse xmlns=\"http://temp.org/\"><patientInfoResult>"
									+ "<FirstName>" + name + "</FirstName><LastName>" + surname + "</LastName>"
									+ "<MiddleName>A</MiddleName><Gender>F</Gender><DateofBirth>" + birthdate
									+ "</DateofBirth><Room>123</Room><Bed>321</Bed>"
									+ "<Success>true</Success></patientInfoResult></patientInfoResponse></soap:Body></soap:Envelope>\r\n");
					out.println("\r\n");

					out.flush(); // flush character output stream buffer
					out.close();
				}

				if (verbose) {
					System.out.println("File " + fileRequested + " of type " + content + " returned");
				}

			}

		} catch (FileNotFoundException fnfe) {
			try {
				fileNotFound(out, dataOut, fileRequested);
			} catch (IOException ioe) {
				System.err.println("Error with file not found exception : " + ioe.getMessage());
			}

		} catch (IOException ioe) {
			System.err.println("Server error : " + ioe);
		} finally {
			try {
				in.close();
				out.close();
				dataOut.close();
			} catch (Exception e) {
				System.err.println("Error closing stream : " + e.getMessage());
			}

			if (verbose) {
				System.out.println("Connection closed.\n");
			}
		}
	}

	private void sendPatientInfoPost(String xmlString) throws JAXBException, IOException {

		StringWriter writer = new StringWriter();
		JAXBContext jaxbContext = null;
		Marshaller jaxbMarshaller = null;

		PatientInfo patientInfo = XmlConverter.convertToPatientInfo(xmlString);

		jaxbContext = JAXBContext.newInstance(PatientInfo.class);
		jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.marshal(patientInfo, writer);

		HttpPost httpPost = new HttpPost("http://localhost:8080/patient-info");
		httpPost.addHeader("content-type", "application/xml");

		StringEntity userEntity = new StringEntity(writer.getBuffer().toString());
		httpPost.setEntity(userEntity);
		try (CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(httpPost)) {
			// System.out.println(EntityUtils.toString(response.getEntity()));
			String result = EntityUtils.toString(response.getEntity());

			JSONObject obj = new JSONObject(result);
			name = obj.getString("name");
			surname = obj.getString("surname");
			birthdate = obj.getString("birthdate");
//			room = obj.getString("room");
//			bed = obj.getString("bed");
		}
	}

	// return supported MIME Types
	private String getContentType(String fileRequested) {
		if (fileRequested.endsWith(".htm") || fileRequested.endsWith(".html"))
			return "text/html";
		else
			return "text/html";
	}

	private void sendPost(String xmlString) throws Exception {
		// private void sendPost(String xmlString) throws Exception {

		String cleanedXML = removeSoapEnvelopeAndSoapBody(xmlString);
		if (cleanedXML == null) {
			// write xmlString to file in case of data loose
		}

		if (!xmlString.isEmpty()) {
			String[] newXmlString = xmlString.split("\\?>");
			if (newXmlString.length > 1)
				xmlString = newXmlString[1];
		}

		StringWriter writer = new StringWriter();
		JAXBContext jaxbContext = null;
		Marshaller jaxbMarshaller = null;
		if (cleanedXML != null)
			if (cleanedXML.contains("patientInfo")) {
//				PatientInfo patientInfo = XmlConverter.convertToPatientInfo(cleanedXML);
//
//				jaxbContext = JAXBContext.newInstance(PatientInfo.class);
//				jaxbMarshaller = jaxbContext.createMarshaller();
//				jaxbMarshaller.marshal(patientInfo, writer);
//
//				HttpPost httpPost = new HttpPost("http://localhost:8080/patient-info");
//				httpPost.addHeader("content-type", "application/xml");
//
//				StringEntity userEntity = new StringEntity(writer.getBuffer().toString());
//				httpPost.setEntity(userEntity);
//				try (CloseableHttpClient httpClient = HttpClients.createDefault();
//						CloseableHttpResponse response = httpClient.execute(httpPost)) {
//					// System.out.println(EntityUtils.toString(response.getEntity()));
//					String result = EntityUtils.toString(response.getEntity());
//
//					JSONObject obj = new JSONObject(result);
//
//					name = obj.getString("name");
//					surname = obj.getString("surname");
//				}

			} else {
				PatientVitalSignDTO vitalSignDTO = XmlConverter.convertToVitalSign(cleanedXML);

				HttpPost httpPost = new HttpPost("http://localhost:8080/add-vital-sign");

				jaxbContext = JAXBContext.newInstance(PatientVitalSignDTO.class);
				jaxbMarshaller = jaxbContext.createMarshaller();
				jaxbMarshaller.marshal(vitalSignDTO, writer);

				httpPost.addHeader("content-type", "application/xml");

				StringEntity userEntity = new StringEntity(writer.getBuffer().toString());
				httpPost.setEntity(userEntity);
				try (CloseableHttpClient httpClient = HttpClients.createDefault();
						CloseableHttpResponse response = httpClient.execute(httpPost)) {
					System.out.println(EntityUtils.toString(response.getEntity()));
				}
			}
	}

	private String removeSoapEnvelopeAndSoapBody(String xmlString) {

		if (!xmlString.isEmpty()) {
//			String[] newXmlString = xmlString.split("\\?>");
//			if (newXmlString.length > 1)
//				xmlString = newXmlString[1];
		} else
			return null;
		try {
			SimpleXml simpleXML = new SimpleXml();
			Element element = simpleXML.fromXml(xmlString);
			String x = simpleXML.domToXml(element.children.get(0).children.get(0));
			return x;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void fileNotFound(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException {
		File file = new File(WEB_ROOT, FILE_NOT_FOUND);
		int fileLength = (int) file.length();
		String content = "application/html";
		byte[] fileData = readFileData(file, fileLength);

		out.println("HTTP/1.1 404 File Not Found");
		out.println("Server: Java HTTP Server from SSaurel : 1.0");
		out.println("Date: " + new Date());
		out.println("Content-type: " + content);
		out.println("Content-length: " + fileLength);
		out.println(); // blank line between headers and content, very important !
		out.flush(); // flush character output stream buffer

		dataOut.write(fileData, 0, fileLength);
		dataOut.flush();

		if (verbose) {
			System.out.println("File " + fileRequested + " not found");
		}
	}

	public static void writeXMLToFile(String request) throws IOException {
		Date requestDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateTime = new SimpleDateFormat("hh_mm__");
		String folderName = dateFormat.format(requestDate);
		String path = "";
		path = System.getenv("APPDATA") + File.separator + "Avicenna-welchallyn-backup";
		File fPath = new File(path);
		if (!fPath.isDirectory()) {
			if (!fPath.mkdirs()) {
				System.out.printf("Unable to create the folder %s, check your privileges.", path);
			}
		}

		path = path + File.separator + folderName;
		System.out.println(path);

		fPath = new File(path);
		if (!fPath.isDirectory()) {
			if (!fPath.mkdirs()) {
				System.out.printf("Unable to create the folder %s, check your privileges.", path);
			}
		}

		UUID uuid = UUID.randomUUID();
		String uuid_string = uuid.toString();
		String fileName = dateTime.format(requestDate) + uuid_string.replace("-", "");
		path = path + File.separator + fileName;
		Files.write(Paths.get(fileName + ".txt"), request.getBytes());
		Files.write(Paths.get(fileName + ".xml"), request.getBytes());
	}

	private byte[] readFileData(File file, int fileLength) throws IOException {
		FileInputStream fileIn = null;
		byte[] fileData = new byte[fileLength];

		try {
			fileIn = new FileInputStream(file);
			fileIn.read(fileData);
		} finally {
			if (fileIn != null)
				fileIn.close();
		}

		return fileData;
	}

	public static void main(String... args) {
		HttpRequestHandler handler = new HttpRequestHandler(new Socket());

		XmlConverter converter = new XmlConverter();
		try {
			handler.sendPost(converter.getPatientInfoString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}