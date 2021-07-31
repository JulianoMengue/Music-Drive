package com.julianomengue.classes;

public class Profile {

	private String fullName;
	private String birthday;
	private String address;
	private String country;
	private String fotoId = "610514c386d1db45a6de1c17";

	public Profile() {
		super();
	}

	public Profile(String fullName, String birthday, String address, String country, String id) {
		super();
		this.fullName = fullName;
		this.birthday = birthday;
		this.address = address;
		this.country = country;
		this.setFotoId(id);
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getFotoId() {
		return fotoId;
	}

	public void setFotoId(String fotoId) {
		this.fotoId = fotoId;
	}

}
