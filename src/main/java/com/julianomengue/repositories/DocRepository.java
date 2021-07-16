package com.julianomengue.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.julianomengue.classes.Doc;

public interface DocRepository extends MongoRepository<Doc, String> {
}
