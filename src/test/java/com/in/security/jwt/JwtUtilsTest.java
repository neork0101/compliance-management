package com.in.security.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import com.in.auth.service.UserDetailsImpl;
import com.in.auth.test.TestResultLoggerExtension;

import io.jsonwebtoken.ExpiredJwtException;

/**
 * Comprehensive test class for the JwtUtils.
 * This class tests JWT token generation, validation, and parsing.
 */
@SpringBootTest
@ExtendWith(TestResultLoggerExtension.class)
public class JwtUtilsTest {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtilsTest.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private UserDetailsImpl userDetails;
    private static final String TEST_JWT_SECRET = "testSecretKeyWhichIsLongEnoughForHS256Algorithm";
    private static final long TEST_JWT_EXPIRATION = 60000; // 1 minute

    /**
     * Set up test data and configure JwtUtils before each test method.
     */
    @BeforeEach
    public void setup() {
        userDetails = new UserDetailsImpl("1", "testuser", "test@example.com", "password", List.of());
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", TEST_JWT_SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", TEST_JWT_EXPIRATION);
    }

    /**
     * Test successful JWT token generation.
     * This test verifies that a token can be generated for a valid user.
     */
    @Test
    @DisplayName("Generate Token - Success")
    public void testGenerateToken_Success() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Act
        String token = jwtUtils.generateToken(userDetails);

        // Assert
        assertNotNull(token, "Generated token should not be null");
        assertTrue(token.length() > 0, "Generated token should not be empty");
    }

    /**
     * Test successful JWT token validation.
     * This test verifies that a valid token can be successfully validated.
     */
    @Test
    @DisplayName("Validate Token - Success")
    public void testValidateJwtToken_Success() {
        // Arrange
        String token = jwtUtils.generateToken(userDetails);

        // Act
        boolean isValid = jwtUtils.validateJwtToken(token);

        // Assert
        assertTrue(isValid, "Token should be valid");
    }

    /**
     * Test JWT token validation with an expired token.
     * This test verifies that an expired token is correctly identified as invalid.
     */
    @Test
    @DisplayName("Validate Token - Expired")
    public void testValidateJwtToken_Expired() {
        // Arrange
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 1); // Set expiration to 1ms
        String token = jwtUtils.generateToken(userDetails);

        // Act & Assert
        assertFalse(jwtUtils.validateJwtToken(token), "Expired token should be invalid");
    }

    /**
     * Test JWT token validation with an invalid token.
     * This test verifies that an improperly formatted token is identified as invalid.
     */
    @Test
    @DisplayName("Validate Token - Invalid Format")
    public void testValidateJwtToken_InvalidFormat() {
        // Arrange
        String invalidToken = "invalid.token.format";

        // Act & Assert
        assertFalse(jwtUtils.validateJwtToken(invalidToken), "Improperly formatted token should be invalid");
    }

    /**
     * Test extracting username from a valid JWT token.
     * This test verifies that the correct username can be extracted from a valid token.
     */
    @Test
    @DisplayName("Get Username From Token - Success")
    public void testGetUserNameFromJwtToken_Success() {
        // Arrange
        String token = jwtUtils.generateToken(userDetails);

        // Act
        String extractedUsername = jwtUtils.getUserNameFromJwtToken(token);

        // Assert
        assertEquals(userDetails.getUsername(), extractedUsername, "Extracted username should match the original");
    }

    /**
     * Test extracting username from an expired JWT token.
     * This test verifies that attempting to extract a username from an expired token throws an exception.
     */
    @Test
    @DisplayName("Get Username From Token - Expired Token")
    public void testGetUserNameFromJwtToken_ExpiredToken() {
        // Arrange
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 1); // Set expiration to 1ms
        String token = jwtUtils.generateToken(userDetails);

        // Act & Assert
        assertThrows(ExpiredJwtException.class, () -> jwtUtils.getUserNameFromJwtToken(token),
                "Extracting username from expired token should throw ExpiredJwtException");
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