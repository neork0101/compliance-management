package com.in.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingResult implements ResponseDto {
    private int organizationsProcessed;
    private int usersProcessed;
}