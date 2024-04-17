package rs.ac.uns.ftn.asd.BookedUp.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.BookedUp.domain.Review;
import rs.ac.uns.ftn.asd.BookedUp.domain.ReviewReport;
import rs.ac.uns.ftn.asd.BookedUp.domain.User;
import rs.ac.uns.ftn.asd.BookedUp.dto.AccommodationDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.ReviewDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.ReviewReportDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.UserDTO;
import rs.ac.uns.ftn.asd.BookedUp.mapper.ReviewMapper;
import rs.ac.uns.ftn.asd.BookedUp.mapper.ReviewReportMapper;
import rs.ac.uns.ftn.asd.BookedUp.mapper.UserMapper;
import rs.ac.uns.ftn.asd.BookedUp.service.ReviewReportService;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/review-reports")
@CrossOrigin
public class ReviewReportController {
    @Autowired
    private ReviewReportService reviewReportService;

    /*url: /api/review-reports GET*/
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ReviewReportDTO>> getReviewReports() {
        Collection<ReviewReport> reviewReports = reviewReportService.getAll();
        Collection<ReviewReportDTO> reviewReportsDTO = reviewReports.stream()
                .map(ReviewReportMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(reviewReportsDTO, HttpStatus.OK);
    }

    /* url: /api/review-reports/1 GET*/
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewReportDTO> getReviewReport(@PathVariable("id") Long id) {
        ReviewReport reviewReport = reviewReportService.getById(id);

        if (reviewReport == null) {
            return new ResponseEntity<ReviewReportDTO>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<ReviewReportDTO>(ReviewReportMapper.toDto(reviewReport), HttpStatus.OK);
    }

    /*url: /api/review-reports POST*/
    @PreAuthorize("hasAuthority('ROLE_HOST')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewReportDTO> createReviewReport(@Valid @RequestBody ReviewReportDTO reviewReportDTO) throws Exception {
        ReviewReport createdReviewReport = null;

        try {
            createdReviewReport = reviewReportService.create(ReviewReportMapper.toEntity(reviewReportDTO));

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ReviewReportDTO(),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(ReviewReportMapper.toDto(createdReviewReport), HttpStatus.CREATED);
    }

    /* url: /api/review-reports/1 PUT*/
    @PreAuthorize("hasAnyRole('HOST', 'ADMIN')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewReportDTO> updateReviewReport(@Valid @RequestBody ReviewReportDTO reviewReportDTO, @PathVariable Long id)
            throws Exception {
        ReviewReport reviewReportForUpdate = reviewReportService.getById(id);
        if (reviewReportForUpdate == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        reviewReportForUpdate.setReason(reviewReportDTO.getReason());
        reviewReportForUpdate.setReportedReview(ReviewMapper.toEntity(reviewReportDTO.getReportedReview()));
        reviewReportForUpdate.setStatus(reviewReportDTO.isStatus());

        reviewReportForUpdate = reviewReportService.save(reviewReportForUpdate);

        return new ResponseEntity<ReviewReportDTO>(ReviewReportMapper.toDto(reviewReportForUpdate), HttpStatus.OK);
    }

    /** url: /api/review-reports/1 DELETE*/
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ReviewReport> deleteReviewReport(@PathVariable("id") Long id) {
        try {
            reviewReportService.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*url: /api/review-reports/reported-reviews GET*/
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/reported-reviews", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ReviewDTO>> getAllReportedReviews() {
        Collection<Review> reportedReviews = reviewReportService.getAllReportedReviews();
        Collection<ReviewDTO> reportedReviewsDTO = reportedReviews.stream()
                .map(ReviewMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(reportedReviewsDTO, HttpStatus.OK);
    }


    /* url: /api/review-reports/reasons/{reportReviewId} GET */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/reasons/{reportReviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<String>> getReportReasons(@PathVariable("reportReviewId") Long reportReviewId) {
        Collection<String> reportReasons = reviewReportService.getReportReasonsForReview(reportReviewId);
        return new ResponseEntity<>(reportReasons, HttpStatus.OK);
    }

}
