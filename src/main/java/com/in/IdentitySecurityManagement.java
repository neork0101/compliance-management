package com.in;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.in.security.models.ERole;
import com.in.security.models.Role;
import com.in.auth.repository.RoleRepository;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableDiscoveryClient
//@EnableMongoRepositories("com.in.compliance.repository")
//@ComponentScan(basePackages = "com.in.compliance") 
public class IdentitySecurityManagement {

	private static final Logger LOG = LoggerFactory.getLogger(IdentitySecurityManagement.class);
	
	@Autowired(required = true)
	RoleRepository roleRepo;
	
	public static void main(String[] args) {
		SpringApplication.run(IdentitySecurityManagement	.class, args);
	}
	
	@PostConstruct
	public void seeder() {

		List<Role> adminRole = roleRepo.findAll();

		if (!adminRole.isEmpty()) {
			
			//adminRole.stream().
			LOG.info("Volunteer Management App started - Roles "+adminRole.size());

		} else {
			List<Role> roles = new ArrayList<>();
			
			Role roleadmin = new Role();
			roleadmin.setName(ERole.ROLE_ADMIN);
			roles.add(roleadmin);
			
			Role roleMod = new Role();
			roleMod.setName(ERole.ROLE_MODERATOR);
			roles.add(roleMod);
			// roleRepo.save(roleMod);

			Role roleUser = new Role();
			roleUser.setName(ERole.ROLE_USER);
			roles.add(roleUser);
			roleRepo.saveAll(roles);
		}

	}

}
