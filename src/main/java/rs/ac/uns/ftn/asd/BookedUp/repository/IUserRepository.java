package rs.ac.uns.ftn.asd.BookedUp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.uns.ftn.asd.BookedUp.domain.User;

import java.util.Collection;
import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {

    public Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User u " +
            "SET u.active = TRUE WHERE u.email = ?1") //stojalo .enabled
    int enableUser(String email);

    @Query("SELECT u FROM User u WHERE u.isBlocked = TRUE")
    Collection<User> findAllBlockedUsers();


}
