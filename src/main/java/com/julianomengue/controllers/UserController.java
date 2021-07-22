package com.julianomengue.controllers;

import javax.servlet.http.HttpServletResponse;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.julianomengue.classes.Foto;
import com.julianomengue.classes.Profile;
import com.julianomengue.classes.User;
import com.julianomengue.services.FotoService;
import com.julianomengue.services.JavaEmail;
import com.julianomengue.services.UserDataService;
import com.julianomengue.services.UserService;

@Controller
@RequestMapping("/")
public class UserController {

	@Autowired
	private FotoService fotoService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserDataService userDataService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@GetMapping()
	public String login(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		return "users/user-login";
	}

	@RequestMapping("/checkUser")
	public String checkUser(Model model, HttpServletResponse response, User user) {
		String messageLoginError = "";
		if (this.userService.findOne(user.getEmail(), user.getPassword()).getEmail() != null
				&& this.userService.findOne(user.getEmail(), user.getPassword()).getPassword() != null) {
			this.userService.save(this.userService.findOne(user.getEmail(), user.getPassword()));
			this.userService.newCookie(user.getEmail(), response);
			return "redirect:/fotos";

		} else {
			messageLoginError = user.getEmail() + " is not registered or password don't macht!";
			model.addAttribute("messageLoginError", messageLoginError);
			return "users/user-login";
		}

	}

	@RequestMapping("/registerUser")
	public String registerUser(Model model, User user) {
		String messageSucess = "";
		String messageError = "";
		user.setEmail(user.getEmail().toLowerCase());
		if (this.userService.findOne(user.getEmail()).getEmail() == null) {
			String password = JavaEmail.getJavaMailSender(user);
			user.setPassword(passwordEncoder().encode(password));
			this.userService.insert(user);
			messageSucess = "Password at " + user.getEmail() + " sendet";
		} else {
			messageError = user.getEmail() + " account is already registered.";
		}
		model.addAttribute("messageError", messageError);
		model.addAttribute("messageSucess", messageSucess);
		this.login(model);
		return "users/user-login";
	}

	@RequestMapping("/passwordForgot")
	public String passwordForgot(Model model, User user) {
		String messagePasswordSucess = "";
		String messagePasswordError = "";
		user.setEmail(user.getEmail().toLowerCase());
		if (this.userService.findOne(user.getEmail()).getEmail() != null) {
			User u = this.userService.getCurrentUser(user.getEmail());
			String password = JavaEmail.emailRepeatPassword(u);
			u.setPassword(passwordEncoder().encode(password));
			this.userService.save(u);
			messagePasswordSucess = "Password sendet to " + u.getEmail();
		} else {
			messagePasswordError = user.getEmail() + " is not registered.";
		}
		model.addAttribute("messagePasswordSucess", messagePasswordSucess);
		model.addAttribute("messagePasswordError", messagePasswordError);
		return "users/user-login";
	}

	@RequestMapping("/userConfig")
	public String userConfig(Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			model.addAttribute("user", this.userService.getCurrentUser(userEmail));
			model.addAttribute("userEmail", userEmail);
			return "users/user-config";
		} else {
			User user = new User();
			model.addAttribute("error", "Not allowed");
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@RequestMapping("/logout")
	public String logoutUser(Model model, HttpServletResponse response) {
		this.userService.newCookie(null, response);
		return "redirect:/";
	}

	@RequestMapping("/editUserProfile")
	public String editUserProfile(Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			User user = new User();
			user = this.userService.getCurrentUser(userEmail);
			model.addAttribute("image", this.fotoService
					.binaryToString(this.fotoService.findById(user.getProfile().getFotoId())).getFotoString());
			model.addAttribute("email", userEmail);
			model.addAttribute("profile", user.getProfile());
			model.addAttribute("userEmail", userEmail);
			return "users/user-profile";
		} else {
			User user = new User();
			model.addAttribute("error", "Not allowed");
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@RequestMapping("/saveProfile")
	public String saveProfile(Model model, @CookieValue("email") String userEmail,
			@RequestParam("image") MultipartFile image, @RequestParam("fullName") String fullName,
			@RequestParam("birthday") String birthday, @RequestParam("address") String address,
			@RequestParam("country") String country) throws Exception {
		if (!userEmail.isBlank()) {
			model.addAttribute("userEmail", userEmail);
			Foto foto = new Foto();
			foto.setFotobinary(new Binary(BsonBinarySubType.BINARY, image.getBytes()));
			String fotoId = this.fotoService.insert(foto).getId();
			Profile profile = new Profile(fullName, birthday, address, country, fotoId);
			User user = new User();
			user = this.userService.getCurrentUser(userEmail);
			user.setProfile(profile);
			this.userService.save(user);
			model.addAttribute("image", this.fotoService
					.binaryToString(this.fotoService.findById(user.getProfile().getFotoId())).getFotoString());
			model.addAttribute("profile", user.getProfile());
			return "users/user-profile";
		} else {
			User user = new User();
			model.addAttribute("error", "Not allowed");
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@RequestMapping("/newPassword")
	public String newPassword(Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			User user = new User();
			model.addAttribute("userEmail", userEmail);
			model.addAttribute("user", user);
			return "users/user-new-password";
		} else {
			User user = new User();
			model.addAttribute("error", "Not allowed");
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@RequestMapping("/saveNewPassword")
	public String saveNewPassword(Model model, User user, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			String message = "";
			User newUser = new User();
			newUser = this.userService.getCurrentUser(userEmail);
			newUser.setPassword(user.getPassword());
			if (JavaEmail.getNewPassword(newUser)) {
				message = "Password at " + newUser.getEmail() + " sent";
			}
			newUser.setPassword(passwordEncoder().encode(user.getPassword()));
			model.addAttribute("user", this.userService.getCurrentUser(userEmail));
			model.addAttribute("userEmail", userEmail);
			model.addAttribute("messageSucess", message);
			this.userService.save(newUser);
			return "users/user-config";
		} else {
			User user1 = new User();
			model.addAttribute("error", "Not allowed");
			model.addAttribute("user", user1);
			return "users/user-login";
		}
	}

	@RequestMapping("/deleteAccount")
	public String deleteAccount(Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			String id = this.userService.getCurrentUser(userEmail).getId();
			model.addAttribute("data", this.userDataService.returnUserData(id));
			model.addAttribute("userEmail", userEmail);
			model.addAttribute("user", userEmail);
			return "users/user-data";
		} else {
			User user = new User();
			model.addAttribute("error", "Not allowed");
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@RequestMapping("/deleteAccountConfirm")
	public String deleteAccountConfirm(Model model, @CookieValue("email") String userEmail,
			HttpServletResponse response) {
		this.userService.newCookie(null, response);
		this.userService.delete(userEmail);
		return "redirect:/";
	}

}
