package com.julianomengue.controllers;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.julianomengue.classes.Foto;
import com.julianomengue.classes.User;
import com.julianomengue.services.FotoService;
import com.julianomengue.services.UserService;

@Controller
@RequestMapping("/fotos")
public class FotoController {

	@Autowired
	private FotoService fotoService;

	@Autowired
	public UserController userController;

	@Autowired
	public UserService userService;

	String error = "Not allowed!";

	@GetMapping()
	public String showFotos(Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			model.addAttribute("userEmail", userEmail);
			model.addAttribute("fotos", this.userService.getCurrentUser(userEmail).getFotos());
			return "fotos/fotos";
		} else {
			model.addAttribute("error", error);
			User user = new User();
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@GetMapping("/uploadFoto")
	public String uploadPhoto(Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			model.addAttribute("userEmail", userEmail);
			return "fotos/new-foto";
		} else {
			model.addAttribute("error", error);
			User user = new User();
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@PostMapping("/add")
	public String addFoto(@RequestParam("image") MultipartFile image, Model model,
			@CookieValue("email") String userEmail) throws Exception {
		if (!userEmail.isBlank()) {
			this.fotoService.addFoto(image, userEmail);
			model.addAttribute("userEmail", userEmail);
			return "redirect:/fotos";
		} else {
			model.addAttribute("error", error);
			User user = new User();
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@GetMapping("/showFoto")
	public String showUserFotoBig(@RequestParam String id, Model model, @CookieValue("email") String userEmail) {
		Foto foto = new Foto();
		foto = this.fotoService.findById(id);
		boolean exist = false;

		for (int i = 0; i < foto.getOwners().size(); i++) {
			if (foto.getOwners().get(i).contentEquals(userEmail)) {
				exist = true;
			}
		}

		String name = this.fotoService.getFotoByIdFromUser(id, userEmail).getTitle();
		if (!userEmail.isBlank() && exist) {
			model.addAttribute("userEmail", userEmail);
			model.addAttribute("image", this.fotoService.binaryToString(foto).getFotoString());
			model.addAttribute("title", name);
			model.addAttribute("size", foto.getSize());
			model.addAttribute("id", foto.getId());
			return "fotos/show-foto";
		} else {
			model.addAttribute("error", error);
			User user = new User();
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public String deleteFoto(@RequestParam String id, Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			Foto foto = fotoService.findById(id);
			model.addAttribute("id", foto.getId());
			model.addAttribute("image", this.fotoService.binaryToString(foto).getFotoString());
			model.addAttribute("title", foto.getTitle());
			model.addAttribute("userEmail", userEmail);
			return "fotos/delete-foto";
		} else {
			model.addAttribute("error", error);
			User user = new User();
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@RequestMapping(value = "/deleteConfirm", method = RequestMethod.GET)
	public String deleteComfirm(@RequestParam String id, Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			this.fotoService.deleteFotoFromUser(id, userEmail);
			return "redirect:/fotos";
		} else {
			model.addAttribute("error", error);
			User user = new User();
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@RequestMapping(value = "/renameFoto", method = RequestMethod.GET)
	public String renameFoto(@RequestParam String id, Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			model.addAttribute("userEmail", userEmail);
			Foto foto = new Foto();
			foto = this.fotoService.getFotoByIdFromUser(id, userEmail);
			model.addAttribute("foto", foto);
			return "fotos/rename-foto";
		} else {
			model.addAttribute("error", error);
			User user = new User();
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@RequestMapping("/saveNewName")
	public String saveNewName(Foto foto, Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			Foto newFoto = new Foto();
			newFoto = this.fotoService.getFotoByIdFromUser(foto.getId(), userEmail);
			newFoto.setTitle(foto.getTitle());
			this.fotoService.saveEditedUserFoto(newFoto, userEmail);
			return "redirect:/fotos";
		} else {
			model.addAttribute("error", error);
			User user = new User();
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@RequestMapping("/sendFoto")
	public String sendFotoToBuddy(Model model, @CookieValue("email") String userEmail, @RequestParam String id,
			HttpServletResponse response) {
		Foto foto = this.fotoService.findById(id);
		foto.setTitle(this.fotoService.getFotoByIdFromUser(id, userEmail).getTitle());
		List<String> buddies = this.userService.getCurrentUser(userEmail).getBuddies();
		model.addAttribute("foto", this.fotoService.binaryToString(foto));
		model.addAttribute("buddies", buddies);
		model.addAttribute("userEmail", userEmail);
		Cookie cookie = null;
		cookie = new Cookie("fotoId", id);
		cookie.setSecure(false);
		cookie.setHttpOnly(false);
		cookie.setMaxAge(7 * 24 * 60 * 60);
		response.addCookie(cookie);
		return "fotos/send-foto-to-buddy";
	}

	@RequestMapping("/sendFotoToBuddy")
	public String comfirmSendFotoToBuddy(Model model, @CookieValue("email") String userEmail,
			@RequestParam String buddyEmail, @CookieValue("fotoId") String id) {
		User buddy = this.userService.getCurrentUser(buddyEmail);
		Foto foto = this.fotoService.getFotoByIdFromUser(id, userEmail);
		foto.addOwners(buddyEmail);
		Foto fotoFromDatabase = this.fotoService.findById(id);
		fotoFromDatabase.addOwners(buddyEmail);
		this.fotoService.save(fotoFromDatabase);
		buddy.addFotos(foto);
		this.userService.save(buddy);
		String message = foto.getTitle() + " sent to " + buddy.getEmail();
		model.addAttribute("message", message);
		model.addAttribute("userEmail", userEmail);
		model.addAttribute("fotos", this.userService.getCurrentUser(userEmail).getFotos());
		return "fotos/fotos";
	}

}
