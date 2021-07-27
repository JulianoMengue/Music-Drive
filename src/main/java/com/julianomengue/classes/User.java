package com.julianomengue.classes;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {

	private String id;
	private String email;
	private String password;
	private List<Buddy> buddies = new ArrayList<Buddy>();
	private Chat chat = new Chat();
	private Profile profile = new Profile();
	private List<Doc> docs = new ArrayList<>();
	private List<Foto> fotos = new ArrayList<>();
	private List<Audio> audios = new ArrayList<>();

	public User() {
	}

	public User(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}

	public void setAudios(List<Audio> audios) {
		this.audios = audios;
	}

	public void setFotos(List<Foto> fotos) {
		this.fotos = fotos;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Audio> getAudios() {
		return audios;
	}

	public void addAudios(Audio audio) {
		this.audios.add(audio);
	}

	public List<Foto> getFotos() {
		return fotos;
	}

	public void addFotos(Foto foto) {
		this.fotos.add(foto);
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public List<Doc> getDocs() {
		return docs;
	}

	public void setDocs(List<Doc> docs) {
		this.docs = docs;
	}

	public void addDoc(Doc doc) {
		this.docs.add(doc);
	}

	public List<Buddy> getBuddies() {
		return buddies;
	}

	public void setBuddies(List<Buddy> buddies) {
		this.buddies = buddies;
	}

	public void addBuddies(Buddy buddy) {
		this.buddies.add(buddy);
	}

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public void removeBuddies(String email) {
		for (int i = 0; i < this.buddies.size(); i++) {
			if (this.buddies.get(i).getEmail().contentEquals(email)) {
				this.buddies.remove(i);
			}
		}
	}

}
