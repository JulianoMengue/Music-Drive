package com.julianomengue.classes;

import java.util.ArrayList;
import java.util.List;

public class Message {

	private String id;
	private String content;
	private List<String> owners = new ArrayList<String>();

	public Message() {
		super();
	}

	public Message(String id, String content, List<String> owners) {
		super();
		this.id = id;
		this.content = content;
		this.owners = owners;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<String> getOwners() {
		return owners;
	}

	public void setOwners(List<String> owners) {
		this.owners = owners;
	}

	public void addOwner(String email) {
		this.owners.add(email);
	}

}
