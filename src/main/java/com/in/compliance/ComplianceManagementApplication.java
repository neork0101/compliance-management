package com.in.compliance;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.in.compliance.models.ERole;
import com.in.compliance.models.Role;
import com.in.compliance.repository.RoleRepository;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
//@EnableMongoRepositories("com.in.compliance.repository")
//@ComponentScan(basePackages = "com.in.compliance") 
public class ComplianceManagementApplication {

	private static final Logger LOG = LoggerFactory.getLogger(ComplianceManagementApplication.class);
	
	@Autowired(required = true)
	RoleRepository roleRepo;
	
	public static void main(String[] args) {
		SpringApplication.run(ComplianceManagementApplication.class, args);
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
