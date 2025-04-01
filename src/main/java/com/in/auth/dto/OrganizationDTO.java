package com.in.auth.dto;


import java.util.List;

import com.in.security.models.Department;
import com.in.security.models.Location;

import lombok.Data;

@Data
public class OrganizationDTO {
    private String id;
    private String name;
    private String acronym;
    private String type; //E.g (Academia, Community College, Polytechnic, Research Institute, Hospital
    private Location location;
    private List<Department> departments;
    private int subscriptionsCount;
    private List<String> modules;
    private List<OnboardedUserDTO> onboardedUsers;

}
