package com.julianomengue.services;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.julianomengue.classes.Foto;
import com.julianomengue.classes.User;
import com.julianomengue.repositories.FotoRepository;

@Service
public class FotoService {

	@Autowired
	private FotoRepository fotoRepo;

	@Autowired
	private UserService userService;

	public Foto binaryToString(Foto foto) {
		foto.setFotoString(Base64.getEncoder().encodeToString(foto.getFotobinary().getData()));
		return foto;
	}

	public Foto findById(String id) {
		return this.fotoRepo.findById(id).get();
	}

	public Foto insert(Foto foto) {
		return this.fotoRepo.insert(foto);
	}

	public void save(Foto foto) {
		this.fotoRepo.save(foto);
	}

	public void delete(Foto foto, String email) {
		this.deleteFotoFromUser(foto.getId(), email);
	}

	public Foto getFotoByIdFromUser(String id, String email) {
		Foto foto = new Foto();
		List<Foto> fotos = this.userService.getCurrentUser(email).getFotos();
		for (int i = 0; i < fotos.size(); i++) {
			if (fotos.get(i).getId().contentEquals(id)) {
				foto = fotos.get(i);
			}
		}
		return foto;
	}

	public void deleteFotoFromUser(String id, String email) {
		User user = new User();
		user = this.userService.findOne(email);
		for (int i = 0; i < user.getFotos().size(); i++) {
			if (user.getFotos().get(i).getId().contentEquals(id)) {
				user.getFotos().remove(i);
				this.userService.save(user);
			}
		}
	}

	public void saveEditedUserFoto(Foto foto, String email) {
		User user = new User();
		user = this.userService.getCurrentUser(email);
		for (int i = 0; i < user.getFotos().size(); i++) {
			if (user.getFotos().get(i).getId().contentEquals(foto.getId())) {
				user.getFotos().remove(i);
				user.getFotos().add(foto);
				this.userService.save(user);
			}
		}

	}

	public String addFoto(MultipartFile file, String email) throws Exception {
		Foto foto = new Foto();
		foto.setFotobinary(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
		foto.setSize(file.getSize() / 1024);
		foto.addOwners(email);
		foto.setTitle(file.getOriginalFilename());
		foto = this.fotoRepo.insert(foto);
		Foto newFoto = new Foto();
		newFoto.setId(foto.getId());
		newFoto.setTitle(foto.getTitle());
		newFoto.setSize(file.getSize() / 1024);
		User user = new User();
		user = this.userService.getCurrentUser(email);
		user.addFotos(newFoto);
		this.userService.save(user);
		return foto.getId();
	}

	public Foto addProfileFoto(MultipartFile file, String email) throws IOException {
		Foto foto = new Foto();
		foto.setFotobinary(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
		foto.setSize(file.getSize() / 1024);
		foto.addOwners(email);
		foto.setTitle(file.getOriginalFilename());
		return this.fotoRepo.insert(foto);
	}

}
