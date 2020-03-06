package com.eee.app.wlchllyn.entities;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "patientInfo", namespace = "http://temp.org/")
@XmlType(name = "patientInfo", propOrder = { "UserName", "Password", "uniquePatientId" })
@XmlAccessorType(XmlAccessType.FIELD)
public class PatientInfo implements Serializable{

	protected String UserName;
	protected String Password;
	protected String uniquePatientId;

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public String getUniquePatientId() {
		return uniquePatientId;
	}

	public void setUniquePatientId(String uniquePatientId) {
		this.uniquePatientId = uniquePatientId;
	}

}
