package com.in.auth.service;



import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.in.auth.repository.UserProfileRepository;
import com.in.security.models.UserProfile;

/**
 * Service class for User Profile Management.
 * Contains business logic for handling user profiles.
 */
@Service
public class UserProfileService {

	private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    private final UserProfileRepository userProfileRepository;

    /**
     * Constructor-based dependency injection for UserProfileRepository.
     * @param userProfileRepository the repository handling database operations
     */
    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    /**
     * Creates a new User Profile.
     * @param userProfile the user profile data
     * @return the created user profile
     */
    public UserProfile createUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    /**
     * Retrieves all User Profiles from the database.
     * @return list of user profiles
     */
    public List<UserProfile> getAllUserProfiles() {
        return userProfileRepository.findAll();
    }

    /**
     * Retrieves a User Profile by its ID.
     * @param id the ID of the user profile
     * @return the user profile with the specified ID, or null if not found
     */
    public UserProfile getUserProfileById(String id) {
        Optional<UserProfile> userProfile = userProfileRepository.findById(id);
        return userProfile.orElse(null);
    }

    /**
     * Updates an existing User Profile.
     * @param id the ID of the user profile to update
     * @param updatedUserProfile the updated user profile data
     * @return the updated user profile, or null if not found
     */
    public UserProfile updateUserProfile(String id, UserProfile updatedUserProfile) {
        // Check if the user profile with the given ID exists
        if (userProfileRepository.existsById(id)) {
            logger.info("ID EXISTS!!!!!!!!!!!Updating user profile with ID: {}", id);
            // Retrieve the existing user profile from the database
            UserProfile existingUserProfile = userProfileRepository.findById(id).get();
           // Update the fields of the existing user profile only if the new values are not null
            if (updatedUserProfile.getFirstName() != null) {
                existingUserProfile.setFirstName(updatedUserProfile.getFirstName());
            }
            if (updatedUserProfile.getLastName() != null) {
                existingUserProfile.setLastName(updatedUserProfile.getLastName());
            }
            /*if (updatedUserProfile.getEmail() != null) {
                existingUserProfile.setEmail(updatedUserProfile.getEmail());
            }*/
            if (updatedUserProfile.getPhoneNumber() != null) {
                existingUserProfile.setPhoneNumber(updatedUserProfile.getPhoneNumber());
            }
            if (updatedUserProfile.getAddress() != null) {
                existingUserProfile.setAddress(updatedUserProfile.getAddress());
            }
            if (updatedUserProfile.getInterests() != null) {
                existingUserProfile.setInterests(updatedUserProfile.getInterests());
            }
            if (updatedUserProfile.getProfilePicture() != null) {
                existingUserProfile.setProfilePicture(updatedUserProfile.getProfilePicture());
            }
            // Save the updated user profile
            return userProfileRepository.save(existingUserProfile);
        }

        return null;
    }

    /**
     * Deletes a User Profile by its ID.
     * @param id the ID of the user profile to delete
     */
    public void deleteUserProfile(String id) {
        userProfileRepository.deleteById(id);
    }
}

