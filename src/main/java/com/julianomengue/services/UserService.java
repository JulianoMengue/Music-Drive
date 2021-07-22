package com.julianomengue.services;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.julianomengue.classes.User;
import com.julianomengue.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepo;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public User findById(String id) {
		return this.userRepo.findById(id).get();
	}

	public void insert(User user) {
		this.userRepo.insert(user);
	}

	public User save(User user) {
		user = this.userRepo.save(user);
		return user;
	}

	public User getCurrentUser(String email) {
		return this.findOne(email);
	}

	public List<User> findAll() {
		List<User> users = this.userRepo.findAll();
		return users;
	}

	public List<String> getAllEmails(String email) {
		List<String> emails = new ArrayList<String>();
		List<User> users = this.userRepo.findAll();
		User user = this.getCurrentUser(email);
		List<String> buddies = user.getBuddies();

		for (int i = 0; i < users.size(); i++) {
			if (!users.get(i).getEmail().contentEquals(email)) {
				emails.add(users.get(i).getEmail());
			}
		}

		for (int i = 0; i < buddies.size(); i++) {
			for (int y = 0; y < emails.size(); y++) {
				if (buddies.get(i).contentEquals(emails.get(y))) {
					emails.remove(y);
				}
			}
		}

		return emails;
	}

	public List<String> getYourBuddies(String email) {
		User user = this.getCurrentUser(email);
		List<String> allEmails = user.getBuddies();
		return allEmails;
	}

	public User findOne(String email) {
		ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("id", "password", "profile", "chat",
				"buddies", "fotos", "audios", "docs");
		User user = new User();
		user.setEmail(email);
		Example<User> ex = Example.of(user, matcher);
		Supplier<User> u = () -> new User(null, null);
		user = this.userRepo.findOne(ex).orElseGet(u);
		return user;
	}

	public User findOne(String email, String password) {
		User user = new User();
		user.setEmail(email);
		if (this.findOne(email).getEmail() != null) {
			if (passwordEncoder().matches(password, this.findOne(email).getPassword())) {
				user = this.findOne(email);
			}
		}
		return user;
	}

	public void newCookie(String email, HttpServletResponse response) {
		Cookie cookie = null;
		cookie = new Cookie("email", email);
		cookie.setSecure(false);
		cookie.setHttpOnly(false);
		cookie.setMaxAge(7 * 24 * 60 * 60);
		response.addCookie(cookie);
	}

	public void delete(String email) {
		this.userRepo.delete(this.getCurrentUser(email));
	}

}
