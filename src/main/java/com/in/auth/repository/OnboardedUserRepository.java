package com.in.auth.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.in.security.models.OnboardedUser;

public interface OnboardedUserRepository extends MongoRepository<OnboardedUser, String> {
    // Find a user by email
    OnboardedUser findByEmail(String email);


}