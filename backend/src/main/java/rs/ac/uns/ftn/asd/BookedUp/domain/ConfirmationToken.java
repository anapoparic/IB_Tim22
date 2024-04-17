package rs.ac.uns.ftn.asd.BookedUp.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String token;

    @NonNull
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime confirmedAt;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

}