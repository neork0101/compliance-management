package com.in.auth.payload.request;

import lombok.Data;

@Data
public class ErrorResponse {
	private String status;
	private String msg;

	public ErrorResponse(String status, String msg){
		this.status=status;
		this.msg=msg;

	}
}
