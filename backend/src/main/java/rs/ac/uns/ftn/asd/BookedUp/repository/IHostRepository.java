package rs.ac.uns.ftn.asd.BookedUp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rs.ac.uns.ftn.asd.BookedUp.domain.Guest;
import rs.ac.uns.ftn.asd.BookedUp.domain.Host;

import java.util.Collection;
import java.util.List;

public interface IHostRepository extends JpaRepository<Host, Long> {

    @Query(value = "SELECT * FROM USERS WHERE TYPE = 'host'", nativeQuery = true)
    List<Host> findAllHosts();
}
