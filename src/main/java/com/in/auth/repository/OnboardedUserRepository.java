package com.in.auth.repository;

import com.in.security.models.OnboardedUser;
import com.in.security.models.User;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface OnboardedUserRepository extends MongoRepository<OnboardedUser, String> {
    // Find a user by email
    OnboardedUser findByEmail(String email);
    
    
}