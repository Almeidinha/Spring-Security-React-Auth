package com.almeidinha.app.requests;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationRequest {

    private String userName;
    private String password;

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

}
