package com.in.auth.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.in.auth.dto.OrganizationDTO;
import com.in.auth.service.OrganizationService;
import com.in.security.models.OnboardedUser;
import com.in.security.models.Organization;

/**
 * Controller for managing Organizations and Onboarded Users.
 */
@RestController
@RequestMapping("/identitymanagement/api/organization")
public class OrganizationController {

    @Autowired
    private OrganizationService organizationService;

    /**
     * Save a new Organization along with its associated OnboardedUser.
     *
     * @param organization The Organization object to be saved.
     * @return ResponseEntity containing the saved Organization.
     */
    @PostMapping
    public ResponseEntity<?> createOrganization(@RequestBody OrganizationDTO organization) {
        try {
            Organization savedOrganization = organizationService.saveOrganizationWithDependencies(organization);
            return new ResponseEntity<>(savedOrganization, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error saving organization: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieve all Organizations or a specific Organization by ID.
     *
     * @param id Optional RequestParam to filter by Organization ID.
     * @return ResponseEntity containing the Organization(s).
     */
    @GetMapping
    public ResponseEntity<?> getOrganizations(@RequestParam(value = "id", required = false) String id) {
        try {
            if (id != null) {
                Optional<Organization> organization = organizationService.getOrganizationById(id);
                if (organization.isPresent()) {
                    return new ResponseEntity<>(organization.get(), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Organization not found with ID: " + id, HttpStatus.NOT_FOUND);
                }
            } else {
                List<Organization> organizations = organizationService.getAllOrganizations();
                return new ResponseEntity<>(organizations, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving organizations: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Retrieve all Onboarded Users (within the organization context).
     *
     * @return List of all saved Onboarded Users.
     */
    @GetMapping("/onboardedUsers")
    public ResponseEntity<?> getAllOnboardedUsers() {
        try {
            List<OnboardedUser> onboardedUsers = organizationService.getAllOnboardedUsers();
            return new ResponseEntity<>(onboardedUsers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving onboarded users: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
