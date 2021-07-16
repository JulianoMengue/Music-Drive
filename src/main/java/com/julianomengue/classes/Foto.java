package com.julianomengue.classes;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "fotos")
public class Foto {

	private String id;

	private String title;

	private List<String> owners = new ArrayList<String>();

	private Binary fotobinary;

	private String fotoString;

	private Long size;

	public Foto() {
	}

	public Foto(String id, String title) {
		super();
		this.id = id;
		this.title = title;
	}

	public Foto(String id, String title, String fotoString) {
		super();
		this.id = id;
		this.title = title;
		this.fotoString = fotoString;
	}

	public Foto(Binary fotoBinary, Long size, String title, String owner) {
		super();
		this.fotobinary = fotoBinary;
		this.size = size;
		this.title = title;
		this.owners.add(owner);
	}

	public Foto(String id, Binary fotobinary, String fotoString) {
		super();
		this.id = id;
		this.fotobinary = fotobinary;
		this.fotoString = fotoString;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Binary getFotobinary() {
		return fotobinary;
	}

	public void setFotobinary(Binary fotobinary) {
		this.fotobinary = fotobinary;
	}

	public String getFotoString() {
		return fotoString;
	}

	public void setFotoString(String fotoString) {
		this.fotoString = fotoString;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

}
