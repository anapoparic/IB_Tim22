package rs.ac.uns.ftn.asd.BookedUp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.uns.ftn.asd.BookedUp.domain.User;
import rs.ac.uns.ftn.asd.BookedUp.domain.UserReport;

import java.util.Collection;

public interface IUserReportRepository extends JpaRepository<UserReport, Long> {

    @Query("SELECT ur.reportedUser FROM UserReport ur WHERE ur.status = true AND ur.reportedUser.isBlocked = false" )
    Collection<User> findAllReportedUsers();

}
