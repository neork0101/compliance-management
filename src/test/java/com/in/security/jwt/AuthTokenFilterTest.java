// File: src/test/java/com/in/security/jwt/AuthTokenFilterTest.java

package com.in.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
// Import MockitoExtension to enable Mockito annotations
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.in.auth.service.UserDetailsImpl;
import com.in.auth.service.UserDetailsServiceImpl;
import com.in.auth.test.TestResultLoggerExtension;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Comprehensive test class for the AuthTokenFilter.
 * This class tests the doFilterInternal method for various scenarios.
 */
@ExtendWith(MockitoExtension.class) // Enable Mockito annotations
@ExtendWith(TestResultLoggerExtension.class) // Custom extension for logging
public class AuthTokenFilterTest {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilterTest.class);

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private UserDetailsImpl userDetails;

    /**
     * Set up test data and inject mocks into private fields.
     */
    @BeforeEach
    public void setUp() {
        // Initialize a test user
        userDetails = new UserDetailsImpl("1", "testuser", "test@example.com", "password", Collections.emptyList());

        // Inject mocks into the private fields of authTokenFilter
       // Uses ReflectionTestUtils.setField to inject the mocked jwtUtils and userDetailsService into the authTokenFilter
       // This is necessary because these fields are private and don't have setters
        ReflectionTestUtils.setField(authTokenFilter, "jwtUtils", jwtUtils);
        ReflectionTestUtils.setField(authTokenFilter, "userDetailsService", userDetailsService);
    }

    /**
     * Test case for a valid JWT token.
     * Expectation: Authentication should be set in the SecurityContextHolder.
     */
    @Test
    @DisplayName("doFilterInternal - Valid JWT Token")
    public void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);

        // Act
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should be set");
        assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getName(), "Username should match");
        verify(filterChain).doFilter(request, response);
    }

    /**
     * Test case for an invalid JWT token.
     * Expectation: Authentication should not be set in the SecurityContextHolder.
     */
    @Test
    @DisplayName("doFilterInternal - Invalid JWT Token")
    public void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        // Arrange
        String token = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtils.validateJwtToken(token)).thenReturn(false);

        // Act
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should not be set");
        verify(filterChain).doFilter(request, response);
    }

    /**
     * Test case for missing JWT token.
     * Expectation: Authentication should not be set in the SecurityContextHolder.
     */
    @Test
    @DisplayName("doFilterInternal - Missing JWT Token")
    public void testDoFilterInternal_MissingToken() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should not be set");
        verify(filterChain).doFilter(request, response);
    }

    /**
     * Test case for expired JWT token.
     * Expectation: Authentication should not be set, exception should be handled gracefully.
     */
    @Test
    @DisplayName("doFilterInternal - Expired JWT Token")
    public void testDoFilterInternal_ExpiredToken() throws ServletException, IOException {
        // Arrange
        String token = "expired.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        doThrow(new RuntimeException("JWT token is expired")).when(jwtUtils).validateJwtToken(token);

        // Act
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should not be set");
        verify(filterChain).doFilter(request, response);
        // Verify that the exception message is set as a request attribute
        verify(request).setAttribute("JWT_MSG", "JWT token is expired");
    }

    /**
     * Test case when an exception occurs during authentication.
     * Expectation: Exception should be caught, and authentication should not be set.
     */
    @Test
    @DisplayName("doFilterInternal - Exception Handling")
    public void testDoFilterInternal_ExceptionHandling() throws ServletException, IOException {
        // Arrange
        String token = "some.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        doThrow(new RuntimeException("Unexpected error")).when(jwtUtils).validateJwtToken(token);

        // Act
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication(), "Authentication should not be set");
        verify(filterChain).doFilter(request, response);
        verify(request).setAttribute("JWT_MSG", "Unexpected error");
    }

    /**
     * Clean up after all tests.
     * Print a summary of all test results.
     */
    @AfterAll
    static void printTestSummary() {
        // Retrieve test results from the custom TestResultLoggerExtension
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
