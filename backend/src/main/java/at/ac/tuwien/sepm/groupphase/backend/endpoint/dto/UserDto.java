package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class UserDto {

    private long id;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String street;
    private String country;
    private String city;
    private String zipCode;
}
