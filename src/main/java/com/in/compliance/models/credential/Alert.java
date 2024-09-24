package com.in.compliance.models.credential;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Document(collection = "Alerts")
public class Alert {

	private String id;

    private String message;
    private boolean read;
    private boolean deleted = false; // field for soft delete
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
