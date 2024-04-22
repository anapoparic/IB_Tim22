package rs.ac.uns.ftn.asd.BookedUp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.uns.ftn.asd.BookedUp.domain.DateRange;
import rs.ac.uns.ftn.asd.BookedUp.domain.PriceChange;

public interface IPriceChangeRepository extends JpaRepository<PriceChange, Long> {

    @Modifying
    @Query("DELETE FROM PriceChange p WHERE p IN (SELECT pc FROM Accommodation a JOIN a.priceChanges pc WHERE a.id = :accommodationId)")
    void deleteByAccommodationId(@Param("accommodationId") Long accommodationId);
}
