package com.in.auth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.in.auth.repository.RoleRepository;
import com.in.security.models.Role;

/**
 * Service to handle role-related operations.
 */
@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Fetch all roles from the database.
     *
     * @return List of roles.
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
