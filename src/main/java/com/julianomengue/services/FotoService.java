package com.julianomengue.services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

import javax.imageio.ImageIO;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.julianomengue.classes.Foto;
import com.julianomengue.classes.User;
import com.julianomengue.repositories.FotoRepository;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class FotoService {

	@Autowired
	private FotoRepository fotoRepo;

	@Autowired
	private UserService userService;

	@Autowired
	private MongoTemplate mongoTemplate;

	public Foto binaryToString(Foto foto) {
		String plus = "data:image/png;base64,";
		foto.setFotoString(plus + Base64.getEncoder().encodeToString(foto.getFotobinary().getData()));
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

	public List<Foto> findAll() {
		return this.findAll();
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
		byte[] Byte = this.resizeImage(file);
		Foto foto = new Foto(new Binary(BsonBinarySubType.BINARY, Byte), Byte.length / 1024, email,
				file.getOriginalFilename());
		foto = this.fotoRepo.insert(foto);
		Foto newFoto = new Foto(foto.getId(), foto.getTitle(), foto.getOwners(), foto.getSize());
		User user = this.userService.getCurrentUser(email);
		user.addFotos(newFoto);
		this.userService.save(user);
		return foto.getId();
	}

	public String addFotoProfile(MultipartFile file) throws IOException {
		Foto foto = new Foto();
		byte[] Byte = this.resizeImage(file);
		foto.setFotobinary(new Binary(BsonBinarySubType.BINARY, Byte));
		foto.setTitle("PROFILE_FOTO");
		foto = this.fotoRepo.insert(foto);
		return foto.getId();
	}

	public byte[] resizeImage(MultipartFile file) throws IOException {
		BufferedImage outputImage = Scalr.resize(Thumbnails.of(file.getInputStream()).scale(1).asBufferedImage(), 500);
		File outputfile = new File("image.png");
		ImageIO.write(outputImage, "png", outputfile);
		byte[] bytes = Files.readAllBytes(Paths.get("image.png"));
		return bytes;
	}

	public void deleteFotosWithoutOwners() {
		List<Foto> fotos = getFotos();
		for (int i = 0; i < fotos.size(); i++) {
			if (fotos.get(i).getId() != "610514c386d1db45a6de1c17") {
				if (!fotos.get(i).getTitle().contentEquals("PROFILE_FOTO")) {
					if (fotos.get(i).getOwners().size() == 0) {
						this.fotoRepo.delete(fotos.get(i));
					}
				}
			}
		}
	}

	public List<Foto> getFotos() {
		Query query = new Query();
		query.fields().include("_id", "title", "owners", "size");
		List<Foto> list = mongoTemplate.find(query, Foto.class);
		return list;
	}

}
