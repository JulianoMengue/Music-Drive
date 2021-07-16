package com.julianomengue.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.julianomengue.classes.User;

public interface UserRepository extends MongoRepository<User, String> {

}
