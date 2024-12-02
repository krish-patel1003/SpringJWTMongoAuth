package com.krish.SpringJWTAuthApp.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class MessageResponse {

    private String message;
    private HttpStatus http_status_code;
}
