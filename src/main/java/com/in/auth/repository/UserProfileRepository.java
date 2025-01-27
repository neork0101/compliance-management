package com.in.auth.repository;



import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.in.security.models.UserProfile;

/**
 * Repository interface for User Profile operations.
 * Extends MongoRepository to provide CRUD operations.
 */
@Repository
public interface UserProfileRepository extends MongoRepository<UserProfile, String> {

	//custom method for onboarding layer signin
	UserProfile findByUserId(String userId);
}

