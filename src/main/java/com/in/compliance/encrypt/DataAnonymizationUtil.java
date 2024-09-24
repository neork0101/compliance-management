package com.in.compliance.encrypt;

import java.util.UUID;

public class DataAnonymizationUtil {

    // Masking provider name by showing only the first letter
    public static String anonymizeProviderName(String providerName) {
        if (providerName == null || providerName.isEmpty()) {
            return providerName;
        }
        return providerName.charAt(0) + "****";  // Example: "John Doe" -> "J****"
    }

    // Tokenization: Replace sensitive data with a UUID token
    public static String tokenizeData(String data) {
        if (data == null || data.isEmpty()) {
            return data;
        }
        // Generate a UUID token for the data
        return UUID.randomUUID().toString();
    }

    // Masking method for contact info (e.g., email or phone)
    public static String maskContactInfo(String contactInfo) {
        if (contactInfo == null || contactInfo.isEmpty()) {
            return contactInfo;
        }
        int length = contactInfo.length();
        if (length <= 4) {
            return "****";  // Mask the whole contact info if it's too short
        }
        // Show only the last 4 characters (e.g., email or phone)
        return "****" + contactInfo.substring(length - 4);
    }
}
