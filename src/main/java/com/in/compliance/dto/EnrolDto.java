package com.in.compliance.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class EnrolDto implements ResponseDto {

    private String programId;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    private String timeOfProgram;
    
    private String volunteerId;

	
	public String getProgramId() {
		return programId;
	}


    
    

}
