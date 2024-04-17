package rs.ac.uns.ftn.asd.BookedUp.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.BookedUp.domain.*;
import rs.ac.uns.ftn.asd.BookedUp.dto.*;
import rs.ac.uns.ftn.asd.BookedUp.mapper.*;
import rs.ac.uns.ftn.asd.BookedUp.service.AccommodationService;
import rs.ac.uns.ftn.asd.BookedUp.service.HostService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hosts")
@CrossOrigin
public class HostController {
    @Autowired
    private HostService hostService;

    @Autowired
    private AccommodationService accommodationService;

    /*url: /api/hosts GET*/
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<HostDTO>> getHosts() {
        Collection<Host> hosts = hostService.getAll();
        Collection<HostDTO> hostsDTO = hosts.stream()
                .map(HostMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(hostsDTO, HttpStatus.OK);
    }

    /* url: /api/hosts/1 GET*/
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HostDTO> getHost(@PathVariable("id") Long id) {
        Host host = hostService.getById(id);

        if (host == null) {
            return new ResponseEntity<HostDTO>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<HostDTO>(HostMapper.toDto(host), HttpStatus.OK);
    }

    /*url: /api/hosts POST*/
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HostDTO> createHost(@RequestBody HostDTO hostDto) throws Exception {
        Host createdHost = null;

        try {
            createdHost = hostService.create(HostMapper.toEntity(hostDto));

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new HostDTO(),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HostMapper.toDto(createdHost), HttpStatus.CREATED);
    }

    private boolean validateHostDTO(HostDTO hostDto) {
        return true;
    }

    /* url: /api/hosts/1 PUT*/
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GUEST', 'ROLE_HOST')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HostDTO> updateHost(@Valid @RequestBody HostDTO hostDTO, @PathVariable Long id)
            throws Exception {
        Host hostForUpdate = hostService.getById(id);
        if (hostForUpdate == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        hostForUpdate.setFirstName(hostDTO.getFirstName());
        hostForUpdate.setLastName(hostDTO.getLastName());
        if (hostDTO.getAddress() != null) {
            hostForUpdate.setAddress(AddressMapper.toEntity(hostDTO.getAddress()));
        }        hostForUpdate.setEmail(hostDTO.getEmail());
        hostForUpdate.setPassword(hostDTO.getPassword());
        hostForUpdate.setPhone(hostDTO.getPhone());
        hostForUpdate.setVerified(hostDTO.isVerified());
        if (hostDTO.getProfilePicture() != null) {
            hostForUpdate.setProfilePicture(PhotoMapper.toEntity(hostDTO.getProfilePicture()));
        }
        hostForUpdate.setBlocked(hostDTO.isBlocked());
        hostForUpdate.setActive(hostDTO.isActive());

        hostForUpdate.setAverageRating(hostDTO.getAverageRating());
        List<Accommodation> accommodations = new ArrayList<Accommodation>();
        List<Reservation> requests = new ArrayList<Reservation>();
//        if(hostDTO.getRequests() != null) {
//            for(ReservationDTO reservationDTO : hostDTO.getRequests())
//                requests.add(ReservationMapper.toEntity(reservationDTO));
//        }

//        List<Notification> notifications = new ArrayList<Notification>();
//        if(hostDTO.getNotifications() != null) {
//            for(NotificationDTO notificationDTO : hostDTO.getNotifications())
//                notifications.add(NotificationMapper.toEntity(notificationDTO));
//        }
//
//        List<Statistics> statistics = new ArrayList<Statistics>();
//        if(dto.getStatistics() != null) {
//            for(StatisticsDTO statisticsDTO : dto.getStatistics())
//                statistics.add(statisticsMapper.toEntity(statisticsDTO));
//        }
//
//        List<AccommodationStatistics> accommodationStatistics = new ArrayList<AccommodationStatistics>();
//        if(dto.getAccommodationStatistics() != null) {
//            for(AccommodationStatisticsDTO accommodationStatisticsDTO : dto.getAccommodationStatistics())
//                accommodationStatistics.add(accommodationStatisticsMapper.toEntity(accommodationStatisticsDTO));
//        }

//        hostForUpdate.setNotifications(notifications);
        //hostForUpdate.setRequests(requests);
        hostForUpdate.setReservationCreatedNotificationEnabled(hostDTO.isReservationCreatedNotificationEnabled());
        hostForUpdate.setCancellationNotificationEnabled(hostDTO.isCancellationNotificationEnabled());
        hostForUpdate.setHostRatingNotificationEnabled(hostDTO.isHostRatingNotificationEnabled());
        hostForUpdate.setAccommodationRatingNotificationEnabled(hostDTO.isAccommodationRatingNotificationEnabled());

        hostForUpdate = hostService.save(hostForUpdate);

        return new ResponseEntity<HostDTO>(HostMapper.toDto(hostForUpdate), HttpStatus.OK);
    }

    /** url: /api/hosts/1 DELETE*/
    @PreAuthorize("hasAuthority('ROLE_HOST')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteHost(@PathVariable("id") Long id) {
        Host host = hostService.getById(id);
        if( host == null ){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
           hostService.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GUEST', 'ROLE_HOST')")
    @GetMapping("/{id}/reservations/search")
    public ResponseEntity<?> searchReservations(
            @RequestParam(required = false) String accommodationName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(required = false) String status) {

        try {
            // Implementirajte logiku pretrage i filtriranja smeštaja koristeći AccommodationService
//            List<AccommodationDTO> filteredAccommodations = accommodationService.searchAndFilterAccommodations(
//                    location, guestsNumber, startDate, endDate, amenities, type, minPrice, maxPrice);

            HashMap<String, String> response = new HashMap<>();
            response.put("message", "Host search completed successfully!");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /* url: /api/hosts/{id}/guests GET */
    @PreAuthorize("hasAuthority('ROLE_HOST')")
    @GetMapping(value = "/{id}/guests", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GuestDTO>> getHostGuests(@PathVariable("id") Long hostId) {
        try {
            Collection<GuestDTO> guestsDTO = hostService.getGuestsByHostId(hostId).stream()
                    .map(GuestMapper::toDto) // Assuming you have a GuestMapper class
                    .collect(Collectors.toList());

            return new ResponseEntity<>(guestsDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
