package nuudelchin.club.web.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class RefreshEntity {

	private String username;
    private String refresh;
    private String expiration;
}
