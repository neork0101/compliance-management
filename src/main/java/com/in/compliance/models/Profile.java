package com.in.compliance.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "profiles")
public class Profile {

   // @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Id
    private String id;
    
    private String volunteerId;
    
    //@Column(name = "interests")
    private String interests;

    //@Column(name = "hobbies")
    private String hobbies;

    //@Column(name = "professional_experience")
    private String professionalExperience;

    //@Column(name = "profile_picture")
    private String profilePicture;

    //@Column(name="created_at", updatable= false)
    LocalDateTime createdAt=LocalDateTime.now();

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
	/*
	 * @OneToOne
	 * 
	 * @JoinColumn(name = "volunteer_id", referencedColumnName = "id") private
	 * Volunteer volunteer;
	 */
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInterests() {
		return interests;
	}
	public void setInterests(String interests) {
		this.interests = interests;
	}
	public String getHobbies() {
		return hobbies;
	}
	public void setHobbies(String hobbies) {
		this.hobbies = hobbies;
	}
	public String getProfessionalExperience() {
		return professionalExperience;
	}
	public void setProfessionalExperience(String professionalExperience) {
		this.professionalExperience = professionalExperience;
	}
	public String getProfilePicture() {
		return profilePicture;
	}
	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}
	public String getVolunteerId() {
		return volunteerId;
	}
	public void setVolunteerId(String volunteerId) {
		this.volunteerId = volunteerId;
	}
	
	
}
