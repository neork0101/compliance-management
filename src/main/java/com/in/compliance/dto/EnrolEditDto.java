package com.in.compliance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class EnrolEditDto implements ResponseDto{

    private String id;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private String date;

    private String timeOfProgram;

}
