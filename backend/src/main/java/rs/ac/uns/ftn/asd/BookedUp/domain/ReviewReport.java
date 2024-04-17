package rs.ac.uns.ftn.asd.BookedUp.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ReviewReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reason;

    @ManyToOne
    @JoinColumn(name = "reported_review_id")
    private Review reportedReview;

    @Column(nullable = false)
    private boolean status;


    public void copyValues(ReviewReport reviewReport) {
        this.reason = reviewReport.getReason();
        this.reportedReview = reviewReport.getReportedReview();
        this.status = reviewReport.isStatus();
    }

}
