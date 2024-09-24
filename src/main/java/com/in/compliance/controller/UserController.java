package com.in.compliance.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.in.compliance.models.User;
import com.in.compliance.repository.UserRepository;

@RestController
@RequestMapping("compliancemanagement/api/user")
public class UserController {
	private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
	@Autowired(required = true)
	  UserRepository userRepository;
	
	 @GetMapping("/{id}")
	 // @PreAuthorize("hasRole('MODERATOR')")
	  public Optional<User> getUserById(@PathVariable String id) {
		 
	    return userRepository.findById(id);
	  }

}
