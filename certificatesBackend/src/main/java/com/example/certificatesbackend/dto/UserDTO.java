package com.example.certificatesbackend.dto;

import com.example.certificatesbackend.domain.enums.Role;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private AddressDTO address;
    private Integer phone;
    private String email;
    private String password;
    private boolean blocked;
    private boolean verified;
    private boolean active = true;
    private PhotoDTO profilePicture;
    private Role role;


    public UserDTO(Long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public UserDTO(String firstName, String lastName, String email, String password, PhotoDTO profilePicture) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.profilePicture = profilePicture;
    }

    public UserDTO(Long id, String firstName, String lastName, AddressDTO address, Integer phone, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.active = true;
        this.verified = true;
    }

    public UserDTO(Long id, String firstName, String lastName, AddressDTO address, Integer phone, String email, String password, boolean isBlocked, boolean verified, PhotoDTO profilePicture, Role role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.blocked = isBlocked;
        this.verified = verified;
        this.profilePicture = profilePicture;
        this.role = role;
    }
}
