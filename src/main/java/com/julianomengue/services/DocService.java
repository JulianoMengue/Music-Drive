package com.julianomengue.services;

import java.util.Base64;
import java.util.List;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.julianomengue.classes.Doc;
import com.julianomengue.classes.User;
import com.julianomengue.repositories.DocRepository;

@Service
public class DocService {

	@Autowired
	private DocRepository docRepo;

	@Autowired
	private UserService userService;

	public Doc binaryToString(Doc doc) {
		doc.setDocString(Base64.getEncoder().encodeToString(doc.getDocbinary().getData()));
		return doc;
	}

	public Doc findById(String id) {
		return this.docRepo.findById(id).get();
	}

	public Doc insert(Doc doc) {
		return this.docRepo.insert(doc);
	}

	public void save(Doc doc) {
		this.docRepo.save(doc);
	}

	public void delete(Doc doc) {
		this.docRepo.delete(doc);
	}

	public Doc getDocByIdFromUser(String id, String email) {
		Doc doc = new Doc();
		List<Doc> docs = this.userService.getCurrentUser(email).getDocs();
		for (int i = 0; i < docs.size(); i++) {
			if (docs.get(i).getId().contentEquals(id)) {
				doc = docs.get(i);
			}
		}
		return doc;
	}

	public void deleteDocFromUser(String id, User user) {
		for (int i = 0; i < user.getDocs().size(); i++) {
			if (user.getDocs().get(i).getId().contentEquals(id)) {
				user.getDocs().remove(i);
				this.userService.save(user);
			}
		}
	}

	public void saveEditedUserDoc(Doc doc, String email) {
		User user = new User();
		user = this.userService.getCurrentUser(email);
		for (int i = 0; i < user.getDocs().size(); i++) {
			if (user.getDocs().get(i).getId().contentEquals(doc.getId())) {
				user.getDocs().remove(i);
				user.getDocs().add(doc);
				this.userService.save(user);
			}
		}

	}

	public String addDoc(MultipartFile file, String email) throws Exception {
		Doc doc = new Doc(new Binary(BsonBinarySubType.BINARY, file.getBytes()), file.getContentType(),
				file.getSize() / 1024, email, file.getOriginalFilename());
		doc = this.docRepo.insert(doc);
		User user = new User();
		user = this.userService.getCurrentUser(email);
		Doc newDoc = new Doc();
		newDoc.setId(doc.getId());
		newDoc.setTitle(doc.getTitle());
		newDoc.addOwners(email);
		user.addDoc(newDoc);
		this.userService.save(user);
		return doc.getId();
	}

}
