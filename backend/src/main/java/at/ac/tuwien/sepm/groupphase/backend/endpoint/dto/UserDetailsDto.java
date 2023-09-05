package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;

@Getter
@Setter
@ToString
public class UserDetailsDto {

    private long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String country;
    private String city;
    private String zipCode;
    private String street;

}
