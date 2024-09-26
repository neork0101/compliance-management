package com.in.auth.test;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom TestWatcher extension to log test results.
 */
public class TestResultLoggerExtension implements TestWatcher {

    private static final Logger logger = LoggerFactory.getLogger(TestResultLoggerExtension.class);

    // Static list to store test results across all tests
    private static List<String> testResults = new ArrayList<>();

    @Override
    public void testSuccessful(ExtensionContext context) {
        String testName = context.getDisplayName();
        String logMessage = String.format("Test: %s - PASSED", testName);
        logger.info(logMessage);
        testResults.add(logMessage);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        String testName = context.getDisplayName();
        String logMessage = String.format("Test: %s - FAILED - Reason: %s", testName, cause.getMessage());
        logger.info(logMessage);
        testResults.add(logMessage);
    }

    /**
     * Provides access to the test results.
     * @return List of test result messages.
     */
    public static List<String> getTestResults() {
        return testResults;
    }
}