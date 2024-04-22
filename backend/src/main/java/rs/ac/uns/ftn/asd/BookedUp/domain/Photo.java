package rs.ac.uns.ftn.asd.BookedUp.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = false)
    private String url;

    @Column(nullable = true, unique = false)
    private String caption;

    @Column(nullable = true)
    private int width;

    @Column(nullable = true)
    private int height;

    @Column(nullable = false)
    private boolean active = true;

//    public void copyValues(Photo photo){
//        this.id = photo.getId();
//        this.url = photo.getUrl();
//        this.caption = photo.getCaption();
//        this.width = photo.getWidth();
//        this.height = photo.getHeight();
//    }
}
