package com.julianomengue.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.julianomengue.classes.Audio;

public interface AudioRepository extends MongoRepository<Audio, String> {

}
