package com.in.auth.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.in.auth.dto.CommunicationResponse;
import com.in.auth.dto.ErrorDetails;
import com.in.auth.dto.ResponseDto;
import com.in.auth.payload.request.LoginRequest;
import com.in.auth.payload.request.SignupRequest;
import com.in.auth.payload.response.JwtResponse;
import com.in.auth.payload.response.MessageResponse;
import com.in.auth.repository.OnboardedUserRepository;
import com.in.auth.repository.RoleRepository;
import com.in.auth.repository.UserProfileRepository;
import com.in.auth.repository.UserRepository;
import com.in.auth.service.PasswordResetService;
import com.in.auth.service.UserDetailsImpl;
import com.in.auth.service.UserDetailsServiceImpl;
import com.in.security.jwt.JwtUtils;
import com.in.security.models.ERole;
import com.in.security.models.OnboardedUser;
import com.in.security.models.Organization;
import com.in.security.models.Role;
import com.in.security.models.User;
import com.in.security.models.UserProfile;
import com.in.security.util.AppConstants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/identitymanagement/api/auth")
@Tag(name = "Authentication Controller", description = "Endpoints for user authentication and registration")
public class AuthController {

	private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
    UserProfileRepository userProfileRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
    OnboardedUserRepository onboardedUserRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	PasswordResetService passwordResetService;

	@Autowired
	UserDetailsServiceImpl userDetailsServiceImpl;

