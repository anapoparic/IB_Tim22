package rs.ac.uns.ftn.asd.BookedUp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rs.ac.uns.ftn.asd.BookedUp.domain.Guest;

import java.util.List;
import java.util.Optional;

public interface IGuestRepository extends JpaRepository<Guest, Long> {

    @Query(value = "SELECT * FROM USERS WHERE TYPE = 'guest'", nativeQuery = true)
    List<Guest> findAllGuest();
}
