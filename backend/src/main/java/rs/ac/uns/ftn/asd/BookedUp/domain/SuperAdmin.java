package rs.ac.uns.ftn.asd.BookedUp.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.Role;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("SUPER_ADMIN")
public class SuperAdmin extends User{

    public SuperAdmin(Long id, String firstName, String lastName, Address address, Integer phone, String email, String password, boolean isBlocked, boolean active, boolean verified, Photo profilePicture, Role role, Timestamp lastPasswordResetDate, String jwt) {
        super(id, firstName, lastName, address, phone, email, password, isBlocked, active, verified, profilePicture, role, lastPasswordResetDate, jwt);
    }
}
