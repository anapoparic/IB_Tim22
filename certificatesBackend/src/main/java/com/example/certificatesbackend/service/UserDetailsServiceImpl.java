package com.example.certificatesbackend.service;

import com.example.certificatesbackend.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.certificatesbackend.repository.IUserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(email);

        return userOptional.map(user -> {
            if (user.isActive()) {
                return org.springframework.security.core.userdetails.User
                        .withUsername(email)
                        .password(user.getPassword())
                        .roles(user.getRole().toString()) // Assuming getRole() returns a string representation of the role
                        .build();
            } else {
                throw new DisabledException("User account is not active");
                // Alternatively, you can throw UsernameNotFoundException to treat inactive users as not found
            }
        }).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
//    public String signUpUser(User user) {
//        boolean userExists = userRepository
//                .findByEmail(user.getEmail())
//                .isPresent();
//
//        if (userExists) {
//            // TODO check of attributes are the same and
//            // TODO if email not confirmed send confirmation email.
//
//            throw new IllegalStateException("email already taken");
//        }
//
//        String encodedPassword = bCryptPasswordEncoder
//                .encode(user.getPassword());
//
//        user.setPassword(encodedPassword);
//
//        userRepository.save(user);
//
//        String token = UUID.randomUUID().toString();
//
//
//        ConfirmationToken confirmationToken = new ConfirmationToken(
//                token,
//                LocalDateTime.now(),
//                LocalDateTime.now().plusHours(24),
//                user
//        );
//
//        confirmationTokenService.saveConfirmationToken(
//                confirmationToken);
//
////        TODO: SEND EMAIL
//
//        return token;
//    }

    public int enableUser(String email) {
        return userRepository.enableUser(email);
    }
}
