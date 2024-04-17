package rs.ac.uns.ftn.asd.BookedUp.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.BookedUp.domain.*;
import rs.ac.uns.ftn.asd.BookedUp.dto.*;
import rs.ac.uns.ftn.asd.BookedUp.mapper.*;
import rs.ac.uns.ftn.asd.BookedUp.service.AdminService;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admins")
@CrossOrigin
public class AdminController {
    @Autowired
    private AdminService adminService;

    /*url: /api/admins GET*/
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<AdminDTO>> getAdmins() {
        Collection<Admin> admins = adminService.getAll();
        Collection<AdminDTO> adminsDTO = admins.stream()
                .map(AdminMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(adminsDTO, HttpStatus.OK);
    }

    /* url: /api/admins/1 GET*/
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminDTO> getAdmin(@PathVariable("id") Long id) {
        Admin admin = adminService.getById(id);

        if (admin == null) {
            return new ResponseEntity<AdminDTO>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<AdminDTO>(AdminMapper.toDto(admin), HttpStatus.OK);
    }

    /*url: /api/admins POST*/
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminDTO> createAdmin(@Valid @RequestBody AdminDTO adminDTO) throws Exception {
        Admin createdAdmin = null;

        try {
            createdAdmin = adminService.create(AdminMapper.toEntity(adminDTO));

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new AdminDTO(),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(AdminMapper.toDto(createdAdmin), HttpStatus.CREATED);
    }

    /* url: /api/admins/1 PUT*/
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AdminDTO> updateAdmin(@PathVariable Long id, @Valid @RequestBody AdminDTO adminDTO) throws Exception {
        Admin adminForUpdate = adminService.getById(id);
        if (adminForUpdate == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        adminForUpdate.setFirstName(adminDTO.getFirstName());
        adminForUpdate.setLastName(adminDTO.getLastName());

        if (adminDTO.getAddress() != null) {
            adminForUpdate.setAddress(AddressMapper.toEntity(adminDTO.getAddress()));
        }
        adminForUpdate.setEmail(adminDTO.getEmail());
        adminForUpdate.setPassword(adminDTO.getPassword());
        adminForUpdate.setPhone(adminDTO.getPhone());
        adminForUpdate.setVerified(adminDTO.isVerified());
        adminForUpdate.setActive(adminDTO.isActive());

        if (adminDTO.getProfilePicture() != null) {
            adminForUpdate.setProfilePicture(PhotoMapper.toEntity(adminDTO.getProfilePicture()));
        }
        adminForUpdate = adminService.save(adminForUpdate);

        return new ResponseEntity<AdminDTO>(AdminMapper.toDto(adminForUpdate), HttpStatus.OK);

    }

    /** url: /api/admins/1 DELETE*/
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Admin> deleteAdmin(@PathVariable("id") Long id) {
        try {
            adminService.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Admin>(HttpStatus.NO_CONTENT);
    }
}
