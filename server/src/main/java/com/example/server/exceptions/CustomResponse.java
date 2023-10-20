package com.example.server.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomResponse {
    private String message;
    private Date timestamp = new Date();

    public CustomResponse(String msg) {
        message = msg;
    }
}
