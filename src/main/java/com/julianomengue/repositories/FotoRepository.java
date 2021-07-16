package com.julianomengue.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.julianomengue.classes.Foto;

public interface FotoRepository extends MongoRepository<Foto, String> {
}
