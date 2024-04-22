package com.example.certificatesbackend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LogInDTO {
    private String email;
    private String password;
}
