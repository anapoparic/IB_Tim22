package rs.ac.uns.ftn.asd.BookedUp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LogInDTO {
    private String email;
    private String password;

    public LogInDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
