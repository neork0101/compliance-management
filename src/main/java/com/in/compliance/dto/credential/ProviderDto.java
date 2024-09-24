package com.in.compliance.dto.credential;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ProviderDto {
	private String id;
	private String name;
	private String contactInfo;
	private String address;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
    private boolean deleted = false; // field for soft delete

}
