package rs.ac.uns.ftn.asd.BookedUp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.ReviewType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private GuestDTO guest;
    private int review;
    private String comment;
    private LocalDateTime date;
    private HostDTO host;
    private AccommodationDTO accommodation;
    private ReviewType type;
    private Boolean approved;


}
