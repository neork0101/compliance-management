package com.in.auth.controller;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.in.auth.service.UserProfileService;
import com.in.security.models.UserProfile;

/**
 * REST Controller for User Profile Management.
 * Handles HTTP requests related to User Profiles.
 */
@RestController
@RequestMapping("identitymanagement/api/user-profile")

public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
    private final UserProfileService userProfileService;

    /**
     * Constructor-based dependency injection for UserProfileService.
     * @param userProfileService the service handling user profile operations
     */
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * Creates a new User Profile.
     * @param userProfile the user profile data
     * @return the created user profile
     */
    @PostMapping
    public ResponseEntity<UserProfile> createUserProfile(@RequestBody UserProfile userProfile) {
        return ResponseEntity.ok(userProfileService.createUserProfile(userProfile));
    }

    /**
     * Retrieves a list of all User Profiles.
     * @return list of user profiles
     */
    @GetMapping
    public ResponseEntity<List<UserProfile>> getAllUserProfiles() {
        return ResponseEntity.ok(userProfileService.getAllUserProfiles());
    }

    /**
     * Retrieves a User Profile by its ID.
     * @param id the ID of the user profile
     * @return the user profile with the specified ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> getUserProfileById(@PathVariable String id) {
        return ResponseEntity.ok(userProfileService.getUserProfileById(id));
    }

    /**
     * Updates an existing User Profile by ID.
     * @param id the ID of the user profile to update
     * @param userProfile the updated user profile data
     * @return the updated user profile
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserProfile> updateUserProfile(@PathVariable String id, @RequestBody UserProfile userProfile) {
        logger.info("User Profile update", id);
        return ResponseEntity.ok(userProfileService.updateUserProfile(id,userProfile));
    }

    /**
     * Deletes a User Profile by ID.
     * @param id the ID of the user profile to delete
     * @return response entity indicating the status of the operation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable String id) {
        userProfileService.deleteUserProfile(id);
        return ResponseEntity.noContent().build();
    }
}