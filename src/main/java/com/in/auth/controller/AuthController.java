package com.in.auth.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.in.security.util.AppConstants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse; // Add this
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content; // Add this
import io.swagger.v3.oas.annotations.media.Schema;  // Add this
import io.swagger.v3.oas.annotations.tags.Tag;

import com.in.auth.dto.ErrorDetails;
//import com.in.compliance.dto.ResponseDto;
import com.in.auth.dto.ResponseDto;
import com.in.auth.payload.request.LoginRequest;
import com.in.auth.payload.request.SignupRequest;
import com.in.auth.payload.response.MessageResponse;
import com.in.auth.payload.response.UserInfoResponse;
import com.in.auth.repository.RoleRepository;
import com.in.auth.repository.UserRepository;
import com.in.auth.service.UserDetailsImpl;

//import com.in.compliance.models.Role;
//import com.in.compliance.models.User;
//import com.in.compliance.payload.request.LoginRequest;
//import com.in.compliance.payload.request.SignupRequest;
//import com.in.compliance.payload.response.MessageResponse;
//import com.in.compliance.payload.response.UserInfoResponse;
//import com.in.compliance.repository.RoleRepository;
//import com.in.compliance.repository.UserRepository;
//import com.in.compliance.service.UserDetailsImpl;
import com.in.security.jwt.JwtUtils;
import com.in.security.models.ERole;
import com.in.security.models.Role;
import com.in.security.models.User;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("securitymanagement/api/auth")
@Tag(name = "Authentication Controller", description = "Endpoints for user authentication and registration")
public class AuthController {

	private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

	@Autowired(required = true)
	private AuthenticationManager authenticationManager;

	@Autowired(required = true)
	private UserRepository userRepository;

	@Autowired(required = true)
	private RoleRepository roleRepository;

	@Autowired(required = true)
	private PasswordEncoder encoder;

	@Autowired(required = true)
	private JwtUtils jwtUtils;

	@PostMapping("/signin")
    @Operation(summary = "Authenticate user and return JWT token",
               description = "Authenticates the user with the provided credentials and returns a JWT token in the Authorization header.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = UserInfoResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorDetails.class)))
    })
	public ResponseEntity<ResponseDto> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		LOG.info("Start Method:authenticateUser");
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		// for cookie approach
		// ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
		String jwtToken = jwtUtils.generateToken(userDetails);

		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).toList();

		// USed for cookie approach
		/*
		 * return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
		 * jwtCookie.toString()) .body(new UserInfoResponse(userDetails.getId(),
		 * userDetails.getUsername(), userDetails.getEmail(), roles));
		 */

		LOG.info("End Method:authenticateUser");

		return ResponseEntity.ok().header("Authorization", jwtToken).body(
				new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
	}

	@PostMapping("/signup")
    @Operation(summary = "Register a new user",
               description = "Creates a new user account with the provided details.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = MessageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad request, invalid input",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorDetails.class)))
    })
	public ResponseEntity<ResponseDto> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

		LOG.info("Start Method:registerUser");
		if (userRepository.existsByUsername(signUpRequest.getUsername()).booleanValue()) {
			LOG.info("Method:registerUser -Error: Username is already taken! ");
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken! "+signUpRequest.getUsername()));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			LOG.info("Method:registerUser -Error: Email is already taken! "+signUpRequest.getEmail());
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create the new user's account
		LOG.info("Method:registerUser - Create the new user's account ");
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException(AppConstants.ROLE_NOTFOUND));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException(AppConstants.ROLE_NOTFOUND));
					roles.add(adminRole);

					break;
				case "mod":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new RuntimeException(AppConstants.ROLE_NOTFOUND));
					roles.add(modRole);

					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException(AppConstants.ROLE_NOTFOUND));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);
		LOG.info("Method:registerUser -User registered successfully!");
		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
}
