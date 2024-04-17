package rs.ac.uns.ftn.asd.BookedUp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.uns.ftn.asd.BookedUp.domain.Accommodation;
import rs.ac.uns.ftn.asd.BookedUp.domain.Guest;
import rs.ac.uns.ftn.asd.BookedUp.domain.Host;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.AccommodationType;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.Amenity;

import java.util.Date;
import java.util.List;

public interface IAccommodationRepository extends JpaRepository<Accommodation, Long> {

    @Query("SELECT a FROM Accommodation a WHERE a.host.id = :hostId AND a.active = true "  )
    List<Accommodation> findAllByHostId(@Param("hostId") Long hostId);

    @Query("SELECT a FROM Accommodation a WHERE a.host.id = :hostId AND a.status = 'ACTIVE' "  )
    List<Accommodation> findAllActiveByHostId(@Param("hostId") Long hostId);

    @Query("SELECT a FROM Accommodation a WHERE a.host.id = :hostId AND a.status = 'REJECTED' "  )
    List<Accommodation> findAllRejectedByHostId(@Param("hostId") Long hostId);

    @Query("SELECT a FROM Accommodation a WHERE a.host.id = :hostId AND (a.status = 'CREATED' OR  a.status = 'CHANGED') ")
    List<Accommodation> findAllRequestsByHostId(@Param("hostId") Long hostId);

    @Query("SELECT a FROM Accommodation a WHERE  a.status = 'CREATED'")
    List<Accommodation> findAllCreated();

    @Query("SELECT a FROM Accommodation a WHERE  a.status = 'CHANGED'")
    List<Accommodation> findAllChanged();

    @Query("SELECT a FROM Accommodation a WHERE  a.status = 'CHANGED' OR a.status='CREATED'")
    List<Accommodation> findAllModified();

    @Query("SELECT a FROM Accommodation a WHERE  a.status = 'ACTIVE' AND a.active = true")
    List<Accommodation> findAllActive();

    @Query("SELECT a, COALESCE(COUNT(r.id), 0) AS reservationCount FROM Accommodation a " +
            "LEFT JOIN a.reservations r " +
            "ON r.status = 'COMPLETED' " +
            "WHERE a.status = 'ACTIVE' " +
            "GROUP BY a.id " +
            "ORDER BY COALESCE(COUNT(r.id), 0) DESC")
    List<Object[]> findMostPopular();


    @Query("SELECT a FROM Accommodation a " +
            "WHERE a.status = 'ACTIVE' " +
            "AND (:accommodationType IS NULL OR a.type = :accommodationType)")
    List<Accommodation> filterAccommodationsByType(
            @Param("accommodationType") AccommodationType accommodationType);

    @Query("SELECT DISTINCT r.guest FROM Accommodation a " +
            "INNER JOIN  a.reservations r " +
            "WHERE a.host = :host " +
            "AND (r.status = 'COMPLETED' OR r.status = 'ACCEPTED')")
    List<Guest> findGuestsByHostWithCompletedOrActiveReservations(@Param("host") Host host);


    @Query("SELECT DISTINCT a.host FROM Accommodation a " +
            "INNER JOIN a.reservations r " +
            "WHERE r.guest = :guest " +
            "AND (r.status = 'COMPLETED' OR r.status = 'ACCEPTED')")
    List<Host> findHostsByGuestWithCompletedOrActiveReservations(@Param("guest") Guest guest);

}
