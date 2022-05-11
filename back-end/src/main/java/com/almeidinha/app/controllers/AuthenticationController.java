package com.almeidinha.app.controllers;

import com.almeidinha.app.config.JWTTokenHelper;
import com.almeidinha.app.entities.User;
import com.almeidinha.app.requests.AuthenticationRequest;
import com.almeidinha.app.responses.LoginResponse;
import com.almeidinha.app.responses.UserInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping("api/v1")
@CrossOrigin
public class AuthenticationController {

    private final JWTTokenHelper jwtTokenHelper;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthenticationController(
            JWTTokenHelper jwtTokenHelper,
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService
    ) {
        this.jwtTokenHelper = jwtTokenHelper;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody AuthenticationRequest authenticationRequest) throws InvalidKeySpecException, NoSuchAlgorithmException {

        final Authentication authentication = this.authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUserName(),
                        authenticationRequest.getPassword())
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        String jwtToken = this.jwtTokenHelper.generateToken(user.getUsername());

        LoginResponse response = new LoginResponse();
        response.setToken(jwtToken);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth/userinfo")
    public ResponseEntity<UserInfo> getUserInfo(Principal principal) {
        User user = (User) this.userDetailsService.loadUserByUsername(principal.getName());
        UserInfo userInfo = new UserInfo(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName()
        );
        userInfo.setRoles(user.getAuthorities());

        return ResponseEntity.ok(userInfo);
    }


}
