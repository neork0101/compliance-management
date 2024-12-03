package com.in.auth.controller;


import com.in.auth.service.ExcelService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("identitymanagement/api/excel")
public class OnboardingController {
    @Autowired
    private ExcelService excelService;

    @PostMapping("/upload")
    public String uploadExcel(@RequestBody Map<String, String> request) {
        String filePath = request.get("filePath"); // Extract filePath from JSON request body
        if (filePath == null || filePath.isEmpty()) {
            return "Error: 'filePath' is required in the request body.";
        }
        excelService.processExcelFile(filePath);
        return "Data from Excel file has been successfully inserted into MongoDB!";
    }
}

