package rs.ac.uns.ftn.asd.BookedUp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rs.ac.uns.ftn.asd.BookedUp.domain.Reservation;
import rs.ac.uns.ftn.asd.BookedUp.domain.Review;
import rs.ac.uns.ftn.asd.BookedUp.domain.ReviewReport;
import rs.ac.uns.ftn.asd.BookedUp.domain.User;

import java.util.Collection;

public interface IReviewReportRepository extends JpaRepository<ReviewReport, Long> {
    @Query("SELECT ur.reportedReview FROM ReviewReport ur WHERE ur.status = true AND ur.reportedReview.isReviewActive = true" )
    Collection<Review> findAllReportedReviews();
}
