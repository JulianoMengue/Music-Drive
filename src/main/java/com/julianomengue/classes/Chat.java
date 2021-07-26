package com.julianomengue.classes;

import java.util.ArrayList;
import java.util.List;

public class Chat {

	private String id;
	private String owner;
	private List<Message> messages = new ArrayList<Message>();

	public Chat() {
		super();
	}

	public Chat(String id, String owner, List<Message> messages) {
		super();
		this.id = id;
		this.owner = owner;
		this.messages = messages;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public void addMessages(Message message) {
		this.messages.add(message);
	}

}
