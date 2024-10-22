package com.in.security.util;

import java.security.SecureRandom;

public class OtpUtil {

    private static final int OTP_LENGTH = 6;
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateOTP() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(secureRandom.nextInt(10)); // Random digit 0-9
        }
        return otp.toString();
    }

    public static long getExpirationTime(int minutes) {
        return System.currentTimeMillis() + (minutes * 60 * 1000); // Set expiration in minutes
    }
}
