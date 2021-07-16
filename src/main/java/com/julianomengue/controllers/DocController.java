package com.julianomengue.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.julianomengue.classes.Doc;
import com.julianomengue.classes.User;
import com.julianomengue.repositories.DocRepository;
import com.julianomengue.services.DocService;
import com.julianomengue.services.UserService;

@Controller
@RequestMapping("/docs")
public class DocController {

	@Autowired
	private DocRepository docRepo;

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
			model.addAttribute("userEmail", userEmail);
			model.addAttribute("docs", this.userService.getCurrentUser(userEmail).getDocs());
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

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addDoc(@RequestParam("doc") MultipartFile doc, Model model, @CookieValue("email") String userEmail)
			throws Exception {
		if (!userEmail.isBlank() && doc.getContentType().contentEquals("text/plain")) {
			this.docService.addDoc(doc, userEmail);
			return "redirect:/docs";
		}

		else if (!doc.getContentType().contentEquals("text/plain") && !userEmail.isBlank()) {
			model.addAttribute("fileError", doc.getContentType() + " " + fileError);
			model.addAttribute("userEmail", userEmail);
			return "docs/new-doc";
		}

		else {
			model.addAttribute("error", error);
			User user = new User();
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@RequestMapping(value = "/showDoc", method = RequestMethod.GET)
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
			model.addAttribute("title", doc.getTitle());
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

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public String deleteDoc(@RequestParam String id, Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			model.addAttribute("userEmail", userEmail);
			Doc doc = docService.findById(id);
			model.addAttribute("id", doc.getId());
			model.addAttribute("doc", this.docService.binaryToString(doc).getDocString());
			model.addAttribute("title", doc.getTitle());
			model.addAttribute("docType", doc.getDocType());
			return "docs/delete-doc";
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
			Doc doc = docService.findById(id);
			this.docRepo.delete(doc);
			this.docService.deleteDocFromUser(id, this.userService.getCurrentUser(userEmail));
			return "redirect:/docs";
		} else {
			model.addAttribute("error", error);
			User user = new User();
			model.addAttribute("user", user);
			return "users/user-login";
		}
	}

	@RequestMapping(value = "/renameDoc", method = RequestMethod.GET)
	public String renameDoc(@RequestParam String id, Model model, @CookieValue("email") String userEmail) {
		if (!userEmail.isBlank()) {
			model.addAttribute("userEmail", userEmail);
			Doc doc = new Doc();
			doc = this.docService.getDocByIdFromUser(id, userEmail);
			model.addAttribute("doc", doc);
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

}
