package rs.ac.uns.ftn.asd.BookedUp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rs.ac.uns.ftn.asd.BookedUp.domain.Admin;
import rs.ac.uns.ftn.asd.BookedUp.domain.Guest;

import java.util.Collection;
import java.util.List;

public interface IAdminRepository extends JpaRepository<Admin, Long> {

    @Query(value = "SELECT * FROM USERS WHERE TYPE = 'admin'", nativeQuery = true)
    List<Admin> findAllAdmins();
}
