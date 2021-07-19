package com.julianomengue.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	public String buddies(Model model, @CookieValue("email") String userEmail) throws IOException {
		if (!userEmail.isBlank()) {
			model.addAttribute("userEmail", userEmail);
			model.addAttribute("buddies", this.userService.getCurrentUser(userEmail).getBuddies());
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

	@GetMapping("/searchBuddy")
	public String searchBuddy(Model model, @CookieValue("email") String userEmail) {
		User user = new User();
		model.addAttribute("user", user);
		model.addAttribute("userEmail", userEmail);
		return "buddies/search-buddy";
	}

	@RequestMapping("/addNewBuddy")
	public String addNewBuddy(Model model, User buddy, @CookieValue("email") String userEmail) {
		String success = " is your buddy now.";
		String error = "User don't exist";
		User newBuddy = this.userService.findOne(buddy.getEmail());
		boolean exist = this.userService.isHeMyBuddy(buddy.getEmail(), userEmail);
		if (newBuddy.getEmail() != null && !exist) {
			User user = this.userService.getCurrentUser(userEmail);
			user.addBuddies(newBuddy.getEmail());
			this.userService.save(user);
			model.addAttribute("success", buddy.getEmail() + success);
			User newUser = new User();
			model.addAttribute("user", newUser);
			model.addAttribute("userEmail", userEmail);
			return "buddies/search-buddy";
		} else {
			model.addAttribute("error", error);
			User newUser = new User();
			model.addAttribute("user", newUser);
			model.addAttribute("userEmail", userEmail);
			return "buddies/search-buddy";
		}
	}

}
