package com.julianomengue.classes;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "docs")
public class Doc {

	private String id;

	private String title;

	private List<String> owners = new ArrayList<String>();

	private Binary docbinary;

	private String docString;

	private String docType;

	private Long size;

	public Doc() {
	}

	public Doc(Binary docbinary, String docType, Long size, String email, String title) {
		super();
		this.title = title;
		this.owners.add(email);
		this.docbinary = docbinary;
		this.docType = docType;
		this.size = size;
	}

	public Doc(String id, String title, String docString) {
		super();
		this.setId(id);
		this.setTitle(title);
		this.setDocString(docString);
	}

	public Doc(String id, Binary docbinary, String docString) {
		super();
		this.setId(id);
		this.setDocbinary(docbinary);
		this.setDocString(docString);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Binary getDocbinary() {
		return docbinary;
	}

	public void setDocbinary(Binary docbinary) {
		this.docbinary = docbinary;
	}

	public String getDocString() {
		return docString;
	}

	public void setDocString(String docString) {
		this.docString = docString;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public List<String> getOwners() {
		return owners;
	}

	public void setOwners(List<String> owners) {
		this.owners = owners;
	}

	public void addOwners(String owner) {
		this.owners.add(owner);
	}

	public void removeOwners(String owner) {
		for (int i = 0; i < this.owners.size(); i++) {
			if (this.owners.get(i).contentEquals(owner)) {
				this.owners.remove(i);
			}
		}
	}

}
