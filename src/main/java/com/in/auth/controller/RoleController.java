package com.in.auth.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.in.auth.service.RoleService;
import com.in.security.models.Role;

/**
 * Controller to manage roles.
 */
@RestController
@RequestMapping("/identitymanagement/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * API to retrieve all roles from the database.
     *
     * @return ResponseEntity containing the list of roles.
     */
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }
}