	/**
	 * Authenticates a user and returns a JWT token.
	 *
	 * @param loginRequest The login request containing username and password
	 * @return ResponseEntity containing the JWT token and user details
	 */
	@PostMapping("/signin")
	@Operation(summary = "Authenticate user and return JWT token", description = "Authenticates the user with the provided credentials and returns a JWT token in both the Authorization header and response body.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully authenticated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponse.class))),
			@ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))) })
	public ResponseEntity<ResponseDto> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		LOG.info("Start Method: authenticateUser");

		// Authenticate the user
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		// Set the authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user details
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

     // Retrieve User from the repository
        User user = userRepository.findById(userDetails.getId()).orElse(null);

        // Retrieve UserProfile
        UserProfile userProfile = userProfileRepository.findByUserId(userDetails.getId());

        if (userProfile == null) {
            LOG.warn("UserProfile not found for user ID: {}", userDetails.getId());
        }

     // Retrieve Organization
        Organization organization = (user != null) ? user.getOrganization() : null;
        if (organization == null) {
            LOG.warn("Organization not found for user ID: {}", userDetails.getId());
        }

        // Check if user can skip 2FA
        if (userDetailsServiceImpl.skip2FA(userDetails)) {
            // Generate JWT token for allowed roles
            String jwtToken = jwtUtils.generateToken(userDetails);

            // Get user roles
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            // Create Response object with token
            JwtResponse jwtResponse = new JwtResponse(jwtToken, userDetails.getId(), userDetails.getUsername(),
                    userDetails.getEmail(), roles, userProfile,
                    organization);

            LOG.info("User {} successfully authenticated with direct JWT token", loginRequest.getUsername());

            // Return ResponseEntity with JWT in header and body
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + jwtToken)
                    .body(jwtResponse);
        } else {

        	// Get user roles
    		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
    				.collect(Collectors.toList());
            // User needs to complete 2FA
        	Map<String, Object> userData = new HashMap<>();
            userData.put("id", userDetails.getId());
            userData.put("username", userDetails.getUsername());
            userData.put("email", userDetails.getEmail());
            userData.put("roles", roles);

            CommunicationResponse<Map<String, Object>> response = new CommunicationResponse<>(
                "2FA_REQUIRED",
                "Two-factor authentication required",
                userData
            );

            JwtResponse jwtResponse = new JwtResponse(null, userDetails.getId(), userDetails.getUsername(),
                    userDetails.getEmail(), roles,userProfile,
                    organization);

            LOG.info("User {} requires 2FA authentication", loginRequest.getUsername());
            return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
        }
	}

	/**
	 * Registers a new user.
	 *
	 * @param signUpRequest The signup request containing user details
	 * @return ResponseEntity with a success message or error details
	 */
	@PostMapping("/signup")
	@Operation(summary = "Register a new user", description = "Creates a new user account with the provided details.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
			@ApiResponse(responseCode = "400", description = "Bad request, invalid input", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))) })
	public ResponseEntity<ResponseDto> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		LOG.info("Start Method: registerUser");

		// Check if username is already taken
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			LOG.info("Method: registerUser - Error: Username is already taken!");
			return ResponseEntity.badRequest()
					.body(new MessageResponse("Error: Username is already taken! " + signUpRequest.getUsername()));
		}

		// Check if email is already in use
		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			LOG.info("Method: registerUser - Error: Email is already taken! " + signUpRequest.getEmail());
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		// check if user email is available in onboarded_users collection
		OnboardedUser onboardedUser=onboardedUserRepository.findByEmail(signUpRequest.getEmail());
		if(onboardedUser==null)
		{
		    LOG.info("Method: registerUser - Error: Email is not available in onboarded users list:" + signUpRequest.getEmail());
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is not available in onboarded users list!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()));


		/*Set<String> strRoles = signUpRequest.getRoles();

		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException(AppConstants.ROLE_NOTFOUND));
			LOG.error("Error: User Role not found.");
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException(AppConstants.ROLE_NOTFOUND));
					LOG.error("Error: Admin Role not found.");
					roles.add(adminRole);
					break;
				case "mod":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new RuntimeException(AppConstants.ROLE_NOTFOUND));
					LOG.error("Error: Moderator Role not found.");
					roles.add(modRole);
					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException(AppConstants.ROLE_NOTFOUND));
					LOG.error("Error: User Role not found.");
					roles.add(userRole);
				}
			});
		}*/

		// retrieving roles and organization details from OnboardedUser collection and setting it to the user.
		user.setRoles(onboardedUser.getRoles());
		user.setOrganization(onboardedUser.getOrganization());
		user.setStatus("Active");
		User savedUser=userRepository.save(user);

		// Creating a user profile when user is created
		UserProfile userProfile=new UserProfile(null,savedUser.getId(),null,null,signUpRequest.getEmail(),null,null,signUpRequest.getUsername(),null,null,null,"Active");
		//UserProfile userProfile=new UserProfile(signUpRequest.getUsername(),"test","test",signUpRequest.getEmail(),"test","test",signUpRequest.getUsername(),"test","test");
        userProfileRepository.save(userProfile);

        // setting status of Onboarded user
           onboardedUser.setStatus("SignUp_Completed");
           onboardedUserRepository.save(onboardedUser);

		LOG.info("Method: registerUser - User registered successfully!");
		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}



	/**
	 * Handles forgot password requests by initiating the password reset process.
	 *
	 * @param email The email address of the user requesting password reset.
	 * @return ResponseEntity with status and message.
	 */
	@PostMapping("/forgot-password")
	@Operation(summary = "Initiate password reset", description = "Sends a password reset link to the user's email.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Password reset link sent successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommunicationResponse.class))),
			@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))) })
	public ResponseEntity<ResponseDto> forgotPassword(@RequestParam("email") String email) {
		try {
			LOG.info("Received password reset request for email: {}", email);

			// Initiate the password reset process
			String emailApiResponse = passwordResetService.initiatePasswordReset(email);

			// Create a response DTO with status, message, and data
			CommunicationResponse<String> response = new CommunicationResponse<>("SUCCESS",
					"Password reset link sent to your email.", emailApiResponse // You can include additional data if
																				// needed
			);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			LOG.error("Exception occurred while processing password reset request for email: {}", email, e);

			// Create an error response DTO with status code, message, and details
			ErrorDetails errorDetails = new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"Error occurred while processing the request", e.getMessage());

			return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Resets the user's password using the provided token and new password.
	 *
	 * @param token       The password reset token.
	 * @param newPassword The new password to set.
	 * @return ResponseEntity with status and message.
	 */
	@PostMapping("/reset-password")
	@Operation(summary = "Reset password", description = "Resets the user's password using the provided token.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Password reset successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommunicationResponse.class))),
			@ApiResponse(responseCode = "400", description = "Invalid or expired token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))) })
	public ResponseEntity<ResponseDto> resetPassword(@RequestParam("token") String token,
			@RequestParam("newPassword") String newPassword) {
		try {
			LOG.info("Password reset attempt with token: {}", token);

			// Reset the password using the token and new password
			passwordResetService.resetPassword(token, newPassword);

			// Create a response DTO with status and message
			CommunicationResponse<Void> response = new CommunicationResponse<>("SUCCESS",
					"Password has been reset successfully.");

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			LOG.error("Exception occurred while resetting password with token: {}", token, e);

			// Create an error response DTO with status code, message, and details
			ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST.value(), "Invalid or expired token",
					e.getMessage());

			return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
		}
	}


	/**
	 * Registers a new user.
	 *
	 * @param signUpRequest The signup request containing user details
	 * @return ResponseEntity with a success message or error details
	 */
	@PostMapping("/admin/signup")
	@Operation(summary = "Register a new user", description = "Creates a new user account with the provided details.")
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))),
	        @ApiResponse(responseCode = "400", description = "Bad request, invalid input", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDetails.class))) })
	public ResponseEntity<ResponseDto> registerUserAdmin(@Valid @RequestBody SignupRequest signUpRequest) {
	    LOG.info("Start Method: registerUser");

	    // Check if username is already taken
	    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
	        LOG.info("Method: registerUser - Error: Username is already taken!");
	        return ResponseEntity.badRequest()
	                .body(new MessageResponse("Error: Username is already taken! " + signUpRequest.getUsername()));
	    }

	    // Check if email is already in use
	    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
	        LOG.info("Method: registerUser - Error: Email is already taken! " + signUpRequest.getEmail());
	        return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
	    }

	    // Create new user's account
	    User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
	            encoder.encode(signUpRequest.getPassword()));

	    Set<String> strRoles = signUpRequest.getRoles();
	    Set<Role> roles = new HashSet<>();

	    Role superAdminRole = roleRepository.findByName(ERole.ROLE_SUPERADMIN)
                .orElseThrow(() -> new RuntimeException(AppConstants.ROLE_NOTFOUND));
        LOG.info("Adding SUPERADMIN role");
        roles.add(superAdminRole);

	    if (strRoles != null) {
	        strRoles.forEach(role -> {
	            switch (role.toLowerCase()) {  //Convert to lowercase for case-insensitive comparison
	            case "admin":
	                Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
	                        .orElseThrow(() -> new RuntimeException(AppConstants.ROLE_NOTFOUND));
	                LOG.info("Adding ADMIN role");
	                roles.add(adminRole);
	                break;
	            case "ROLE_INVESTIGATOR":
	                Role modRole = roleRepository.findByName(ERole.ROLE_INVESTIGATOR)
	                        .orElseThrow(() -> new RuntimeException(AppConstants.ROLE_NOTFOUND));
	                LOG.info("Adding MODERATOR role");
	                roles.add(modRole);
	                break;
	            default:
	                Role userRole = roleRepository.findByName(ERole.ROLE_USER)
	                        .orElseThrow(() -> new RuntimeException(AppConstants.ROLE_NOTFOUND));
	                LOG.info("Adding USER role for unrecognized role: " + role);
	                roles.add(userRole);
	            }
	        });
	    }

	    user.setRoles(roles);
	    userRepository.save(user);

	    LOG.info("Method: registerUser - User registered successfully with roles: {}",
	             roles.stream().map(r -> r.getName().name()).collect(Collectors.joining(", ")));
	    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	@GetMapping("/roles")
	public ResponseEntity<?> checkRoles() {
	    List<Role> roles = roleRepository.findAll();
	    return ResponseEntity.ok(roles.stream()
	        .map(r -> r.getName().name())
	        .collect(Collectors.toList()));
	}
}