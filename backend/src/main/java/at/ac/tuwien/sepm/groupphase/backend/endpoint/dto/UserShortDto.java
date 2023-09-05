package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class UserShortDto {
    private long id;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
}
