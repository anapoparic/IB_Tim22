package rs.ac.uns.ftn.asd.BookedUp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.Role;

import java.sql.Timestamp;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("HOST")
public class Host extends User {

    @Column(unique = false, nullable = true)
    private double averageRating;

//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name = "host_id", nullable = true)
//    private List<Reservation> requests;

//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name = "host_id", nullable = true)
//    private List<Statistics> statistics;
//
//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name = "host_id", nullable = true)
//    private List<AccommodationStatistics> accommodationStatistics;

    @Column(nullable = true)
    private boolean reservationCreatedNotificationEnabled = true;

    @Column(nullable = true)
    private boolean cancellationNotificationEnabled = true;

    @Column(nullable = true)
    private boolean hostRatingNotificationEnabled = true;

    @Column(nullable = true)
    private boolean accommodationRatingNotificationEnabled = true;


    public Host(Long id, String firstName, String lastName, Address address, Integer phone, String email, String password, boolean isBlocked, boolean active, boolean verified, Photo profilePicture, Role role, Timestamp lastPasswordResetDate, String jwt, double averageRating, boolean reservationCreatedNotificationEnabled, boolean cancellationNotificationEnabled, boolean hostRatingNotificationEnabled, boolean accommodationRatingNotificationEnabled) {
        super(id, firstName, lastName, address, phone, email, password, isBlocked, active, verified, profilePicture, role, lastPasswordResetDate, jwt);
        this.averageRating = averageRating;
        this.reservationCreatedNotificationEnabled = reservationCreatedNotificationEnabled;
        this.cancellationNotificationEnabled = cancellationNotificationEnabled;
        this.hostRatingNotificationEnabled = hostRatingNotificationEnabled;
        this.accommodationRatingNotificationEnabled = accommodationRatingNotificationEnabled;
    }
}
