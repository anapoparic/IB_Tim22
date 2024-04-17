package rs.ac.uns.ftn.asd.BookedUp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhotoDTO {
    private Long id;
    private String url;
    private String caption;
    private boolean active = true;

    public void copyValues(PhotoDTO dto) {
        this.url = dto.getUrl();
        this.caption = dto.getCaption();
    }
}
