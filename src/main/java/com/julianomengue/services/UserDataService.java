package com.julianomengue.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.julianomengue.classes.User;
import com.julianomengue.classes.UserFiles;

@Service
public class UserDataService {

	@Autowired
	public UserService userService;

	public UserFiles returnUserData(String id) {
		User user = new User();
		UserFiles userFiles = new UserFiles();
		user = this.userService.findById(id);
		userFiles.setFotos(user.getFotos().size());
		userFiles.setAudios(user.getAudios().size());
		userFiles.setDocs(user.getDocs().size());
		userFiles.setBuddies(user.getBuddies().size());
		return userFiles;
	}

}
