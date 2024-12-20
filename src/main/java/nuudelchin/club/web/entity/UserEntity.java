package nuudelchin.club.web.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class UserEntity {

    private String username;
    private String password;
    private String role;
}
