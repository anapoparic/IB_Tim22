package rs.ac.uns.ftn.asd.BookedUp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.asd.BookedUp.domain.Review;
import rs.ac.uns.ftn.asd.BookedUp.domain.ReviewReport;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReportDTO {
    private Long id;
    private String reason;
    private ReviewDTO reportedReview;
    private boolean status;

}
