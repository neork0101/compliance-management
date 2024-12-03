package com.in.security.models;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "Organization")
public class Organization {
    @Id
    private String id;
    private String name;
    private String location;
    private int subscriptionsCount;
    private List<String> modules;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public int getSubscriptionsCount() { return subscriptionsCount; }
    public void setSubscriptionsCount(int subscriptionsCount) { this.subscriptionsCount = subscriptionsCount; }
    
    public List<String> getModules() { return modules; }
    public void setModules(List<String> modules) { this.modules = modules; }
}
