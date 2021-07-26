package com.julianomengue.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.julianomengue.classes.Chat;
import com.julianomengue.classes.Message;
import com.julianomengue.classes.User;
import com.julianomengue.services.FotoService;
import com.julianomengue.services.UserService;

@Controller
@RequestMapping("/buddies")
public class BuddyController {

	@Autowired
	private UserService userService;

	@Autowired
	private FotoService fotoService;

	String error = "Not allowed!";

	@GetMapping()
	public String getYoursBuddies(Model model, @CookieValue("email") String userEmail) {
		List<String> emails = this.userService.getYourBuddies(userEmail);
		model.addAttribute("userEmail", userEmail);
		model.addAttribute("buddies", emails);
		return "buddies/your-buddies";
	}

	@GetMapping("/yourBuddies")
	public String buddies(Model model, @CookieValue("email") String userEmail) throws IOException {
		if (!userEmail.isBlank()) {
			List<String> emails = this.userService.getAllEmails(userEmail);
			model.addAttribute("userEmail", userEmail);
			model.addAttribute("buddies", emails);
			return "buddies/buddies";
		} else {
			User user = new User();
			model.addAttribute("error", error);
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@GetMapping("/showBuddy")
	public String showBuddy(Model model, @CookieValue("email") String userEmail, @RequestParam String buddyEmail) {
		User user = this.userService.findOne(buddyEmail);
		model.addAttribute("image", this.fotoService
				.binaryToString(this.fotoService.findById(user.getProfile().getFotoId())).getFotoString());
		model.addAttribute("email", buddyEmail);
		model.addAttribute("profile", user.getProfile());
		model.addAttribute("userEmail", userEmail);
		return "buddies/buddy-profile";
	}

	@GetMapping("/messages")
	public String messages(Model model, @CookieValue("email") String userEmail) {
		Chat chat = this.userService.getCurrentUser(userEmail).getChat();
		model.addAttribute("userEmail", userEmail);
		model.addAttribute("messages", chat.getMessages());
		Message message = new Message();
		model.addAttribute("message", message);
		return "buddies/messages";
	}

	@GetMapping("/saveMessage")
	public String saveMessage(Model model, @CookieValue("email") String userEmail,
			@RequestParam("content") String content) {
		Message message = new Message();
		message.setContent(content);
		message.addOwner(userEmail);
		User user = this.userService.getCurrentUser(userEmail);
		user.getChat().addMessages(message);
		user = this.userService.save(user);
		Message newMessage = new Message();
		model.addAttribute("messages", user.getChat().getMessages());
		model.addAttribute("userEmail", userEmail);
		model.addAttribute("message", newMessage);
		return "buddies/messages";
	}

	@GetMapping("/addBuddy")
	public String addBuddy(Model model, @CookieValue("email") String userEmail, @RequestParam String buddyEmail) {
		String message = " is your buddy know";
		User user = this.userService.getCurrentUser(userEmail);
		User buddy = this.userService.getCurrentUser(buddyEmail);
		user.addBuddies(buddyEmail);
		buddy.addBuddies(userEmail);
		this.userService.save(user);
		this.userService.save(buddy);
		List<String> emails = this.userService.getAllEmails(userEmail);
		model.addAttribute("userEmail", userEmail);
		model.addAttribute("buddies", emails);
		model.addAttribute("message", buddyEmail + message);
		return "buddies/buddies";
	}

	@GetMapping("/deleteBuddy")
	public String deleteBuddy(Model model, @CookieValue("email") String userEmail, @RequestParam String buddyEmail) {
		String message = " is not your buddy anymore";
		User user = this.userService.getCurrentUser(userEmail);
		User buddy = this.userService.getCurrentUser(buddyEmail);
		user.removeBuddies(buddyEmail);
		buddy.removeBuddies(userEmail);
		this.userService.save(user);
		this.userService.save(buddy);
		List<String> emails = this.userService.getYourBuddies(userEmail);
		model.addAttribute("userEmail", userEmail);
		model.addAttribute("buddies", emails);
		model.addAttribute("message", buddyEmail + message);
		return "buddies/your-buddies";
	}

}
