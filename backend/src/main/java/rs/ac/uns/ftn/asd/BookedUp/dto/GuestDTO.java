package rs.ac.uns.ftn.asd.BookedUp.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import lombok.experimental.SuperBuilder;
import rs.ac.uns.ftn.asd.BookedUp.domain.*;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.Role;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope=GuestDTO.class)
public class GuestDTO extends UserDTO {


    private List<AccommodationDTO> favourites;
    private boolean notificationEnable = true;


    public GuestDTO(Long id, String firstName, String lastName, AddressDTO address, Integer phone, String email, String password, boolean isBlocked, boolean verified, PhotoDTO profilePicture, Role role, List<AccommodationDTO> favourites, boolean notificationEnable) {
        super(id, firstName, lastName, address, phone, email, password, isBlocked, verified, profilePicture, role);
        this.favourites = favourites;
        this.notificationEnable = notificationEnable;
    }


}
