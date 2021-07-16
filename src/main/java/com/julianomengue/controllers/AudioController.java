package com.julianomengue.controllers;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.julianomengue.classes.Audio;
import com.julianomengue.classes.User;
import com.julianomengue.services.AudioService;
import com.julianomengue.services.UserService;

@Controller
@RequestMapping("/audios")
public class AudioController {

	@Autowired
	public AudioService audioService;

	@Autowired
	public UserController userController;

	@Autowired
	public UserService userService;

	String error = "Not allowed!";

	@GetMapping()
	public String getAllAudios(Model model, @CookieValue("email") String userEmail) throws IOException {
		if (!userEmail.isBlank()) {
			model.addAttribute("userEmail", userEmail);
			model.addAttribute("audios", this.userService.getCurrentUser(userEmail).getAudios());
			return "audios/audios";
		} else {
			User user = new User();
			model.addAttribute("error", error);
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@GetMapping("/uploadAudio")
	public String uploadAudio(Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			model.addAttribute("userEmail", userEmail);
			return "audios/new-audio";
		} else {
			User user = new User();
			model.addAttribute("error", error);
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@PostMapping("/add")
	public String add(@RequestParam("audio") MultipartFile audioBinary, Model model,
			@CookieValue("email") String userEmail) throws IOException {
		if (!userEmail.isBlank()) {
			this.audioService.addAudio(audioBinary, userEmail);
			model.addAttribute("audios", this.userService.getCurrentUser(userEmail).getAudios());
			model.addAttribute("userEmail", userEmail);
			return "redirect:/audios";
		} else {
			User user = new User();
			model.addAttribute("error", error);
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@GetMapping("/playAudio")
	public String playAudio(@RequestParam String id, Model model, @CookieValue("email") String userEmail) {
		Audio audio = new Audio();
		audio = this.audioService.findById(id);
		boolean exist = false;
		for (int i = 0; i < audio.getOwners().size(); i++) {
			if (audio.getOwners().get(i).contentEquals(userEmail)) {
				exist = true;
			}
		}
		if (!userEmail.isBlank() && exist) {
			model.addAttribute("userEmail", userEmail);
			model.addAttribute("id", audio.getId());
			model.addAttribute("title", audio.getTitle());
			model.addAttribute("size", audio.getSize());
			model.addAttribute("audio", Base64.getEncoder().encodeToString(audio.getAudioBinary().getData()));
			return "audios/show-audio";
		} else {
			User user = new User();
			model.addAttribute("error", error);
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@GetMapping("/delete")
	public String deleteAudio(@RequestParam String id, Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			Audio audio = new Audio();
			audio = this.audioService.getAudioByIdFromUser(id, userEmail);
			model.addAttribute("id", audio.getId());
			model.addAttribute("title", audio.getTitle());
			model.addAttribute("userEmail", userEmail);
			return "audios/delete-audio";
		} else {
			User user = new User();
			model.addAttribute("error", error);
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@GetMapping("/deleteConfirm")
	public String deleteComfirm(@RequestParam String id, Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			this.audioService.deleteAudioFromUser(id, this.userService.getCurrentUser(userEmail));
			model.addAttribute("userEmail", userEmail);
			model.addAttribute("audios", this.userService.getCurrentUser(userEmail).getAudios());
			return "redirect:/audios";
		} else {
			User user = new User();
			model.addAttribute("error", error);
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@GetMapping("/renameAudio")
	public String renameAudio(@RequestParam String id, Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			Audio audio = new Audio();
			audio = this.audioService.getAudioByIdFromUser(id, userEmail);
			model.addAttribute("userEmail", userEmail);
			model.addAttribute("audio", audio);
			return "audios/rename-audio";
		} else {
			User user = new User();
			model.addAttribute("error", error);
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@RequestMapping("/saveNewName")
	public String saveNewName(Audio audio, Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			Audio newAudio = new Audio();
			newAudio = this.audioService.getAudioByIdFromUser(audio.getId(), userEmail);
			newAudio.setTitle(audio.getTitle());
			this.audioService.saveEditedUserAudio(newAudio, userEmail);
			return "redirect:/audios";
		} else {
			User user = new User();
			model.addAttribute("error", error);
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

}
