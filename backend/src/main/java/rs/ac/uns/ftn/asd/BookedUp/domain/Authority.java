package rs.ac.uns.ftn.asd.BookedUp.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Authority implements GrantedAuthority  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String role;



    public Authority(String role) {
        super();
        this.role = role;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Authority a = (Authority) o;
        if (a.getId() == null || id == null) {
            if(a.getRole().equals(getRole())){
                return true;
            }
            return false;
        }
        return Objects.equals(id, a.getId());
    }


    @Override
    public String getAuthority() {
        return role;
    }
}
