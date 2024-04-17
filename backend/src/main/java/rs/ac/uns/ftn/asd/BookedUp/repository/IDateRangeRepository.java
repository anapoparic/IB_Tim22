package rs.ac.uns.ftn.asd.BookedUp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.uns.ftn.asd.BookedUp.domain.DateRange;

public interface IDateRangeRepository extends JpaRepository<DateRange, Long> {

    @Modifying
    @Query("DELETE FROM DateRange d WHERE d IN (SELECT dr FROM Accommodation a JOIN a.availability dr WHERE a.id = :accommodationId)")
    void deleteByAccommodationId(@Param("accommodationId") Long accommodationId);


}
