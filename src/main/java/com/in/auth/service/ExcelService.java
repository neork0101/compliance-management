package com.in.auth.service;


import com.in.security.models.ERole;
import com.in.security.models.OnboardedUser;
import com.in.security.models.Organization;
import com.in.security.models.Role;
import com.in.auth.controller.AuthController;
import com.in.auth.repository.OnboardedUserRepository;
import com.in.auth.repository.OrganizationRepository;
import com.in.auth.repository.RoleRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExcelService {
    
    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private OrganizationRepository organizationRepository;

   
    @Autowired
    private OnboardedUserRepository onboardedUserRepository;
    
    @Autowired
    RoleRepository roleRepository;

    public void processExcelFile(String filePath) {
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {

            // Process Organization_Details sheet
            Sheet orgSheet = workbook.getSheet("Organization_Details");
            List<Organization> organizations = new ArrayList<>();
        
      
            for (Row row : orgSheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                if (row.getCell(0) == null || row.getCell(1) == null) continue; // Skip empty rows

                Organization organization = new Organization();
                organization.setName(row.getCell(0).getStringCellValue());
                organization.setLocation(row.getCell(1).getStringCellValue());
                organization.setSubscriptionsCount((int) row.getCell(2).getNumericCellValue());
                organization.setModules(Arrays.asList(row.getCell(3).getStringCellValue().split(",")));
                LOG.info("organization");
                organizations.add(organization);
            }
            List<Organization> savedOrganizations = organizationRepository.saveAll(organizations);

            // Process User_Details sheet
            Sheet userSheet = workbook.getSheet("User_Details");
            List<OnboardedUser> users = new ArrayList<>();
            for (Row row : userSheet) {
                
                if (row.getRowNum() == 0) continue; // Skip header row
                if (row.getCell(0) == null || row.getCell(1) == null) continue; // Skip empty rows

                OnboardedUser user = new OnboardedUser();
                user.setEmail(row.getCell(0).getStringCellValue());
                LOG.info("User Email:"+user.getEmail());
                Set<Role> roles = Arrays.stream(row.getCell(1).getStringCellValue().split(","))
                        .map(String::trim)
                        .map(roleName -> roleRepository.findByName(ERole.valueOf(roleName))
                                .orElseThrow(() -> new RuntimeException("Error: Role not found - " + roleName)))
                        .collect(Collectors.toSet());
                user.setRoles(roles);
                // find organization by name
                user.setOrganization(savedOrganizations.get(0));
                user.setStatus("Signup_Pending");
                
                users.add(user);
                
            }
            onboardedUserRepository.saveAll(users);

        } catch (Exception e) {
            LOG.info("Exception in processing file");
            StringWriter sw = new StringWriter();
            try (PrintWriter pw = new PrintWriter(sw)) {
                e.printStackTrace(pw);
            }
         
            LOG.error("An error occurred: {}", sw.toString(), e);
        }
    }
   
}    