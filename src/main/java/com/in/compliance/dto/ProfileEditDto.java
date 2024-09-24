package com.in.compliance.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
@Data
public class ProfileEditDto implements ResponseDto{

    private String interests;
    private String hobbies;
    private String professionalExperience;
    private String profilePicture;
    
}