package rs.ac.uns.ftn.asd.BookedUp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.Role;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("GUEST")
public class Guest extends User {

//    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL)
//    private List<Reservation> requests;

//    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL)
//    private List<Reservation> reservations;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "guest_favourite",
            joinColumns = @JoinColumn(name = "guest_id"),
            inverseJoinColumns = @JoinColumn(name = "accommodation_id"))
    private List<Accommodation> favourites;

//    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL)
//    private List<Review> reviews;

    @Column(nullable = true)
    private boolean notificationEnable = true;

    public Guest(Long id, String firstName, String lastName, Address address, Integer phone, String email, String password, boolean isBlocked, boolean active, boolean verified, Photo profilePicture, Role role, Timestamp lastPasswordResetDate, String jwt, List<Accommodation> favourites, boolean notificationEnable) {
        super(id, firstName, lastName, address, phone, email, password, isBlocked, active, verified, profilePicture, role, lastPasswordResetDate, jwt);
        this.favourites = favourites;
        this.notificationEnable = notificationEnable;
    }
}

