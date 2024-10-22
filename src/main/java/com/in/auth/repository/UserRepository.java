package com.in.auth.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.in.security.models.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);
  
  // Find a user by their email address
  User findByEmail(String email);

  // Find a user by their password reset token
  User findByResetToken(String resetToken);
}
