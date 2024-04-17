package rs.ac.uns.ftn.asd.BookedUp.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.BookedUp.domain.Accommodation;
import rs.ac.uns.ftn.asd.BookedUp.domain.User;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.AccommodationStatus;
import rs.ac.uns.ftn.asd.BookedUp.dto.AccommodationDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.LogInDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.UserDTO;
import rs.ac.uns.ftn.asd.BookedUp.mapper.AccommodationMapper;
import rs.ac.uns.ftn.asd.BookedUp.mapper.AddressMapper;
import rs.ac.uns.ftn.asd.BookedUp.mapper.PhotoMapper;
import rs.ac.uns.ftn.asd.BookedUp.mapper.UserMapper;
import rs.ac.uns.ftn.asd.BookedUp.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    /** url: /api/users GET*/
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<UserDTO>> getUsers() {
        Collection<User> users = userService.getAll();

        Collection<UserDTO> usersDTO = users.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(usersDTO, HttpStatus.OK);
    }

    /** url: /api/users/1 GET*/
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> getUser(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        UserDTO userDTO = UserMapper.toDto(user);

        if (userDTO == null) {
            return new ResponseEntity<UserDTO>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
    }

    /** url: /api/users/active-users GET*/
    @GetMapping(value = "/active-users",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<UserDTO>> getActiveUsers() {
        Collection<User> users = userService.getActiveAll();

        Collection<UserDTO> usersDTO = users.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(usersDTO, HttpStatus.OK);
    }

    /** url: /api/users/blocked-users GET*/
    @GetMapping(value = "/blocked-users",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<UserDTO>> getBlockedUsers() {
        Collection<User> users = userService.getBlockedAll();

        Collection<UserDTO> usersDTO = users.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(usersDTO, HttpStatus.OK);
    }

    /** url: /api/users/reported-users GET*/
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GUEST', 'ROLE_HOST')")
    @GetMapping(value = "/reported-users",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<UserDTO>> getReportedUsers() {
        Collection<User> users = userService.getReportedAll();

        Collection<UserDTO> usersDTO = users.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(usersDTO, HttpStatus.OK);
    }

    /** url: /api/users POST*/
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDto) throws Exception {
        User createdUser = null;

        try {
            createdUser = userService.create(UserMapper.toEntity(userDto));
            System.out.println(createdUser.getAddress());

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new UserDTO(),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(UserMapper.toDto(createdUser), HttpStatus.CREATED);
    }

    /** url: /api/users/1 PUT*/
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GUEST', 'ROLE_HOST')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO userDto, @PathVariable Long id)
            throws Exception {
        User userForUpdate = userService.getById(id);

        if (userForUpdate == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        userForUpdate.setFirstName(userDto.getFirstName());
        userForUpdate.setLastName(userDto.getLastName());
        if (userDto.getAddress() != null) {
            userForUpdate.setAddress(AddressMapper.toEntity(userDto.getAddress()));
        }
        userForUpdate.setEmail(userDto.getEmail());
        userForUpdate.setPassword(userDto.getPassword());
        userForUpdate.setPhone(userDto.getPhone());
        userForUpdate.setVerified(userDto.isVerified());
        if (userDto.getProfilePicture() != null) {
            userForUpdate.setProfilePicture(PhotoMapper.toEntity(userDto.getProfilePicture()));
        }
        userForUpdate.setBlocked(userDto.isBlocked());
        userForUpdate.setActive(userDto.isActive());


        userForUpdate = userService.save(userForUpdate);

        return new ResponseEntity<UserDTO>(UserMapper.toDto(userForUpdate), HttpStatus.OK);
    }

    /** url: /api/users/1 DELETE*/
    @PreAuthorize("hasAnyRole('HOST', 'GUEST')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable("id") Long id) {
        try {
            userService.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
    }

    private boolean validateUserDTO(UserDTO userDto) {
        return true;
    }

    /** url: /api/users/login POST*/
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LogInDTO loginDTO) {
        boolean loginSuccessful = userService.authenticateUser(loginDTO.getEmail(), loginDTO.getPassword());

        if (loginSuccessful) {
            // You might return a token or user details upon successful login
            // For simplicity, this example returns a success message
            return ResponseEntity.ok("Login successful");
        } else {
            // Return unauthorized status for failed login
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    /** url: /api/users/register-guest POST*/
    @PostMapping("/register-guest")
    public ResponseEntity<String> registerGuest(@RequestBody UserDTO userDTO) {
        // Your registration logic for guests here
        boolean registrationSuccessful = userService.registerGuest(userDTO);

        if (registrationSuccessful) {
            return ResponseEntity.ok("Guest registration successful");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Guest registration failed");
        }
    }

    /** url: /api/users/register-host POST*/
    @PostMapping("/register-host")
    public ResponseEntity<String> registerHost(@RequestBody UserDTO userDTO) {
        // Your registration logic for hosts here
        boolean registrationSuccessful = userService.registerHost(userDTO);

        if (registrationSuccessful) {
            return ResponseEntity.ok("Host registration successful");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Host registration failed");
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GUEST', 'ROLE_HOST')")
    @PutMapping(value = "/{id}/block", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> blockUser(@PathVariable("id") Long id)
            throws Exception {
        User user = userService.getById(id);
        if (user == null){
            return new ResponseEntity<UserDTO>(HttpStatus.NOT_FOUND);
        }

        if (!user.isActive() || user.isBlocked()){
            return new ResponseEntity<UserDTO>(HttpStatus.FORBIDDEN);
        }

        userService.blockUser(user);
        return new ResponseEntity<UserDTO>(UserMapper.toDto(user), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping(value = "/{id}/unblock", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> unblockUser(@PathVariable("id") Long id)
            throws Exception {
        User user = userService.getById(id);
        if (user == null){
            return new ResponseEntity<UserDTO>(HttpStatus.NOT_FOUND);
        }

        if (!user.isActive() || !user.isVerified() || !user.isBlocked()){
            return new ResponseEntity<UserDTO>(HttpStatus.FORBIDDEN);
        }

        userService.unblockUser(user);
        return new ResponseEntity<UserDTO>(UserMapper.toDto(user), HttpStatus.OK);
    }
}
