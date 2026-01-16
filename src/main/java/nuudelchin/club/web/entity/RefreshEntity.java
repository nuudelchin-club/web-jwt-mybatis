package nuudelchin.club.web.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RefreshEntity {

	private String username;
    private String refresh;
    private String expiration;
}
