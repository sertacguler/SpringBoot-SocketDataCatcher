package com.eee.app.wlchllyn.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "patientInfo", namespace = "http://temp.org/")
@XmlType(name = "patientInfo", propOrder = { "name", "surname", "sex", "room", "bed" })
@XmlAccessorType(XmlAccessType.FIELD)
public class PrsPatientResponseDto {

	private Long PrsPatientId;
	private String name;
	private String surname;
	private String sex;
	private String middleName;
	private Date birthdate;
	private String room;
	private String bed;

	public Long getPrsPatientId() {
		return PrsPatientId;
	}

	public void setPrsPatientId(Long prsPatientId) {
		PrsPatientId = prsPatientId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getBed() {
		return bed;
	}

	public void setBed(String bed) {
		this.bed = bed;
	}

}
