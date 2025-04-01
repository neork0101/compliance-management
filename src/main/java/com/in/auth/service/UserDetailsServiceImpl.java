package com.in.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.in.auth.repository.UserRepository;
import com.in.security.config.SecurityProperties;
import com.in.security.models.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;



  @Autowired
  private SecurityProperties securityProperties;


  /**
   *
   */
  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      if (username == null) {
          throw new UsernameNotFoundException("User Not Found with username: null");
      }

      User user = userRepository.findByUsername(username)
          .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

      return UserDetailsImpl.build(user);
  }

  /**
   *
   * @param email
   * @return
   * @throws UsernameNotFoundException
   */
  @Transactional
  public User loadUserByEmail(String email) throws UsernameNotFoundException {
      if (email == null) {
          throw new UsernameNotFoundException("User Not Found with email: null");
      }

      User user = userRepository.findByEmail(email);

      return user;
  }

  public boolean skip2FA(UserDetails userDetails) {

	    // Extract authorities from userDetails and check for intersection with allowAccess
	    return userDetails.getAuthorities().stream()
	            .map(item -> item.getAuthority())
	            .anyMatch(securityProperties.getAllowedRoles()::contains);
	}

}
