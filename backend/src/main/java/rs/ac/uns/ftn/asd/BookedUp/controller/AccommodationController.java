package rs.ac.uns.ftn.asd.BookedUp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.BookedUp.domain.*;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.*;
import rs.ac.uns.ftn.asd.BookedUp.dto.*;
import rs.ac.uns.ftn.asd.BookedUp.mapper.*;
import rs.ac.uns.ftn.asd.BookedUp.service.AccommodationService;
import org.springframework.security.access.prepost.PreAuthorize;


import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/accommodations")
public class AccommodationController {
    @Autowired
    private AccommodationService accommodationService;

    /*url: /api/accommodations GET*/
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<AccommodationDTO>> getAccommodations() {
        Collection<Accommodation> accommodations = accommodationService.getAll();
        Collection<AccommodationDTO> accommodationsDTO = accommodations.stream()
                .map(AccommodationMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(accommodationsDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_HOST')")
    @GetMapping(value = "/host/{hostId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<AccommodationDTO>> getAllByHostId(@PathVariable("hostId") Long hostId) {
        Collection<Accommodation> accommodations = accommodationService.findAllByHostId(hostId);
        Collection<AccommodationDTO> accommodationDTOS = accommodations.stream()
                .map(AccommodationMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(accommodationDTOS, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_HOST')")
    @GetMapping(value = "/host/{hostId}/active", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<AccommodationDTO>> getAllActiveByHostId(@PathVariable("hostId") Long hostId) {
        Collection<Accommodation> accommodations = accommodationService.findAllActiveByHostId(hostId);
        Collection<AccommodationDTO> accommodationDTOS = accommodations.stream()
                .map(AccommodationMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(accommodationDTOS, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_HOST')")
    @GetMapping(value = "/host/{hostId}/requests", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<AccommodationDTO>> getAllRequestsByHostId(@PathVariable("hostId") Long hostId) {
        Collection<Accommodation> accommodations = accommodationService.findAllRequestsByHostId(hostId);
        Collection<AccommodationDTO> accommodationDTOS = accommodations.stream()
                .map(AccommodationMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(accommodationDTOS, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_HOST')")
    @GetMapping(value = "/host/{hostId}/rejected", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<AccommodationDTO>> getAllRejectedByHostId(@PathVariable("hostId") Long hostId) {
        Collection<Accommodation> accommodations = accommodationService.findAllRejectedByHostId(hostId);
        Collection<AccommodationDTO> accommodationDTOS = accommodations.stream()
                .map(AccommodationMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(accommodationDTOS, HttpStatus.OK);
    }

    /* url: /api/accommodations/1 GET*/
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccommodationDTO> getAccommodation(@PathVariable("id") Long id) {
        Accommodation accommodation = accommodationService.getById(id);

        if (accommodation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<AccommodationDTO>(AccommodationMapper.toDto(accommodation), HttpStatus.OK);
    }

    /*url: /api/accommodations POST*/
    @PreAuthorize("hasAuthority('ROLE_HOST')")
    @RequestMapping(method=RequestMethod.POST)
    public ResponseEntity<AccommodationDTO> createAccommodation(@Valid @RequestBody AccommodationDTO accommodationDTO) throws Exception {
        Accommodation createdAccommodation = null;

        try {
            createdAccommodation = accommodationService.create(AccommodationMapper.toEntity(accommodationDTO));

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new AccommodationDTO(),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(AccommodationMapper.toDto(createdAccommodation), HttpStatus.CREATED);
    }

    /* url: /api/accommodations/1 PUT*/
    @PreAuthorize("hasAuthority('ROLE_HOST')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccommodationDTO> updateAccommodation(@Valid @RequestBody AccommodationDTO accommodationDTO, @PathVariable Long id) throws Exception {
        Accommodation accommodationForUpdate = accommodationService.getById(id);
        if (accommodationForUpdate == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (accommodationService.hasActiveReservations(accommodationForUpdate.getId())){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        accommodationDTO.setId(id);
        Long idAdress = accommodationForUpdate.getAddress().getId();
        AddressDTO address = accommodationDTO.getAddress();
        address.setId(idAdress);
        accommodationDTO.setAddress(address);

        accommodationForUpdate = this.accommodationService.updateAccommodation(accommodationForUpdate, accommodationDTO);

        return new ResponseEntity<AccommodationDTO>(AccommodationMapper.toDto(accommodationForUpdate), HttpStatus.OK);
    }

    /** url: /api/accommodations/1 DELETE*/
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Accommodation> deleteAccommodation(@PathVariable("id") Long id) {
        try {
            accommodationService.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Accommodation>(HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping(value = "/{id}/confirmation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccommodationDTO> approveReservation(@PathVariable("id") Long id) throws Exception {
        Accommodation accommodation = accommodationService.getById(id);
        if (accommodation == null){
            return new ResponseEntity<AccommodationDTO>(HttpStatus.NOT_FOUND);
        }

        if (accommodation.getStatus() == AccommodationStatus.ACTIVE || accommodation.getStatus() == AccommodationStatus.REJECTED){
            return new ResponseEntity<AccommodationDTO>(HttpStatus.FORBIDDEN);
        }

//        da li dodati proveru za manualnu

        accommodationService.approveAccommodation(accommodation);
        return new ResponseEntity<AccommodationDTO>(AccommodationMapper.toDto(accommodation), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping(value = "/{id}/rejection", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccommodationDTO> rejectReservation(@PathVariable("id") Long id)  throws Exception {
        Accommodation accommodation = accommodationService.getById(id);
        if (accommodation == null){
            return new ResponseEntity<AccommodationDTO>(HttpStatus.NOT_FOUND);
        }

        if (accommodation.getStatus() == AccommodationStatus.ACTIVE || accommodation.getStatus() == AccommodationStatus.REJECTED){
            return new ResponseEntity<AccommodationDTO>(HttpStatus.FORBIDDEN);
        }

//        da li dodati proveru za manualnu
        accommodationService.rejectAccommodation(accommodation);
        return new ResponseEntity<AccommodationDTO>(AccommodationMapper.toDto(accommodation), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/modified", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<AccommodationDTO>> getAllModified() {
        Collection<Accommodation> accommodations = accommodationService.findAllModified();
        Collection<AccommodationDTO> accommodationsDTO = accommodations.stream()
                .map(AccommodationMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(accommodationsDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/created", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<AccommodationDTO>> getAllCreated() {
        Collection<Accommodation> accommodations = accommodationService.findAllCreated();
        Collection<AccommodationDTO> accommodationsDTO = accommodations.stream()
                .map(AccommodationMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(accommodationsDTO, HttpStatus.OK);
    }


    @GetMapping(value = "/mostPopular", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<AccommodationDTO>> getMostPopular() {
        Collection<Accommodation> accommodations = accommodationService.findMostPopular();
        Collection<AccommodationDTO> accommodationsDTO = accommodations.stream()
                .map(AccommodationMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(accommodationsDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/changed", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<AccommodationDTO>> getAllChanged() {
        Collection<Accommodation> accommodations = accommodationService.findAllChanged();
        Collection<AccommodationDTO> accommodationsDTO = accommodations.stream()
                .map(AccommodationMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(accommodationsDTO, HttpStatus.OK);
    }

    @GetMapping("/search-filters")
    public ResponseEntity<List<AccommodationDTO>> searchAccommodationsFilters(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer guestsNumber,
            @RequestParam(required = false)  Date startDate,
            @RequestParam(required = false)  Date endDate,
            @RequestParam(required = false) List<Object> amenities,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double customMaxBudget,
            @RequestParam(required = false) Object selectedType,
            @RequestParam(required = false) String name
    ) {
        String lowercaseName = name.toLowerCase();
        try {
            //SEARCH
            List<Accommodation> searchedAccommodations = accommodationService.searchAccommodations(
                    location, guestsNumber, startDate, endDate);
            for (Accommodation accommodation : searchedAccommodations) {
                accommodationService.updatePrice(accommodation);
                System.out.println(accommodation.getPrice());
            }

            List<AccommodationDTO> results = searchedAccommodations.stream()
                    .map(AccommodationMapper::toDto)
                    .collect(Collectors.toList());

            long differenceInMilliseconds = endDate.getTime() - startDate.getTime();
            long daysBetween = TimeUnit.DAYS.convert(differenceInMilliseconds, TimeUnit.MILLISECONDS);

            for (AccommodationDTO accommodationDTO : results){
                if (accommodationDTO.getPriceType().equals(PriceType.PER_NIGHT)) {
                    double totalPrice = accommodationService.calculateTotalPrice(AccommodationMapper.toEntity(accommodationDTO), startDate, endDate, (int)daysBetween, 1);
                    accommodationDTO.setTotalPrice(totalPrice);
                } else {
                    double totalPrice = accommodationService.calculateTotalPrice(AccommodationMapper.toEntity(accommodationDTO), startDate, endDate, (int)daysBetween,guestsNumber);
                    accommodationDTO.setTotalPrice(totalPrice);
                }
            }
//            //FILTER
            List<AccommodationDTO> filteredAccommodations = results.stream()
                    .filter(accommodationDTO ->
                            ((customMaxBudget == null || customMaxBudget == 0 || accommodationDTO.getTotalPrice() <= customMaxBudget) &&
                                    (amenities == null || amenities.isEmpty() || amenities.stream().allMatch(amenity -> accommodationDTO.getAmenities().stream().anyMatch(accAmenity -> accAmenity instanceof Amenity && ((Amenity) accAmenity).name().equals((String) amenity)))) &&
                                    (minPrice == null || minPrice == 0 || (accommodationDTO.getTotalPrice() >= minPrice)) &&
                                    (maxPrice == null || maxPrice == 0 || (accommodationDTO.getTotalPrice() <= maxPrice)) &&
                                    (selectedType == null || selectedType.equals("null") || accommodationDTO.getType().name().equals(selectedType)) &&
                                    (name == null || name.isEmpty() || (accommodationDTO.getName().trim().toLowerCase().contains(lowercaseName)))))
                    .collect(Collectors.toList());

            if (!filteredAccommodations.isEmpty() || (amenities != null || minPrice != 0.0 || maxPrice != 0.0 || customMaxBudget > 0.0 || !selectedType.equals("null") || !name.isEmpty())) {
                return new ResponseEntity<>(filteredAccommodations, HttpStatus.OK);
            }
            return new ResponseEntity<>(results, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search-filter")
    public ResponseEntity<List<AccommodationDTO>> searchAccommodations(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer guestsNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @RequestParam(required = false) List<Object> amenities,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double customMaxBudget,
            @RequestParam(required = false) Object selectedType,
            @RequestParam(required = false) String name
    ) {
        String lowercaseName = name.toLowerCase();
        try {
            //SEARCH
            List<Accommodation> searchedAccommodations = accommodationService.searchAccommodations(
                    location, guestsNumber, startDate, endDate);
            for (Accommodation accommodation : searchedAccommodations) {
                accommodationService.updatePrice(accommodation);
                System.out.println(accommodation.getPrice());
            }

            List<AccommodationDTO> results = searchedAccommodations.stream()
                    .map(AccommodationMapper::toDto)
                    .collect(Collectors.toList());

            long differenceInMilliseconds = endDate.getTime() - startDate.getTime();
            long daysBetween = TimeUnit.DAYS.convert(differenceInMilliseconds, TimeUnit.MILLISECONDS);

            for (AccommodationDTO accommodationDTO : results){
                if (accommodationDTO.getPriceType().equals(PriceType.PER_NIGHT)) {
                    double totalPrice = accommodationService.calculateTotalPrice(AccommodationMapper.toEntity(accommodationDTO), startDate, endDate, (int)daysBetween, 1);
                    accommodationDTO.setTotalPrice(totalPrice);
                } else {
                    double totalPrice = accommodationService.calculateTotalPrice(AccommodationMapper.toEntity(accommodationDTO), startDate, endDate, (int)daysBetween,guestsNumber);
                    accommodationDTO.setTotalPrice(totalPrice);
                }
            }
//            //FILTER
            List<AccommodationDTO> filteredAccommodations = results.stream()
                    .filter(accommodationDTO ->
                            ((customMaxBudget == null || customMaxBudget == 0 || accommodationDTO.getTotalPrice() <= customMaxBudget) &&
                                    (amenities == null || amenities.isEmpty() || amenities.stream().allMatch(amenity -> accommodationDTO.getAmenities().stream().anyMatch(accAmenity -> accAmenity instanceof Amenity && ((Amenity) accAmenity).name().equals((String) amenity)))) &&
                                    (minPrice == null || minPrice == 0 || (accommodationDTO.getTotalPrice() >= minPrice)) &&
                                    (maxPrice == null || maxPrice == 0 || (accommodationDTO.getTotalPrice() <= maxPrice)) &&
                                    (selectedType == null || selectedType.equals("null") || accommodationDTO.getType().name().equals(selectedType)) &&
                                    (name == null || name.isEmpty() || (accommodationDTO.getName().trim().toLowerCase().contains(lowercaseName)))))
                    .collect(Collectors.toList());

            if (!filteredAccommodations.isEmpty() || (amenities != null || minPrice != 0.0 || maxPrice != 0.0 || customMaxBudget > 0.0 || !selectedType.equals("null") || !name.isEmpty())) {
                return new ResponseEntity<>(filteredAccommodations, HttpStatus.OK);
            }
            return new ResponseEntity<>(results, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}

