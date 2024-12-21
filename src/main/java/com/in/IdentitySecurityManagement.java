package com.in;

import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.in.auth.repository.RoleRepository;
import com.in.security.models.ERole;
import com.in.security.models.Role;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableDiscoveryClient
//@EnableMongoRepositories("com.in.compliance.repository")
//@ComponentScan(basePackages = "com.in.compliance") 
public class IdentitySecurityManagement {

	private static final Logger LOG = LoggerFactory.getLogger(IdentitySecurityManagement.class);
	
	@Autowired(required = true)
	RoleRepository roleRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(IdentitySecurityManagement	.class, args);
	}
	
	/**
     * Method to ensure all roles are present in the database.
     * Adds only the missing roles.
     */
    @PostConstruct
    public void initializeRoles() {
        Arrays.stream(ERole.values()).forEach(roleEnum -> {
            // Check if the role already exists
            Optional<Role> existingRole = roleRepository.findByName(roleEnum);
            if (existingRole.isEmpty()) {
                // Save the missing role
                Role role = new Role(roleEnum);
                roleRepository.save(role);
                System.out.println("Role added to DB: " + roleEnum);
            } else {
                System.out.println("Role already exists in DB: " + roleEnum);
            }
        });
    }

}
