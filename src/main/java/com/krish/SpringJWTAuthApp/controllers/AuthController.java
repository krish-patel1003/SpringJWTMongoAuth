package com.krish.SpringJWTAuthApp.controllers;

import com.krish.SpringJWTAuthApp.models.User;
import com.krish.SpringJWTAuthApp.payload.request.LoginRequest;
import com.krish.SpringJWTAuthApp.payload.request.SignupRequest;
import com.krish.SpringJWTAuthApp.payload.response.JwtResponse;
import com.krish.SpringJWTAuthApp.payload.response.MessageResponse;
import com.krish.SpringJWTAuthApp.repo.RoleRepo;
import com.krish.SpringJWTAuthApp.repo.UserRepo;
import com.krish.SpringJWTAuthApp.security.jwt.JwtService;
import com.krish.SpringJWTAuthApp.security.services.AuthenticationService;
import com.krish.SpringJWTAuthApp.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwt;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationService authService;

    @Autowired
    UserRepo userRepo;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest request) {
            JwtResponse jwtResponse = authService.authenticate(request);
            return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest request) {
        MessageResponse messageResponse = authService.register(request);

        return new ResponseEntity<>(messageResponse, messageResponse.getHttp_status_code());
    }

}
