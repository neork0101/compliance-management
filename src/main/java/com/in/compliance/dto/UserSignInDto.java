package com.in.compliance.dto;

import com.in.compliance.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserSignInDto implements ResponseDto{

    private User userCredentials;
 

}
