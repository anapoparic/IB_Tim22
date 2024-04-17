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
@DiscriminatorValue("ADMIN")
public class Admin extends User{
//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name = "admin_id", nullable = true)
//    private List<UserReport> userReports;
//
//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name = "admin_id", nullable = true)
//    private List<ReviewReport> reviewReports;
//
//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinColumn(name = "admin_id", nullable = true)
//    private List<Accommodation> requests;


    public Admin(Long id, String firstName, String lastName, Address address, Integer phone, String email, String password, boolean isBlocked, boolean active, boolean verified, Photo profilePicture, Role role, Timestamp lastPasswordResetDate, String jwt) {
        super(id, firstName, lastName, address, phone, email, password, isBlocked, active, verified, profilePicture, role, lastPasswordResetDate, jwt);
    }
}
