package rs.ac.uns.ftn.asd.BookedUp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.asd.BookedUp.domain.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReportDTO {
    private Long id;
    private String reason;
    private UserDTO reportedUser;
    private boolean status;

}
