package com.in.security.models;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import java.util.List;

@Document(collection = "Organization")
@Data
public class Organization {
    @Id
    private String id;
    private String name;
    private String location;
    private int subscriptionsCount;
    private List<String> modules;

  
}
