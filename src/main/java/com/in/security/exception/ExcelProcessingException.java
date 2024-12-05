package com.in.security.exception;

public class ExcelProcessingException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ExcelProcessingException(String message) {
        super(message);
    }

    public ExcelProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}