package com.example.certificatesbackend.controller;

import com.example.certificatesbackend.domain.User;
import com.example.certificatesbackend.dto.LogInDTO;
import com.example.certificatesbackend.dto.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.http.MediaType;

import com.example.certificatesbackend.service.UserService;
import com.example.certificatesbackend.security.jwt.JwtTokenUtil;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class LogInController {
    private static final Logger logger = LoggerFactory.getLogger(LogInController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TokenDTO> login(@RequestBody LogInDTO loginDto) {

        System.out.println("Usao sam u funkciju");

        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());


        try {
            System.out.println("ovo su prosledjeni parametri " + loginDto.getEmail() + " " + loginDto.getPassword() );
            System.out.println("ovo je prebacen auth " + authReq.getCredentials() );
            Authentication auth = authenticationManager.authenticate(authReq);
            System.out.println("Zavrsio sam autentikaciju");

            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(auth);

            UserDetails userDetail = userDetailsService.loadUserByUsername(loginDto.getEmail());
            System.out.println("Ucitao sam korisnika po username-u");

            if (!userDetail.isEnabled()) {
                System.out.println("Korisnik nije omogucen");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            User user = userService.getByEmail(loginDto.getEmail());
            if (user != null && user.isBlocked()) {
                System.out.println("Korisnik je blokiran");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            String token = jwtTokenUtil.generateToken(userDetail);
            System.out.println("Generisem token: " + token);

            TokenDTO tokenDto = new TokenDTO();
            tokenDto.setToken(token);
            System.out.println("Setujem token");

            return new ResponseEntity<>(tokenDto, HttpStatus.OK);

        } catch (AuthenticationException e) {
            System.out.println("Autentikacija nije uspela: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            System.out.println("Greška prilikom logiranja: " + e.getMessage());
            e.printStackTrace(); // Dodajte ovu liniju za detaljan ispis stack trace-a u konzolu
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
