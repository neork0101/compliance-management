package com.in.auth.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.in.auth.dto.CommunicationResponse;
import com.in.auth.dto.ProcessingResult;
import com.in.auth.dto.ResponseDto;
import com.in.auth.service.OnboardingService;
import com.in.security.exception.InvalidFileFormatException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("identitymanagement/api/excel")
@Tag(name = "Onboarding Controller", description = "Excel file processing endpoints for user onboarding")
@Slf4j
public class OnboardingController {

    @Autowired
    private OnboardingService onboardingService;

    @PostMapping("/upload")
    @Operation(summary = "Upload and process Excel file",
              description = "Process an Excel file containing organization and user details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or file format"),
        @ApiResponse(responseCode = "404", description = "File not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseDto> uploadExcel(@RequestBody Map<String, String> request) {
        log.info("Processing excel file upload request");
        String filePath = request.get("filePath");

        if (filePath == null || filePath.isEmpty()) {
            throw new InvalidFileFormatException("File path is required");
        }

        ProcessingResult result = onboardingService.processExcelFile(filePath);
        return ResponseEntity.ok(new CommunicationResponse<>("SUCCESS",
            "Excel file processed successfully", result));
    }
}