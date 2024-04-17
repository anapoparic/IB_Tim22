package rs.ac.uns.ftn.asd.BookedUp.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.BookedUp.domain.*;
import rs.ac.uns.ftn.asd.BookedUp.dto.*;
import rs.ac.uns.ftn.asd.BookedUp.mapper.*;
import rs.ac.uns.ftn.asd.BookedUp.service.GuestService;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/guests")
@CrossOrigin
public class GuestController {
    @Autowired
    private GuestService guestService;

    /*url: /api/guests GET*/
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GuestDTO>> getGuests() {
        Collection<Guest> guests = guestService.getAll();
        Collection<GuestDTO> guestsDTO = guests.stream()
                .map(GuestMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(guestsDTO, HttpStatus.OK);
    }

    /* url: /api/guests/1 GET*/
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GuestDTO> getGuest(@PathVariable("id") Long id) {
        Guest guest = guestService.getById(id);

        if (guest == null) {
            return new ResponseEntity<GuestDTO>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<GuestDTO>(GuestMapper.toDto(guest), HttpStatus.OK);
    }

    /*url: /api/guests POST*/
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GuestDTO> createGuest(@RequestBody GuestDTO guestDto) throws Exception {
        Guest guest = null;

        try {
            guest = guestService.create(GuestMapper.toEntity(guestDto));

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new GuestDTO(),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(GuestMapper.toDto(guest), HttpStatus.CREATED);
    }

    private boolean validateGuestDTO(GuestDTO guestDto) {
        return true;
    }

    /* url: /api/guests/1 PUT*/
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GUEST', 'ROLE_HOST')")
    @PutMapping("/{id}")
    public ResponseEntity<GuestDTO> updateGuest(@PathVariable Long id, @Valid @RequestBody GuestDTO guestDTO) throws Exception {
        Guest guestForUpdate = guestService.getById(id);
        if (guestForUpdate == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        guestForUpdate.setFirstName(guestDTO.getFirstName());
        guestForUpdate.setLastName(guestDTO.getLastName());
        if (guestDTO.getAddress() != null) {
            guestForUpdate.setAddress(AddressMapper.toEntity(guestDTO.getAddress()));
        }
        guestForUpdate.setEmail(guestDTO.getEmail());
        guestForUpdate.setPassword(guestDTO.getPassword());
        guestForUpdate.setPhone(guestDTO.getPhone());
        guestForUpdate.setVerified(guestDTO.isVerified());
        if (guestDTO.getProfilePicture() != null) {
            guestForUpdate.setProfilePicture(PhotoMapper.toEntity(guestDTO.getProfilePicture()));
        }
        guestForUpdate.setBlocked(guestDTO.isBlocked());
        guestForUpdate.setActive(guestDTO.isActive());

        List<Accommodation> favourites = new ArrayList<Accommodation>();
        if(guestDTO.getFavourites() != null) {
            for(AccommodationDTO accommodationDTO : guestDTO.getFavourites())
                favourites.add(AccommodationMapper.toEntity(accommodationDTO));
        }
        guestForUpdate.setFavourites(favourites);
        guestForUpdate.setNotificationEnable(guestDTO.isNotificationEnable());

        guestForUpdate = guestService.save(guestForUpdate);

        return new ResponseEntity<GuestDTO>(GuestMapper.toDto(guestForUpdate), HttpStatus.OK);
    }

    /** url: /api/guests/1 DELETE*/
    @PreAuthorize("hasAuthority('ROLE_GUEST')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteGuest(@PathVariable("id") Long id) {

        Guest guest = guestService.getById(id);
        if( guest == null ){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            guestService.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /* url: /api/guests/{id}/hosts GET */
    @PreAuthorize("hasRole('ROLE_GUEST')")
    @GetMapping(value = "/{id}/hosts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<HostDTO>> getGuestHosts(@PathVariable("id") Long guestId) {
        try {
            Collection<HostDTO> hostDTOS = guestService.getHostsByGuestId(guestId).stream()
                    .map(HostMapper::toDto) // Assuming you have a GuestMapper class
                    .collect(Collectors.toList());

            return new ResponseEntity<>(hostDTOS, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ROLE_GUEST')")
    @PutMapping("/{guestId}/add-favourite/{accommodationId}")
    public ResponseEntity<Void> addFavouriteAccommodation(
            @PathVariable Long guestId,
            @PathVariable Long accommodationId
    ) {
        try {
            guestService.addFavouriteAccommodation(guestId, accommodationId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_GUEST')")
    @PutMapping("/{guestId}/remove-favourite/{accommodationId}")
    public ResponseEntity<Void> removeFavouriteAccommodation(
            @PathVariable Long guestId,
            @PathVariable Long accommodationId
    ) {
        try {
            guestService.removeFavouriteAccommodation(guestId, accommodationId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_GUEST')")
    @GetMapping("/{guestId}/is-favourite/{accommodationId}")
    public ResponseEntity<Boolean> isFavouriteAccommodation(
            @PathVariable Long guestId,
            @PathVariable Long accommodationId
    ) {
        try {
            boolean isFavourite = guestService.isFavouriteAccommodation(guestId, accommodationId);
            return new ResponseEntity<>(isFavourite, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
