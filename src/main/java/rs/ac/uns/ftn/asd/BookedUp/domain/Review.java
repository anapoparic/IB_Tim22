package rs.ac.uns.ftn.asd.BookedUp.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.ReviewType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int review;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private Host host;

    @ManyToOne
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewType type;

    @Column(nullable = false)
    private Boolean isReviewActive;

    @Column(nullable = false)
    private Boolean approved;

    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    public void copyValues(Review review) {
        this.id = review.getId();
        this.review = review.getReview();
        this.comment = review.getComment();
        this.date = review.getDate();
        this.host = review.getHost();
        this.accommodation = review.getAccommodation();
        this.type = review.getType();
        this.isReviewActive = review.isReviewActive;
        this.guest = review.getGuest();
    }
}
