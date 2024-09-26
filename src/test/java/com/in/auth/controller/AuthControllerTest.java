package com.in.auth.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.in.auth.payload.request.LoginRequest;
import com.in.auth.payload.request.SignupRequest;
import com.in.auth.repository.RoleRepository;
import com.in.auth.repository.UserRepository;
import com.in.auth.service.UserDetailsImpl;
import com.in.security.jwt.JwtUtils;
import com.in.security.models.ERole;
import com.in.security.models.Role;
import com.in.security.models.User;
import com.in.auth.test.TestResultLoggerExtension;

/**
 * Comprehensive test class for the AuthController.
 * This class tests the authentication and registration endpoints.
 */
@SpringBootTest 
@AutoConfigureMockMvc // Automatically configures MockMvc
@ExtendWith(TestResultLoggerExtension.class) // Extends the test with our custom TestResultLoggerExtension
public class AuthControllerTest {

    // Logger for this test class
    private static final Logger logger = LoggerFactory.getLogger(AuthControllerTest.class);

    @Autowired
    private MockMvc mockMvc; // MockMvc is used to perform and verify HTTP requests and responses

    @Autowired
    private ObjectMapper objectMapper; // ObjectMapper is used to convert Java objects to JSON and vice versa

    // Mock beans are used to simulate the behavior of these components without actually invoking them
    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private PasswordEncoder encoder;

    @MockBean
    private JwtUtils jwtUtils;

    // These objects will be used across multiple tests
    private LoginRequest validLoginRequest;
    private SignupRequest validSignupRequest;

    /**
     * Set up test data before each test method.
     */
    @BeforeEach
    public void setup() {
        // Initialize a valid login request
        validLoginRequest = new LoginRequest("testuser", "testpassword");

        // Initialize a valid signup request
        validSignupRequest = new SignupRequest("newuser", "newuser@example.com", new HashSet<>(Collections.singletonList("user")));
        validSignupRequest.setPassword("newpassword");
    }

    /**
     * Test successful user authentication.
     * This test verifies that a user can successfully authenticate and receive a JWT token.
     */
    @Test
    @DisplayName("Authenticate User - Success")
    public void testAuthenticateUser_Success() throws Exception {
        // Arrange: Set up the test scenario
        UserDetailsImpl userDetails = new UserDetailsImpl("1", "testuser", "test@example.com", "testpassword", Collections.emptyList());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        
        // Mock the behavior of the authentication manager and JWT utils
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateToken(any(UserDetailsImpl.class))).thenReturn("test.jwt.token");

        // Act and Assert: Perform the request and verify the response
        mockMvc.perform(post("/securitymanagement/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk()) // Expect a 200 OK status
                .andExpect(header().string("Authorization", "test.jwt.token")) // Verify the JWT token in the header
                .andExpect(jsonPath("$.username").value("testuser")) // Verify the username in the response body
                .andExpect(jsonPath("$.email").value("test@example.com")); // Verify the email in the response body

        // Verify that the authentication manager was called with the correct credentials
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(validLoginRequest.getUsername(), validLoginRequest.getPassword()));
    }

    /**
     * Test authentication failure due to invalid credentials.
     * This test verifies that the system correctly handles authentication attempts with invalid credentials.
     */
    @Test
    @DisplayName("Authenticate User - Invalid Credentials")
    public void testAuthenticateUser_InvalidCredentials() throws Exception {
        // Arrange: Set up the test scenario for invalid credentials
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act and Assert: Perform the request with invalid credentials and verify the response
        mockMvc.perform(post("/securitymanagement/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isUnauthorized()); // Expect a 401 Unauthorized status

        // Verify that the authentication manager was called with the provided credentials
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(validLoginRequest.getUsername(), validLoginRequest.getPassword()));
    }

    /**
     * Test successful user registration.
     * This test verifies that a new user can be successfully registered in the system.
     */
    @Test
    @DisplayName("Register User - Success")
    public void testRegisterUser_Success() throws Exception {
        // Arrange: Set up the test scenario for successful user registration
        when(userRepository.existsByUsername(validSignupRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(validSignupRequest.getEmail())).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(new Role(ERole.ROLE_USER)));
        when(encoder.encode(validSignupRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User(validSignupRequest.getUsername(), validSignupRequest.getEmail(), "encodedPassword"));

        // Act and Assert: Perform the signup request and verify the response
        mockMvc.perform(post("/securitymanagement/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSignupRequest)))
                .andExpect(status().isOk()) // Expect a 200 OK status
                .andExpect(jsonPath("$.message").value("User registered successfully!")); // Verify the success message

        // Verify that the user repository was called to save the new user
        verify(userRepository).save(any(User.class));
    }

    /**
     * Test registration failure due to username already taken.
     * This test verifies that the system correctly handles registration attempts with an existing username.
     */
    @Test
    @DisplayName("Register User - Username Already Taken")
    public void testRegisterUser_UsernameAlreadyTaken() throws Exception {
        // Arrange: Set up the test scenario for a username that's already taken
        when(userRepository.existsByUsername(validSignupRequest.getUsername())).thenReturn(true);

        // Act and Assert: Perform the signup request and verify the response
        mockMvc.perform(post("/securitymanagement/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSignupRequest)))
                .andExpect(status().isBadRequest()) // Expect a 400 Bad Request status
                .andExpect(jsonPath("$.message").value("Error: Username is already taken! " + validSignupRequest.getUsername()));

        // Verify that the user repository was not called to save the user
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test registration failure due to email already in use.
     * This test verifies that the system correctly handles registration attempts with an existing email.
     */
    @Test
    @DisplayName("Register User - Email Already in Use")
    public void testRegisterUser_EmailAlreadyInUse() throws Exception {
        // Arrange: Set up the test scenario for an email that's already in use
        when(userRepository.existsByUsername(validSignupRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(validSignupRequest.getEmail())).thenReturn(true);

        // Act and Assert: Perform the signup request and verify the response
        mockMvc.perform(post("/securitymanagement/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSignupRequest)))
                .andExpect(status().isBadRequest()) // Expect a 400 Bad Request status
                .andExpect(jsonPath("$.message").value("Error: Email is already in use!"));

        // Verify that the user repository was not called to save the user
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test registration failure due to role not found.
     * This test verifies that the system correctly handles registration attempts when the default role is not found.
     */
    @Test
    @DisplayName("Register User - Role Not Found")
    public void testRegisterUser_RoleNotFound() throws Exception {
        // Arrange: Set up the test scenario where the user role is not found
        when(userRepository.existsByUsername(validSignupRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(validSignupRequest.getEmail())).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.empty());

        // Act and Assert: Perform the signup request and verify the response
        mockMvc.perform(post("/securitymanagement/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSignupRequest)))
                .andExpect(status().isInternalServerError()); // Expect a 500 Internal Server Error status

        // Verify that the user repository was not called to save the user
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Print a summary of all test results after all tests have completed.
     */
    @AfterAll
    static void printTestSummary() {
        List<String> testResults = TestResultLoggerExtension.getTestResults();
        logger.info("==== Test Summary ====");
        int totalTests = testResults.size();
        long passedTests = testResults.stream().filter(result -> result.contains("PASSED")).count();
        logger.info("Total Tests: {}", totalTests);
        logger.info("Passed: {}", passedTests);
        logger.info("Failed: {}", totalTests - passedTests);
        logger.info("==== Detailed Results ====");
        testResults.forEach(logger::info);
    }
}