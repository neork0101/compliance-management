package com.in.security.models;


import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@Document(collection = "Organization")
@Data
public class Organization {
    @Id
    private String id;
    private String name;
    private String acronym;
    private String type; //E.g (Academia, Community College, Polytechnic, Research Institute, Hospital
    private Location location;
    private List<Department> departments;
    private int subscriptionsCount;
    private List<String> modules;
    @DBRef
    @JsonManagedReference
    private List<OnboardedUser> onboardedUsers=new ArrayList<>();

}
