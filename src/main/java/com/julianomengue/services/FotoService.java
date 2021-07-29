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
		Foto foto = new Foto();
		byte[] Byte = this.resizeImage(file);
		foto.setFotobinary(new Binary(BsonBinarySubType.BINARY, Byte));
		int i = Byte.length;
		Long t = (long) i;
		foto.setSize(t / 1024);
		foto.addOwners(email);
		foto.setTitle(file.getOriginalFilename());
		foto = this.fotoRepo.insert(foto);
		Foto newFoto = new Foto();
		newFoto.setId(foto.getId());
		newFoto.setTitle(foto.getTitle());
		newFoto.setSize(foto.getSize());
		newFoto.addOwners(email);
		User user = new User();
		user = this.userService.getCurrentUser(email);
		user.addFotos(newFoto);
		this.userService.save(user);
		return foto.getId();
	}

	public Foto addProfileFoto(MultipartFile file, String email) throws IOException {
		Foto foto = new Foto();
		byte[] Byte = this.resizeImage(file);
		foto.setFotobinary(new Binary(BsonBinarySubType.BINARY, Byte));
		int i = Byte.length;
		Long t = (long) i;
		foto.setSize(t / 1024);
		foto.addOwners(email);
		foto.setTitle(file.getOriginalFilename());
		return this.fotoRepo.insert(foto);
	}

	public byte[] resizeImage(MultipartFile file) throws IOException {
		BufferedImage image = Thumbnails.of(file.getInputStream()).scale(1).asBufferedImage();
		BufferedImage outputImage = Scalr.resize(image, 550);
		File outputfile = new File("image.png");
		ImageIO.write(outputImage, "png", outputfile);
		byte[] bytes = Files.readAllBytes(Paths.get("image.png"));
		return bytes;
	}

}
