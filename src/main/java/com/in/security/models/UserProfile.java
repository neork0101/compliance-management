package com.in.security.models;



import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_profiles")
public class UserProfile {
    
	@Id
    private String id;
	@Field(name = "userId")
    private String userId;
    @Field(name = "first_name")
    private String firstName;
    @Field(name = "last_name")
    private String lastName;
    @Field(name = "email")
    private String email;
    @Field(name = "phone_number")
    private String phoneNumber;
    @Field(name = "address")
    private String address;
    @Field(name = "user_name")
    private String userName;
    @Field(name = "interests")
    private String interests;
    @Field(name = "profile_picture")
    private String profilePicture;
    @Field(name = "city")
    private String city;
    @Field(name = "status")
    private String status;
  
}
