package com.in.auth.controller;

import java.util.HashSet;
import java.util.List;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.in.auth.dto.ErrorDetails;
import com.in.auth.dto.ResponseDto;
import com.in.auth.payload.request.LoginRequest;
import com.in.auth.payload.request.SignupRequest;
import com.in.auth.payload.response.JwtResponse;
import com.in.auth.payload.response.MessageResponse;
import com.in.auth.repository.RoleRepository;
import com.in.auth.repository.UserRepository;
import com.in.auth.service.PasswordResetService;
import com.in.auth.service.UserDetailsImpl;
import com.in.security.jwt.JwtUtils;
import com.in.security.models.ERole;
import com.in.security.models.Role;
import com.in.security.models.User;
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
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;
    
    @Autowired
    private PasswordResetService passwordResetService;


    /**
     * Authenticates a user and returns a JWT token.
     * 
     * @param loginRequest The login request containing username and password
     * @return ResponseEntity containing the JWT token and user details
     */
    @PostMapping("/signin")
    @Operation(summary = "Authenticate user and return JWT token",
               description = "Authenticates the user with the provided credentials and returns a JWT token in both the Authorization header and response body.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = JwtResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
                     content = @Content(mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorDetails.class)))
    })
    public ResponseEntity<ResponseDto> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        LOG.info("Start Method: authenticateUser");

        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // Set the authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user details
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Generate JWT token
        String jwtToken = jwtUtils.generateToken(userDetails);

        // Get user roles
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Create JwtResponse object
        JwtResponse jwtResponse = new JwtResponse(
            jwtToken,
            userDetails.getId(), 
            userDetails.getUsername(), 
            userDetails.getEmail(), 
            roles
        );
        
        LOG.info("User {} successfully authenticated", loginRequest.getUsername());
        LOG.debug("Generated JWT token for user {}: {}", loginRequest.getUsername(), jwtToken);


        LOG.info("End Method: authenticateUser");

        // Return ResponseEntity with JWT in header and body
        return ResponseEntity.ok()
            .header("Authorization", "Bearer " + jwtToken)
            .body(jwtResponse);
    }

    /**
     * Registers a new user.
     * 
     * @param signUpRequest The signup request containing user details
     * @return ResponseEntity with a success message or error details
     */
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
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(), 
                             signUpRequest.getEmail(),
                             encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
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
        }

        user.setRoles(roles);
        userRepository.save(user);

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
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        try {
            System.out.println("Received input request for password reset via email");
            String emailApiResponse = passwordResetService.initiatePasswordReset(email);
            return new ResponseEntity<>("Password reset link sent to your email. " + emailApiResponse, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            return new ResponseEntity<>("Error occurred while processing the request", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) {
        try {
            passwordResetService.resetPassword(token, newPassword);
            return new ResponseEntity<>("Password has been reset successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid or expired token", HttpStatus.BAD_REQUEST);
        }
    }
}