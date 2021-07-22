package com.julianomengue.classes;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "audios")
public class Audio {

	private String id;

	private String title;

	private List<String> owners = new ArrayList<String>();

	private Binary audioBinary;

	private String audioString;

	private Long size;

	public Audio() {
		super();
	}

	public Audio(String title) {
		super();
		this.title = title;
	}

	public Audio(String id, String title) {
		super();
		this.id = id;
		this.title = title;
	}

	public Audio(Binary audioBinary, Long size, String title, String owner) {
		super();
		this.audioBinary = audioBinary;
		this.size = size;
		this.title = title;
		this.owners.add(owner);
	}

	public Audio(String id, String title, Long size) {
		super();
		this.id = id;
		this.title = title;
		this.size = size;
	}

	public Audio(String id, String title, Binary audioBinary, String audioString) {
		super();
		this.id = id;
		this.title = title;
		this.audioBinary = audioBinary;
		this.audioString = audioString;
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

	public Binary getAudioBinary() {
		return audioBinary;
	}

	public void setAudioBinary(Binary audioBinary) {
		this.audioBinary = audioBinary;
	}

	public String getAudioString() {
		return audioString;
	}

	public void setAudioString(String audioString) {
		this.audioString = audioString;
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