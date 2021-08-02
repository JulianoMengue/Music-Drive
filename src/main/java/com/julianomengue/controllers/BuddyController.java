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

import com.julianomengue.classes.Buddy;
import com.julianomengue.classes.Chat;
import com.julianomengue.classes.Message;
import com.julianomengue.classes.User;
import com.julianomengue.services.BuddyService;
import com.julianomengue.services.FotoService;
import com.julianomengue.services.UserService;

@Controller
@RequestMapping("/buddies")
public class BuddyController {

	@Autowired
	private UserService userService;

	@Autowired
	private FotoService fotoService;

	@Autowired
	private BuddyService buddyService;

	String error = "Not allowed!";

	@GetMapping()
	public String yourBuddies(Model model, @CookieValue("email") String userEmail) throws IOException {
		if (!userEmail.isBlank()) {
			List<Buddy> buddies = this.userService.getCurrentUser(userEmail).getBuddies();
			String no = null;
			if (buddies.size() == 0) {
				no = "You don't have any buddy yet.";
			}
			model.addAttribute("no", no);
			model.addAttribute("userEmail", userEmail);
			model.addAttribute("buddies", buddies);
			return "buddies/buddies";
		} else {
			User user = new User();
			model.addAttribute("error", error);
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@GetMapping("/yourBuddies")
	public String getNotYourBuddies(Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			List<Buddy> buddies = this.buddyService.getAllNotYourBuddies(userEmail);
			String no = null;
			if (buddies.size() == 0) {
				no = "You don't have any buddy yet.";
			}
			model.addAttribute("no", no);
			model.addAttribute("userEmail", userEmail);
			model.addAttribute("buddies", buddies);
			return "buddies/your-buddies";
		} else {
			User user = new User();
			model.addAttribute("error", error);
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@GetMapping("/showBuddy")
	public String showBuddy(Model model, @CookieValue("email") String userEmail, @RequestParam String email) {
		if (!userEmail.isBlank()) {
			User user = this.userService.findOne(email);
			model.addAttribute("image", this.fotoService
					.binaryToString(this.fotoService.findById(user.getProfile().getFotoId())).getFotoString());
			model.addAttribute("email", email);
			model.addAttribute("profile", user.getProfile());
			model.addAttribute("userEmail", userEmail);
			return "buddies/buddy-profile";
		} else {
			User user = new User();
			model.addAttribute("error", error);
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@GetMapping("/messages")
	public String messages(Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			Chat chat = this.userService.getCurrentUser(userEmail).getChat();
			model.addAttribute("userEmail", userEmail);
			model.addAttribute("messages", chat.getMessages());
			Message message = new Message();
			model.addAttribute("message", message);
			return "buddies/messages";
		} else {
			User user = new User();
			model.addAttribute("error", error);
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@GetMapping("/saveMessage")
	public String saveMessage(Model model, @CookieValue("email") String userEmail,
			@RequestParam("content") String content) {
		if (!userEmail.isBlank()) {
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
		} else {
			User user = new User();
			model.addAttribute("error", error);
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@GetMapping("/addBuddy")
	public String addBuddy(Model model, @CookieValue("email") String userEmail, @RequestParam String email) {
		if (!userEmail.isBlank()) {
			String message = " is your buddy know";
			User user = this.userService.getCurrentUser(userEmail);
			User buddy = this.userService.getCurrentUser(email);

			Buddy buddyBuddy = new Buddy();
			buddyBuddy.setEmail(buddy.getEmail());
			buddyBuddy.setName(buddy.getProfile().getFullName());
			user.addBuddies(buddyBuddy);

			Buddy userUser = new Buddy();
			userUser.setEmail(user.getEmail());
			userUser.setName(user.getProfile().getFullName());
			buddy.addBuddies(userUser);

			this.userService.save(user);
			this.userService.save(buddy);

			model.addAttribute("messageGreen", email + message);
			List<Buddy> buddies = this.userService.getCurrentUser(userEmail).getBuddies();
			String no = null;
			if (buddies.size() == 0) {
				no = "You don't have any buddy yet.";
			}
			model.addAttribute("no", no);
			model.addAttribute("userEmail", userEmail);
			model.addAttribute("buddies", buddies);
			return "buddies/buddies";

		} else {
			User user = new User();
			model.addAttribute("error", error);
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@GetMapping("/deleteBuddy")
	public String deleteBuddy(Model model, @CookieValue("email") String userEmail, @RequestParam String email) {
		if (!userEmail.isBlank()) {
			String message = " is not your buddy anymore";
			User user = this.userService.getCurrentUser(userEmail);
			User buddy = this.userService.getCurrentUser(email);
			user.removeBuddies(email);
			buddy.removeBuddies(userEmail);
			this.userService.save(user);
			this.userService.save(buddy);
			model.addAttribute("message", email + message);
			List<Buddy> buddies = this.userService.getCurrentUser(userEmail).getBuddies();
			String no = null;
			if (buddies.size() == 0) {
				no = "You don't have any buddy yet.";
			}
			model.addAttribute("no", no);
			model.addAttribute("userEmail", userEmail);
			model.addAttribute("buddies", buddies);
			return "buddies/buddies";
		} else {
			User user = new User();
			model.addAttribute("error", error);
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}
}
