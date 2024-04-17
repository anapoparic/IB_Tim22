package rs.ac.uns.ftn.asd.BookedUp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.BookedUp.domain.Guest;
import rs.ac.uns.ftn.asd.BookedUp.domain.Host;
import rs.ac.uns.ftn.asd.BookedUp.domain.Photo;
import rs.ac.uns.ftn.asd.BookedUp.domain.User;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.Role;
import rs.ac.uns.ftn.asd.BookedUp.dto.GuestDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.HostDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.TokenDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.UserDTO;
import rs.ac.uns.ftn.asd.BookedUp.mapper.*;
import rs.ac.uns.ftn.asd.BookedUp.service.HostService;
import rs.ac.uns.ftn.asd.BookedUp.service.RegistrationService;
import rs.ac.uns.ftn.asd.BookedUp.service.UserService;

import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/registration")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserService userService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String register(@RequestBody UserDTO userDTO) {
        User existingUser = userService.getByEmail(userDTO.getEmail());

        if (existingUser != null && !existingUser.isActive()) {
            existingUser.setFirstName(userDTO.getFirstName());
            existingUser.setLastName(userDTO.getLastName());
            existingUser.setAddress(AddressMapper.toEntity(userDTO.getAddress()));
            existingUser.setPhone(userDTO.getPhone());
            existingUser.setPassword(userDTO.getPassword());
            existingUser.setRole(userDTO.getRole());
            existingUser.setProfilePicture(PhotoMapper.toEntity(userDTO.getProfilePicture()));
            existingUser.setActive(false);
            existingUser.setVerified(false);

            return registrationService.register(existingUser);
        } else {
            // Ako korisnik ne postoji ili je aktivan, nastavi sa registracijom kao i pre
            if (userDTO.getRole() == Role.HOST) {
                Host host = new Host();

                host.setFirstName(userDTO.getFirstName());
                host.setLastName(userDTO.getLastName());
                host.setAddress(AddressMapper.toEntity(userDTO.getAddress()));
                host.setPhone(userDTO.getPhone());
                host.setEmail(userDTO.getEmail());
                host.setPassword(userDTO.getPassword());
                host.setRole(userDTO.getRole());

                host.setProfilePicture(PhotoMapper.toEntity(userDTO.getProfilePicture()));

                host.setActive(false);
                host.setVerified(false);

                return registrationService.register(host);
            } else if (userDTO.getRole() == Role.GUEST) {
                Guest guest = new Guest();

                guest.setFirstName(userDTO.getFirstName());
                guest.setLastName(userDTO.getLastName());
                guest.setAddress(AddressMapper.toEntity(userDTO.getAddress()));
                guest.setPhone(userDTO.getPhone());
                guest.setEmail(userDTO.getEmail());
                guest.setPassword(userDTO.getPassword());
                guest.setRole(userDTO.getRole());

                guest.setProfilePicture(PhotoMapper.toEntity(userDTO.getProfilePicture()));

                guest.setActive(false);
                guest.setVerified(false);

                return registrationService.register(guest);
            } else {
                throw new IllegalArgumentException("Nepodržana uloga: " + userDTO.getRole());
            }
        }
    }


    @PostMapping(value = "/android", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TokenDTO androidRegister(@RequestBody UserDTO userDTO) {
        User existingUser = userService.getByEmail(userDTO.getEmail());

        if (existingUser != null && !existingUser.isActive()) {
            existingUser.setFirstName(userDTO.getFirstName());
            existingUser.setLastName(userDTO.getLastName());
            existingUser.setAddress(AddressMapper.toEntity(userDTO.getAddress()));
            existingUser.setPhone(userDTO.getPhone());
            existingUser.setPassword(userDTO.getPassword());
            existingUser.setRole(userDTO.getRole());
            existingUser.setProfilePicture(PhotoMapper.toEntity(userDTO.getProfilePicture()));
            existingUser.setActive(false);
            existingUser.setVerified(false);

            return registrationService.androidRegister(existingUser);
        } else {
            if (userDTO.getRole() == Role.HOST) {
                Host host = new Host();

                host.setFirstName(userDTO.getFirstName());
                host.setLastName(userDTO.getLastName());
                host.setAddress(AddressMapper.toEntity(userDTO.getAddress()));
                host.setPhone(userDTO.getPhone());
                host.setEmail(userDTO.getEmail());
                host.setPassword(userDTO.getPassword());
                host.setRole(userDTO.getRole());

                host.setProfilePicture(PhotoMapper.toEntity(userDTO.getProfilePicture()));

                host.setActive(false);
                host.setVerified(false);

                return registrationService.androidRegister(host);
            } else if (userDTO.getRole() == Role.GUEST) {
                Guest guest = new Guest();

                guest.setFirstName(userDTO.getFirstName());
                guest.setLastName(userDTO.getLastName());
                guest.setAddress(AddressMapper.toEntity(userDTO.getAddress()));
                guest.setPhone(userDTO.getPhone());
                guest.setEmail(userDTO.getEmail());
                guest.setPassword(userDTO.getPassword());
                guest.setRole(userDTO.getRole());

                guest.setProfilePicture(PhotoMapper.toEntity(userDTO.getProfilePicture()));

                guest.setActive(false);
                guest.setVerified(false);

                return registrationService.androidRegister(guest);
            } else {
                throw new IllegalArgumentException("Nepodržana uloga: " + userDTO.getRole());
            }
        }
    }


    @GetMapping()
    public String confirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }
}
