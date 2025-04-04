package com.in.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.in.auth.repository.RoleRepository;
import com.in.security.models.ERole;
import com.in.security.models.Role;

import jakarta.annotation.PostConstruct;

@Service
public class RoleInitializerService {

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void init() {
        if (roleRepository.count() == 0) {
            // Add admin role if no roles exist as default
            Role adminRole = new Role(ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
        }
    }
}