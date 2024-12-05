package com.in.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingResult implements ResponseDto {
    private int organizationsProcessed;
    private int usersProcessed;
}