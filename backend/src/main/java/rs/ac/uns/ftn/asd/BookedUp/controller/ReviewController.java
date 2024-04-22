package rs.ac.uns.ftn.asd.BookedUp.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.BookedUp.domain.Accommodation;
import rs.ac.uns.ftn.asd.BookedUp.domain.Review;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.AccommodationStatus;
import rs.ac.uns.ftn.asd.BookedUp.dto.AccommodationDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.ReservationDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.ReviewDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.UserDTO;
import rs.ac.uns.ftn.asd.BookedUp.mapper.AccommodationMapper;
import rs.ac.uns.ftn.asd.BookedUp.mapper.HostMapper;
import rs.ac.uns.ftn.asd.BookedUp.mapper.ReviewMapper;
import rs.ac.uns.ftn.asd.BookedUp.mapper.UserMapper;
import rs.ac.uns.ftn.asd.BookedUp.service.ReviewService;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    /*url: /api/reviews GET*/
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ReviewDTO>> getReviews() {
        Collection<Review> reviews = reviewService.getAll();
        Collection<ReviewDTO> reviewsDTO = reviews.stream()
                .map(ReviewMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(reviewsDTO, HttpStatus.OK);
    }

    /* url: /api/reviews/1 GET*/
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewDTO> getReview(@PathVariable("id") Long id) {
        Review review = reviewService.getById(id);

        if (review == null) {
            return new ResponseEntity<ReviewDTO>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<ReviewDTO>(ReviewMapper.toDto(review), HttpStatus.OK);
    }

    /* url: /api/reviews/accommodation/1 GET*/
    @GetMapping(value = "/accommodation/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ReviewDTO>> getAccommodationReviews(@PathVariable("id") Long id) {
        Collection<Review> reviews = reviewService.findAllByAccommodationId(id);
        Collection<ReviewDTO> reviewsDTO = reviews.stream()
                .map(ReviewMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(reviewsDTO, HttpStatus.OK);
    }

    /*url: /api/reviews POST*/
    @PreAuthorize("hasAuthority('ROLE_GUEST')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDTO) throws Exception {
        Review createdReview = null;

        try {
            createdReview = reviewService.create(ReviewMapper.toEntity(reviewDTO));

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ReviewDTO(),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(ReviewMapper.toDto(createdReview), HttpStatus.CREATED);
    }

    /* url: /api/reviews/1 PUT*/
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GUEST', 'ROLE_HOST')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewDTO> updateReview(@Valid @RequestBody ReviewDTO reviewDTO, @PathVariable Long id)
            throws Exception {
        Review reviewForUpdate = reviewService.getById(id);
        if (reviewForUpdate == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //reviewForUpdate.setGuest(reviewDTO.getGuest());
        reviewForUpdate.setReview(reviewDTO.getReview());
        reviewForUpdate.setComment(reviewDTO.getComment());
        reviewForUpdate.setDate(reviewDTO.getDate());
        reviewForUpdate.setHost(HostMapper.toEntity(reviewDTO.getHost()));
        reviewForUpdate.setAccommodation(AccommodationMapper.toEntity(reviewDTO.getAccommodation()));
        reviewForUpdate.setType(reviewDTO.getType());

        reviewForUpdate = reviewService.save(reviewForUpdate);

        return new ResponseEntity<ReviewDTO>(ReviewMapper.toDto(reviewForUpdate), HttpStatus.OK);
    }

    /** url: /api/reviews/1 DELETE*/
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GUEST', 'ROLE_HOST')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Review> deleteReview(@PathVariable("id") Long id) {
        try {
            reviewService.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Review>(HttpStatus.NO_CONTENT);
    }

    /* url: /api/reviews/guest/{guestId} GET*/
    @PreAuthorize("hasAuthority('ROLE_GUEST')")
    @GetMapping(value = "/guest/{guestId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ReviewDTO>> getReviewsByGuestId(@PathVariable("guestId") Long guestId) {
        Collection<Review> reviews = reviewService.findAllByGuestId(guestId);
        Collection<ReviewDTO> reviewsDTO = reviews.stream()
                .map(ReviewMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(reviewsDTO, HttpStatus.OK);
    }

    /* url: /api/reviews/guest/{guestId}/accommodation GET*/
    @PreAuthorize("hasAuthority('ROLE_GUEST')")
    @GetMapping(value = "/guest/{guestId}/accommodation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ReviewDTO>> getAccommodationReviewsByGuestId(@PathVariable("guestId") Long guestId) {
        Collection<Review> reviews = reviewService.findAllAccommodationReviewsByGuestId(guestId);
        Collection<ReviewDTO> reviewsDTO = reviews.stream()
                .map(ReviewMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(reviewsDTO, HttpStatus.OK);
    }

    /* url: /api/reviews/guest/{guestId}/host GET*/
    @PreAuthorize("hasAuthority('ROLE_GUEST')")
    @GetMapping(value = "/guest/{guestId}/host", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ReviewDTO>> getHostReviewsByGuestId(@PathVariable("guestId") Long guestId) {
        Collection<Review> reviews = reviewService.findAllHostReviewsByGuestId(guestId);
        Collection<ReviewDTO> reviewsDTO = reviews.stream()
                .map(ReviewMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(reviewsDTO, HttpStatus.OK);
    }

    /* url: /api/reviews/host/{hostId}/accommodation GET*/
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GUEST', 'ROLE_HOST')")
    @GetMapping(value = "/host/{hostId}/accommodation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ReviewDTO>> getAccommodationReviewsByHostId(@PathVariable("hostId") Long hostId) {
        Collection<Review> reviews = reviewService.findAllAccommodationReviewsByHostId(hostId);
        Collection<ReviewDTO> reviewsDTO = reviews.stream()
                .map(ReviewMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(reviewsDTO, HttpStatus.OK);
    }

    /* url: /api/reviews/host/{hostId}/host GET*/
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GUEST', 'ROLE_HOST')")
    @GetMapping(value = "/host/{hostId}/host", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ReviewDTO>> getHostReviewsByHostId(@PathVariable("hostId") Long hostId) {
        Collection<Review> reviews = reviewService.findAllHostReviewsByHostId(hostId);
        Collection<ReviewDTO> reviewsDTO = reviews.stream()
                .map(ReviewMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(reviewsDTO, HttpStatus.OK);
    }

    /* url: /api/reviews/host/{hostId} GET*/
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GUEST', 'ROLE_HOST')")
    @GetMapping(value = "/host/{hostId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ReviewDTO>> getReviewsByHostId(@PathVariable("hostId") Long hostId) {
        Collection<ReviewDTO> accommodationReviews = getAccommodationReviewsByHostId(hostId).getBody();
        Collection<ReviewDTO> hostReviews = getHostReviewsByHostId(hostId).getBody();

        if (accommodationReviews == null) {
            accommodationReviews = Collections.emptyList();
        }

        if (hostReviews == null) {
            hostReviews = Collections.emptyList();
        }

        Collection<ReviewDTO> mergedReviews = Stream.concat(accommodationReviews.stream(), hostReviews.stream())
                .collect(Collectors.toList());

        return new ResponseEntity<>(mergedReviews, HttpStatus.OK);
    }

    /* url: /api/reviews/unapproved GET */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GUEST', 'ROLE_HOST')")
    @GetMapping(value = "/unapproved", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ReviewDTO>> getUnapprovedReviews() {
        Collection<Review> unapprovedReviews = reviewService.findAllUnapprovedReviews();
        Collection<ReviewDTO> unapprovedReviewsDTO = unapprovedReviews.stream()
                .map(ReviewMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(unapprovedReviewsDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping(value = "/{id}/confirmation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewDTO> approveReview(@PathVariable("id") Long id)
            throws Exception {
        Review review = reviewService.getById(id);
        if (review == null){
            return new ResponseEntity<ReviewDTO>(HttpStatus.NOT_FOUND);
        }

        reviewService.approveReview(review);
        return new ResponseEntity<ReviewDTO>(ReviewMapper.toDto(review), HttpStatus.OK);
    }
}
