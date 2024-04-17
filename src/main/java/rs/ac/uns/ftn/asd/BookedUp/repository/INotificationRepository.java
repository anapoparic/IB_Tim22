package rs.ac.uns.ftn.asd.BookedUp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.uns.ftn.asd.BookedUp.domain.Host;
import rs.ac.uns.ftn.asd.BookedUp.domain.Notification;

import java.util.Collection;

public interface INotificationRepository extends JpaRepository<Notification, Long> {


}
