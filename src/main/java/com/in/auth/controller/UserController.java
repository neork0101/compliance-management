package com.in.auth.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.in.auth.repository.RoleRepository;
import com.in.auth.repository.UserRepository;
import com.in.security.models.Role;
import com.in.security.models.User;

@RestController
@RequestMapping("identitymanagement/api/user")
public class UserController {
	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired(required = true)
	private UserRepository userRepository;

	@Autowired(required = true)
	private RoleRepository roleRepository;

	@GetMapping("/{id}")
	// @PreAuthorize("hasRole('MODERATOR')")
	public Optional<User> getUserById(@PathVariable String id) {
		log.info("Start getUserById: {}");
		return userRepository.findById(id);
	}

	@GetMapping("/search")
	// @PreAuthorize("hasRole('ADMIN')")
	public List<User> getAllUser() {
		log.info("Start getAllUser: {}");
		return userRepository.findAll();
	}

	@DeleteMapping("/{id}")
	// @PreAuthorize("hasRole('ADMIN')")
	public String deleteUser(@PathVariable String id) {
		log.info("Start deleteUser: {}");
		userRepository.deleteById(id);
		log.info("Success deleteUser:");
		return "User Deletion Successful";
	}

	@GetMapping("/roles")
	// @PreAuthorize("hasRole('ADMIN')")
	public List<Role> getAllRole() {
		log.info("Start getAllRole: {}");
		return roleRepository.findAll();
	}

	@DeleteMapping("/role/{id}")
	// @PreAuthorize("hasRole('ADMIN')")
	public String deleteRole(@PathVariable String id) {
		log.info("Start deleteRole: {}");
		roleRepository.deleteById(id);
		log.info("Success deleteRole:");
		return "Role Deletion Successful";
	}

	@PutMapping("/{id}")
	// @PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> updateUser(@PathVariable String id, @RequestBody User updatedUser) {
		log.info("Start updateUser: {}", id);

		return userRepository.findById(id).map(user -> {

			user.setUsername(updatedUser.getUsername());
			// user.setEmail(updatedUser.getEmail());
			//user.setStatus("Active");
			if (updatedUser.getRoles() != null) {
				user.setRoles(updatedUser.getRoles());
			}

			if (updatedUser.getOrganization() != null) {
				user.setOrganization(updatedUser.getOrganization());
			}
			// Update any other relevant fields here
			user = userRepository.save(user);
			log.info("Success updateUser: {}", id);
			return ResponseEntity.ok().body("Successfully updated");
		}).orElseGet(() -> {
			log.warn("User not found: {}", id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

		});
	}

}
