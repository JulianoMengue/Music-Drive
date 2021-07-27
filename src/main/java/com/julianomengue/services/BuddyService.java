package com.julianomengue.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.julianomengue.classes.Buddy;
import com.julianomengue.classes.User;

@Service
public class BuddyService {

	@Autowired
	private UserService userService;

	public List<Buddy> getAllNotYourBuddies(String email) {
		List<User> allUsers = this.userService.findAll();
		List<Buddy> allBuddies = new ArrayList<Buddy>();
		List<Buddy> buddies = this.userService.getCurrentUser(email).getBuddies();
		Buddy newBuddy;
		for (int i = 0; i < allUsers.size(); i++) {
			if (!allUsers.get(i).getEmail().contentEquals(email)) {
				newBuddy = new Buddy();
				newBuddy.setEmail(allUsers.get(i).getEmail());
				newBuddy.setName(allUsers.get(i).getProfile().getFullName());
				allBuddies.add(newBuddy);
			}
		}

		for (int i = 0; i < allBuddies.size(); i++) {
			for (int y = 0; y < buddies.size(); y++) {
				if (allBuddies.get(i).getEmail().contentEquals(buddies.get(y).getEmail())) {
					allBuddies.remove(i);
				}
			}
		}

		return allBuddies;
	}

}
