package com.example.certificatesbackend.controller;

import com.example.certificatesbackend.domain.User;
import com.example.certificatesbackend.dto.LogInDTO;
import com.example.certificatesbackend.dto.TokenDTO;
import com.example.certificatesbackend.security.jwt.JwtTokenUtil;
import com.example.certificatesbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class LogInController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping( value ="/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TokenDTO> login(@RequestBody LogInDTO loginDto) {

        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(loginDto.getEmail(),
                loginDto.getPassword());
        Authentication auth = authenticationManager.authenticate(authReq);

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);

        UserDetails userDetail = userDetailsService.loadUserByUsername(loginDto.getEmail());

        if (!userDetail.isEnabled()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userService.getByEmail(loginDto.getEmail());
        if (user.isBlocked()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        String token = jwtTokenUtil.generateToken(userDetail);
        TokenDTO tokenDto = new TokenDTO();
        tokenDto.setToken(token);

        return new ResponseEntity<>(tokenDto, HttpStatus.OK);
    }

    @GetMapping(value = "/logout", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity logoutUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof AnonymousAuthenticationToken)){
            SecurityContextHolder.clearContext();

            return new ResponseEntity<>("You successfully logged out!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User is not authenticated!", HttpStatus.UNAUTHORIZED);
        }

    }
}
