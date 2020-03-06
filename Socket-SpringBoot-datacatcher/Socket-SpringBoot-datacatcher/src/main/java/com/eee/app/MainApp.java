package com.eee.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainApp {
	public static final int PORT = 9247;
	public static final boolean VERBOSE = true;

	public static void main(String[] args) {

		SpringApplication.run(MainApp.class, args);
		
		ExecutorService executor = Executors.newFixedThreadPool(4);
		ServerSocket serveeeonnect = null;
		try {

			serveeeonnect = new ServerSocket(PORT);
			System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");
			while (true) {
				/*HttpRequestHandler server = new HttpRequestHandler(serveeeonnect.accept());
				if (VERBOSE) {
					System.out.println("Connecton opened. (" + new Date() + ")");
				}
				Thread thread = new Thread(server);
				thread.start();*/
				
				HttpRequestHandler worker = new HttpRequestHandler(serveeeonnect.accept());
				executor.execute(worker);
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Server Connection error : " + e.getMessage());
		} finally {
			try {
				serveeeonnect.close();
				
				executor.shutdown();
				while (!executor.isTerminated()) {
				}
				System.out.println("Finished all threads");
				
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Server Connection close error : " + e.getMessage());
			}
		}

	}

}
