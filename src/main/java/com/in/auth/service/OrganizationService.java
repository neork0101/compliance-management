package com.in.auth.service;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.in.auth.dto.OrganizationDTO;
import com.in.auth.repository.OnboardedUserRepository;
import com.in.auth.repository.OrganizationRepository;
import com.in.auth.repository.RoleRepository;
import com.in.auth.repository.UserRepository;
import com.in.security.models.ERole;
import com.in.security.models.OnboardedUser;
import com.in.security.models.Organization;
import com.in.security.models.Role;
import com.in.security.util.AppConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * Service class for handling business logic related to Organizations and Onboarded Users.
 */
@Service
@Slf4j
public class OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OnboardedUserRepository onboardedUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
	UserRepository userRepository;

    // Save an Organization and its associated OnboardedUser
    public Organization saveOrganizationWithDependencies(OrganizationDTO requestOrg) {

    	Organization organization = new Organization();
    	organization.setAcronym(requestOrg.getAcronym());
    	organization.setDepartments(requestOrg.getDepartments());
    	organization.setLocation(requestOrg.getLocation());
    	organization.setModules(requestOrg.getModules());
    	organization.setName(requestOrg.getName());
    	organization.setSubscriptionsCount(requestOrg.getSubscriptionsCount());
    	organization.setType(requestOrg.getType());

        Organization savedOrganization = organizationRepository.save(organization);

        if (requestOrg.getOnboardedUsers() != null) {

        	requestOrg.getOnboardedUsers().forEach(user -> {

        		OnboardedUser existingUser= onboardedUserRepository.findByEmail(user.getEmail());
        		// Check if email is already in use
        		if (existingUser != null) {
        			if(existingUser.getStatus().equalsIgnoreCase("Signup_Pending")) {
						log.info("Method: registerUser - Error: Email is already taken! " + existingUser.getEmail() + " status "+existingUser.getStatus());
					} else {
						log.info("Method: registerUser - Error: Email is already sign up completed! " + existingUser.getEmail() + " status "+existingUser.getStatus());
					}

        		} else {
                    OnboardedUser  onboardedUser= new OnboardedUser();
                    onboardedUser.setEmail(user.getEmail());
                    onboardedUser.setStatus("Signup_Pending");
                    onboardedUser.setOrganization(savedOrganization);
                    onboardedUser.setRoles(getRoles(user.getRoles()));
                    OnboardedUser savedUser = onboardedUserRepository.save(onboardedUser);

                    savedOrganization.getOnboardedUsers().add(savedUser);

        		}

        	});
        }

        return savedOrganization;
    }

    private Set<Role> getRoles(Set<String> strRoles) {
    	Set<Role> roles = new HashSet<>();

    	strRoles.forEach(role -> {
			switch (role) {

			case "ROLE_SUPERADMIN":
				Role superAdminRole = roleRepository.findByName(ERole.ROLE_SUPERADMIN)
						.orElseThrow(() -> new RuntimeException(AppConstants.ROLE_NOTFOUND));
				log.error("Error: Admin Role not found.");
				roles.add(superAdminRole);
				break;

			case "ROLE_ADMIN":
				Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
						.orElseThrow(() -> new RuntimeException(AppConstants.ROLE_NOTFOUND));
				log.error("Error: Admin Role not found.");
				roles.add(adminRole);
				break;
			case "ROLE_INVESTIGATOR":
				Role modRole = roleRepository.findByName(ERole.ROLE_INVESTIGATOR)
						.orElseThrow(() -> new RuntimeException(AppConstants.ROLE_NOTFOUND));
				log.error("Error: Moderator Role not found.");
				roles.add(modRole);
				break;
			default:
				Role userRole = roleRepository.findByName(ERole.ROLE_USER)
						.orElseThrow(() -> new RuntimeException(AppConstants.ROLE_NOTFOUND));
				log.error("Error: User Role not found.");
				roles.add(userRole);
			}
		});

    	return roles;
    }

    // Retrieve a specific Organization by ID
    public Optional<Organization> getOrganizationById(String id) {
        return organizationRepository.findById(id);
    }

    // Retrieve all Organizations
    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    // Retrieve all Onboarded Users
    public List<OnboardedUser> getAllOnboardedUsers() {
        return onboardedUserRepository.findAll();
    }
}
