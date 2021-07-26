package com.julianomengue.services;

import java.io.IOException;
import java.util.List;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.julianomengue.classes.Audio;
import com.julianomengue.classes.User;
import com.julianomengue.repositories.AudioRepository;

@Service
public class AudioService {

	@Autowired
	private AudioRepository audioRepo;

	@Autowired
	public UserService userService;

	public Audio findById(String id) {
		return this.audioRepo.findById(id).get();
	}

	public void delete(Audio audio) {
		this.audioRepo.delete(audio);
	}

	public void save(Audio audio) {
		this.audioRepo.save(audio);
	}

	public Audio insert(Audio audio) {
		return this.audioRepo.insert(audio);
	}

	public List<Audio> findAll() {
		return this.audioRepo.findAll();
	}

	public void saveEditedUserAudio(Audio audio, String email) {
		User user = new User();
		user = this.userService.getCurrentUser(email);
		for (int i = 0; i < user.getAudios().size(); i++) {
			if (user.getAudios().get(i).getId().contentEquals(audio.getId())) {
				user.getAudios().remove(i);
				user.getAudios().add(audio);
				this.userService.save(user);
			}
		}
	}

	public void deleteAudioFromUser(String id, User user) {
		for (int i = 0; i < user.getAudios().size(); i++) {
			if (user.getAudios().get(i).getId().contentEquals(id)) {
				user.getAudios().remove(i);
				this.userService.save(user);
			}
		}
	}

	public Audio getAudioByIdFromUser(String id, String email) {
		Audio audio = new Audio();
		List<Audio> audios = this.userService.getCurrentUser(email).getAudios();
		for (int i = 0; i < audios.size(); i++) {
			if (audios.get(i).getId().contentEquals(id)) {
				audio = audios.get(i);
			}
		}
		return audio;
	}

	public Audio addprofileAudio(MultipartFile file) throws IOException {
		Audio audio = new Audio();
		audio.setAudioBinary(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
		audio = this.audioRepo.insert(audio);
		Audio newAudio = new Audio();
		newAudio.setId(audio.getId());
		newAudio.setTitle(audio.getTitle());
		return newAudio;
	}

	public String addAudio(MultipartFile file, String email) throws IOException {
		Audio audio = new Audio(new Binary(BsonBinarySubType.BINARY, file.getBytes()), file.getSize() / 1024,
				file.getOriginalFilename(), email);
		audio = audioRepo.insert(audio);
		Audio newAudio = new Audio(audio.getId(), audio.getTitle(), audio.getSize());
		newAudio.addOwners(email);
		User user = new User();
		user = this.userService.getCurrentUser(email);
		user.getAudios().add(newAudio);
		this.userService.save(user);
		return audio.getId();
	}

}
