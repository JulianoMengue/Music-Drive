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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.julianomengue.classes.Buddy;
import com.julianomengue.classes.Doc;
import com.julianomengue.classes.User;
import com.julianomengue.services.DocService;
import com.julianomengue.services.UserService;

@Controller
@RequestMapping("/docs")
public class DocController {

	@Autowired
	private DocService docService;

	@Autowired
	public UserController userController;

	@Autowired
	public UserService userService;

	String error = "Not allowed!";

	String fileError = " is not a text file.";

	@GetMapping()
	public String showDocs(Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			List<Doc> docs = this.userService.getCurrentUser(userEmail).getDocs();
			String no = null;
			if (docs.size() == 0) {
				no = "You don't have any texts yet.";
			}
			model.addAttribute("no", no);
			model.addAttribute("userEmail", userEmail);
			model.addAttribute("docs", docs);
			return "docs/docs";
		} else {
			model.addAttribute("error", error);
			User user = new User();
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@GetMapping("/uploadDoc")
	public String uploadDoc(Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			model.addAttribute("userEmail", userEmail);
			return "docs/new-doc";
		} else {
			model.addAttribute("error", error);
			User user = new User();
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@PostMapping("/add")
	public String addDoc(@RequestParam("doc") MultipartFile doc, Model model, @CookieValue("email") String userEmail)
			throws Exception {
		if (!userEmail.isBlank()) {
			this.docService.addDoc(doc, userEmail);
			return "redirect:/docs";
		}

		else {
			model.addAttribute("error", error);
			User user = new User();
			model.addAttribute("user", user);
			return "users/user-login";
		}

	}

	@GetMapping("/showDoc")
	public String showUserDocBig(@RequestParam String id, Model model, @CookieValue("email") String userEmail) {
		Doc doc = new Doc();
		doc = this.docService.findById(id);
		boolean exist = false;
		for (int i = 0; i < doc.getOwners().size(); i++) {
			if (doc.getOwners().get(i).contentEquals(userEmail)) {
				exist = true;
			}
		}

		if (!userEmail.isBlank() && exist) {
			model.addAttribute("userEmail", userEmail);
			model.addAttribute("doc", this.docService.binaryToString(doc).getDocString());
			model.addAttribute("title", this.docService.getDocByIdFromUser(id, userEmail).getTitle());
			model.addAttribute("id", doc.getId());
			model.addAttribute("size", doc.getSize());
			model.addAttribute("docType", doc.getDocType());
			return "docs/show-doc";
		} else {
			model.addAttribute("error", error);
			User user = new User();
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@GetMapping("/delete")
	public String deleteDoc(@RequestParam String id, Model model, @CookieValue("email") String userEmail) {
		Doc doc = docService.findById(id);
		List<String> ownersEmails = doc.getOwners();
		boolean exist = false;
		for (int i = 0; i < ownersEmails.size(); i++) {
			if (ownersEmails.get(i).contentEquals(userEmail)) {
				exist = true;
			}
		}

		if (!userEmail.isBlank() && exist) {
			String title = this.docService.getDocByIdFromUser(id, userEmail).getTitle();
			model.addAttribute("userEmail", userEmail);
			model.addAttribute("id", doc.getId());
			model.addAttribute("doc", this.docService.binaryToString(doc).getDocString());
			model.addAttribute("title", title);
			model.addAttribute("docType", doc.getDocType());
			return "docs/delete-doc";
		} else {
			model.addAttribute("error", error);
			User user = new User();
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@GetMapping("/deleteConfirm")
	public String deleteComfirm(@RequestParam String id, Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			this.docService.deleteDocFromUser(id, this.userService.getCurrentUser(userEmail));
			Doc doc = this.docService.findById(id);
			doc.removeOwners(userEmail);
			this.docService.save(doc);
			return "redirect:/docs";
		} else {
			model.addAttribute("error", error);
			User user = new User();
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@GetMapping("/renameDoc")
	public String renameDoc(@RequestParam String id, Model model, @CookieValue("email") String userEmail) {
		Doc doc = docService.findById(id);
		List<String> ownersEmails = doc.getOwners();
		boolean exist = false;
		for (int i = 0; i < ownersEmails.size(); i++) {
			if (ownersEmails.get(i).contentEquals(userEmail)) {
				exist = true;
			}
		}

		if (!userEmail.isBlank() && exist) {
			model.addAttribute("userEmail", userEmail);
			Doc newDoc = new Doc();
			newDoc = this.docService.getDocByIdFromUser(id, userEmail);
			model.addAttribute("doc", newDoc);
			return "docs/rename-doc";
		} else {
			model.addAttribute("error", error);
			User user = new User();
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@RequestMapping("/saveNewName")
	public String saveNewName(Doc doc, Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			Doc newDoc = new Doc();
			newDoc = this.docService.getDocByIdFromUser(doc.getId(), userEmail);
			newDoc.setTitle(doc.getTitle());
			this.docService.saveEditedUserDoc(newDoc, userEmail);
			Doc DBDoc = new Doc();
			DBDoc = this.docService.findById(doc.getId());
			DBDoc.setTitle(doc.getTitle());
			this.docService.save(DBDoc);
			return "redirect:/docs";
		} else {
			model.addAttribute("error", error);
			User user = new User();
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@RequestMapping("/sendDoc")
	public String sendDocToBuddy(Model model, @CookieValue("email") String userEmail, @RequestParam String id,
			HttpServletResponse response) {
		Doc doc = docService.findById(id);
		List<String> ownersEmails = doc.getOwners();
		boolean exist = false;
		for (int i = 0; i < ownersEmails.size(); i++) {
			if (ownersEmails.get(i).contentEquals(userEmail)) {
				exist = true;
			}
		}
		if (!userEmail.isBlank() && exist) {
			String title = this.docService.getDocByIdFromUser(id, userEmail).getTitle();
			List<Buddy> buddies = this.userService.getCurrentUser(userEmail).getBuddies();
			model.addAttribute("title", title);
			model.addAttribute("buddies", buddies);
			model.addAttribute("userEmail", userEmail);
			Cookie cookie = null;
			cookie = new Cookie("docId", id);
			cookie.setSecure(false);
			cookie.setHttpOnly(false);
			cookie.setMaxAge(7 * 24 * 60 * 60);
			response.addCookie(cookie);
			return "docs/send-doc-to-buddy";
		} else {
			model.addAttribute("error", error);
			User user = new User();
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@RequestMapping("/sendDocToBuddy")
	public String comfirmSendDocToBuddy(Model model, @CookieValue("email") String userEmail,
			@RequestParam String buddyEmail, @CookieValue("docId") String id) {
		User buddy = this.userService.getCurrentUser(buddyEmail);
		Doc doc = this.docService.getDocByIdFromUser(id, userEmail);
		doc.addOwners(buddyEmail);
		Doc docFromDatabase = this.docService.findById(id);
		docFromDatabase.addOwners(buddyEmail);
		this.docService.save(docFromDatabase);
		buddy.addDoc(doc);
		this.userService.save(buddy);
		String message = doc.getTitle() + " sent to " + buddy.getEmail();
		model.addAttribute("message", message);
		model.addAttribute("userEmail", userEmail);
		model.addAttribute("docs", this.userService.getCurrentUser(userEmail).getDocs());
		return "docs/docs";
	}

}
