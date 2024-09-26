package com.in.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.in.auth.repository.UserRepository;
import com.in.security.models.ERole;
import com.in.security.models.Role;
import com.in.security.models.User;
import com.in.auth.test.TestResultLoggerExtension;

/**
 * Comprehensive test class for the UserDetailsServiceImpl.
 * This class tests the user details loading functionality.
 */
@SpringBootTest
@ExtendWith(TestResultLoggerExtension.class)
public class UserDetailsServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImplTest.class);

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private UserRepository userRepository;

    private User testUser;

    /**
     * Set up test data before each test method.
     */
    @BeforeEach
    public void setup() {
        // Initialize a test user with a role
        testUser = new User("testuser", "test@example.com", "password");
        testUser.setId("1");
        Role userRole = new Role(ERole.ROLE_USER);
        testUser.setRoles(Collections.singleton(userRole));
    }

    /**
     * Test successful loading of user details.
     * This test verifies that a user can be successfully loaded by username.
     */
    @Test
    @DisplayName("Load User By Username - Success")
    public void testLoadUserByUsername_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails, "UserDetails should not be null");
        assertEquals("testuser", userDetails.getUsername(), "Username should match");
        assertEquals("password", userDetails.getPassword(), "Password should match");
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")), "User should have ROLE_USER authority");

        // Verify that the user repository was called
        verify(userRepository).findByUsername("testuser");
    }

    /**
     * Test loading user details with a non-existent username.
     * This test verifies that the correct exception is thrown when a user is not found.
     */
    @Test
    @DisplayName("Load User By Username - User Not Found")
    public void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent");
        }, "Should throw UsernameNotFoundException");

        assertEquals("User Not Found with username: nonexistent", exception.getMessage(),
                "Exception message should match");

        // Verify that the user repository was called
        verify(userRepository).findByUsername("nonexistent");
    }

    /**
     * Test loading user details with a null username.
     * This test verifies that the service handles null input by throwing a UsernameNotFoundException.
     */
    @Test
    @DisplayName("Load User By Username - Null Username")
    public void testLoadUserByUsername_NullUsername() {
        // Act & Assert
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(null);
        }, "Should throw UsernameNotFoundException for null username");

        assertEquals("User Not Found with username: null", exception.getMessage(),
                "Exception message should match");

        // Verify that the user repository was not called
        verify(userRepository, never()).findByUsername(any());
    }
    /**
     * Test loading user details for a user with multiple roles.
     * This test verifies that all roles are correctly mapped to authorities.
     */
    @Test
    @DisplayName("Load User By Username - Multiple Roles")
    public void testLoadUserByUsername_MultipleRoles() {
        // Arrange
        User multiRoleUser = new User("multiuser", "multi@example.com", "password");
        multiRoleUser.setId("2");
        multiRoleUser.setRoles(Set.of(new Role(ERole.ROLE_USER), new Role(ERole.ROLE_ADMIN)));
        when(userRepository.findByUsername("multiuser")).thenReturn(Optional.of(multiRoleUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("multiuser");

        // Assert
        assertNotNull(userDetails, "UserDetails should not be null");
        assertEquals("multiuser", userDetails.getUsername(), "Username should match");
        assertEquals(2, userDetails.getAuthorities().size(), "User should have 2 authorities");
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")), "User should have ROLE_USER authority");
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")), "User should have ROLE_ADMIN authority");

        // Verify that the user repository was called
        verify(userRepository).findByUsername("multiuser");
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