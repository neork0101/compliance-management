package com.in.auth.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.in.auth.repository.UserRepository;
import com.in.security.models.User;

@RestController
@RequestMapping("identitymanagement/api/user")
public class UserController {
	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired(required = true)
	 private UserRepository userRepository;
	
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

}
