package com.in.auth.service;

import com.in.auth.repository.OnboardedUserRepository;
import com.in.auth.repository.OrganizationRepository;
import com.in.auth.repository.RoleRepository;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.in.security.exception.ExcelProcessingException;
import com.in.security.exception.FileNotFoundException;
import com.in.security.exception.InvalidFileFormatException;
import com.in.security.models.ERole;
import com.in.security.models.OnboardedUser;
import com.in.security.models.Organization;
import com.in.security.models.Role;
import com.in.auth.dto.ProcessingResult;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OnboardingService {
    
    private static final Logger log = LoggerFactory.getLogger(OnboardingService.class);
    
    @Autowired
    private OrganizationRepository organizationRepository;
    
    @Autowired
    private OnboardedUserRepository onboardedUserRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private MongoClient mongoClient;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("${identity.excel.allowed-extensions:xlsx}")
    private List<String> allowedExtensions;

    /**
     * Process the Excel file and save organizations and users to the database.
     * Uses MongoDB transactions to ensure atomicity.
     */
    public ProcessingResult processExcelFile(String filePath) {
        log.info("Starting to process excel file: {}", filePath);
        validateFile(filePath);
        
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            // Process all data first before any database operations
            log.info("Processing organization sheet...");
            List<Organization> organizations = processOrganizationSheet(workbook);
            
            log.info("Processing user sheet...");
            List<OnboardedUser> users = processUserSheet(workbook, organizations);
            
            // Handle database operations in a transaction
            return executeInTransaction(organizations, users);
        } catch (IOException e) {
            log.error("IO error while processing excel file", e);
            throw new ExcelProcessingException("Failed to read excel file", e);
        } catch (Exception e) {
            log.error("Error processing excel file", e);
            throw new ExcelProcessingException("Failed to process excel file: " + e.getMessage(), e);
        }
    }

    /**
     * Execute database operations within a MongoDB transaction
     */
    private ProcessingResult executeInTransaction(List<Organization> organizations, List<OnboardedUser> users) {
        try (ClientSession session = mongoClient.startSession()) {
            try {
                // Start transaction
                session.startTransaction();
                
                // Save organizations
                List<Organization> savedOrgs = saveOrganizations(session, organizations);
                log.info("Saved {} organizations", savedOrgs.size());
                
                // Update and save users
                List<OnboardedUser> savedUsers = saveUsers(session, users, savedOrgs.get(0));
                log.info("Saved {} users", savedUsers.size());
                
                // Commit transaction
                session.commitTransaction();
                
                return new ProcessingResult(savedOrgs.size(), savedUsers.size());
                
            } catch (Exception e) {
                log.error("Error during transaction, performing rollback", e);
                session.abortTransaction();
                throw new ExcelProcessingException("Failed to save data: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Save organizations within the transaction context
     */
    private List<Organization> saveOrganizations(ClientSession session, List<Organization> organizations) {
        List<Organization> savedOrgs = new ArrayList<>();
        for (Organization org : organizations) {
            // Need to bind the save operation to the session
            Organization savedOrg = mongoTemplate.withSession(session)
                .execute(action -> organizationRepository.save(org));
            savedOrgs.add(savedOrg);
        }
        return savedOrgs;
    }

    /**
     * Save users within the transaction context
     */
    private List<OnboardedUser> saveUsers(ClientSession session, List<OnboardedUser> users, Organization organization) {
        List<OnboardedUser> savedUsers = new ArrayList<>();
        for (OnboardedUser user : users) {
            user.setOrganization(organization);
            // Need to bind the save operation to the session
            OnboardedUser savedUser = mongoTemplate.withSession(session)
                .execute(action -> onboardedUserRepository.save(user));
            savedUsers.add(savedUser);
        }
        return savedUsers;
    }

    private void validateFile(String filePath) {
        File file = new File(filePath);
        
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }
        
        if (!file.isFile()) {
            throw new InvalidFileFormatException("Path is not a file: " + filePath);
        }
        
        String extension = FilenameUtils.getExtension(filePath).toLowerCase();
        if (!allowedExtensions.contains(extension)) {
            throw new InvalidFileFormatException("Invalid file format. Allowed formats: " + 
                String.join(", ", allowedExtensions));
        }
    }

    /**
     * Process the organization sheet from the workbook.
     */
    private List<Organization> processOrganizationSheet(Workbook workbook) {
        Sheet sheet = workbook.getSheet("Organization_Details");
        if (sheet == null) {
            throw new ExcelProcessingException("Organization_Details sheet not found");
        }

        List<Organization> organizations = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header

            try {
                Organization org = extractOrganization(row, formatter);
                if (org != null) {
                    organizations.add(org);
                }
            } catch (Exception e) {
                log.error("Error processing organization row {}", row.getRowNum(), e);
                throw new ExcelProcessingException("Error processing organization at row " + 
                    (row.getRowNum() + 1), e);
            }
        }

        if (organizations.isEmpty()) {
            throw new ExcelProcessingException("No valid organizations found in the Excel file");
        }

        return organizations;
    }

    private Organization extractOrganization(Row row, DataFormatter formatter) {
        String name = formatter.formatCellValue(row.getCell(0));
        if (StringUtils.isBlank(name)) {
            return null;
        }

        Organization org = new Organization();
        org.setName(name);
        org.setLocation(formatter.formatCellValue(row.getCell(1)));
        
        // Handle numeric cell value safely
        Cell subscriptionCell = row.getCell(2);
        if (subscriptionCell != null) {
            if (subscriptionCell.getCellType() == CellType.NUMERIC) {
                org.setSubscriptionsCount((int) subscriptionCell.getNumericCellValue());
            } else {
                org.setSubscriptionsCount(Integer.parseInt(formatter.formatCellValue(subscriptionCell)));
            }
        }

        String modulesStr = formatter.formatCellValue(row.getCell(3));
        if (StringUtils.isNotBlank(modulesStr)) {
            org.setModules(Arrays.stream(modulesStr.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList()));
        }
        
        return org;
    }

    /**
     * Process the user sheet from the workbook.
     */
    private List<OnboardedUser> processUserSheet(Workbook workbook, List<Organization> organizations) {
        Sheet sheet = workbook.getSheet("User_Details");
        if (sheet == null) {
            throw new ExcelProcessingException("User_Details sheet not found");
        }

        List<OnboardedUser> users = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // Skip header

            try {
                OnboardedUser user = extractUser(row, formatter, organizations);
                if (user != null) {
                    users.add(user);
                }
            } catch (Exception e) {
                log.error("Error processing user row {}", row.getRowNum(), e);
                throw new ExcelProcessingException("Error processing user at row " + 
                    (row.getRowNum() + 1), e);
            }
        }

        if (users.isEmpty()) {
            throw new ExcelProcessingException("No valid users found in the Excel file");
        }

        return users;
    }

    private OnboardedUser extractUser(Row row, DataFormatter formatter, List<Organization> organizations) {
        String email = formatter.formatCellValue(row.getCell(0));
        if (StringUtils.isBlank(email)) {
            return null;
        }

        OnboardedUser user = new OnboardedUser();
        user.setEmail(email.trim());

        // Process roles
        String rolesStr = formatter.formatCellValue(row.getCell(1));
        if (StringUtils.isBlank(rolesStr)) {
            throw new ExcelProcessingException("Roles cannot be empty for user: " + email);
        }

        Set<Role> roles = Arrays.stream(rolesStr.split(","))
            .map(String::trim)
            .filter(StringUtils::isNotBlank)
            .map(roleName -> {
                try {
                    return roleRepository.findByName(ERole.valueOf(roleName.toUpperCase()))
                        .orElseThrow(() -> new ExcelProcessingException("Role not found: " + roleName));
                } catch (IllegalArgumentException e) {
                    throw new ExcelProcessingException("Invalid role name: " + roleName);
                }
            })
            .collect(Collectors.toSet());

        if (roles.isEmpty()) {
            throw new ExcelProcessingException("No valid roles found for user: " + email);
        }

        user.setRoles(roles);
        user.setStatus("Signup_Pending");

        return user;
    }
}