package rs.ac.uns.ftn.asd.BookedUp.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import rs.ac.uns.ftn.asd.BookedUp.domain.*;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.Role;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AdminDTO extends UserDTO{
//    private List<UserReportDTO> userReports;
//    private List<ReviewReportDTO> reviewReports;
//    private List<AccommodationDTO> requests;


    public AdminDTO(Long id, String firstName, String lastName, AddressDTO address, Integer phone, String email, String password, boolean isBlocked, boolean verified, PhotoDTO profilePicture, Role role) {
        super(id, firstName, lastName, address, phone, email, password, isBlocked, verified, profilePicture, role);

//        this.userReports = userReports;
//        this.reviewReports = reviewReports;
//        this.requests = requests;
    }


}
