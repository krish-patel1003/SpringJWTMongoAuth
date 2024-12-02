package com.krish.SpringJWTAuthApp.security.services;

import com.krish.SpringJWTAuthApp.models.ERole;
import com.krish.SpringJWTAuthApp.models.Role;
import com.krish.SpringJWTAuthApp.models.User;
import com.krish.SpringJWTAuthApp.payload.request.LoginRequest;
import com.krish.SpringJWTAuthApp.payload.request.SignupRequest;
import com.krish.SpringJWTAuthApp.payload.response.JwtResponse;
import com.krish.SpringJWTAuthApp.payload.response.MessageResponse;
import com.krish.SpringJWTAuthApp.repo.RoleRepo;
import com.krish.SpringJWTAuthApp.repo.UserRepo;
import com.krish.SpringJWTAuthApp.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuthenticationService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepo userRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtService jwtService;

    public JwtResponse authenticate(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(),
                        request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtService.generateJwtToken(request.getUsername());

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }

    public MessageResponse register(SignupRequest request) {

        if (userRepo.existsByUsername(request.getUsername())) {
            System.out.println("Username taken");
            return new MessageResponse("Error: Username already taken!!!", HttpStatus.BAD_REQUEST);
        }

        if (userRepo.existsByEmail(request.getEmail())) {
            System.out.println("Email taken");
            return new MessageResponse("Error: Email already in use!!!", HttpStatus.BAD_REQUEST);
        }

        User user = new User(request.getUsername(),
                request.getEmail(),
                encoder.encode(request.getPassword()));

        Set<String> strRoles = request.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepo.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not Found."));
            roles.add(userRole);
        }
        else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepo.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not Found;"));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRepo.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not Found;"));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepo.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role not Found"));
                        roles.add(userRole);
                        break;
                }
            });
        }

        user.setRoles(roles);
        userRepo.save(user);

        return new MessageResponse("New User created!" + user.getUsername(), HttpStatus.CREATED);
    }
}
