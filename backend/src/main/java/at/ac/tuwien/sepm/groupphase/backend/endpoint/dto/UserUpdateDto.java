package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.validation.NullOrEmail;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.validation.NullOrNotBlank;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.validation.UniqueEmail;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Size;

@Getter
@Setter
@ToString(exclude = "password")
public class UserUpdateDto {

    @NullOrNotBlank(message = "Firstname must either be null or not blank")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters long")
    private String firstName;

    @NullOrNotBlank(message = "Lastname must either be null or not blank")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters long")
    private String lastName;

    @NullOrNotBlank(message = "Email must either be null or not blank")
    @NullOrEmail(message = "Given email does not conform to email format")
    private String email;

    @NullOrNotBlank(message = "Password must either be null or not be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NullOrNotBlank(message = "Country must either be null or not blank")
    @Size(min = 2, max = 100, message = "Country name must be between 2 and 100 characters long")
    private String country;

    @NullOrNotBlank(message = "City must either be null or not blank")
    @Size(min = 2, max = 100, message = "City name must be between 2 and 100 characters long")
    private String city;

    @NullOrNotBlank(message = "Zip Code must either be null or not blank")
    @Size(min = 2, max = 100, message = "ZipCode name must be between 2 and 100 characters long")
    private String zipCode;

    @NullOrNotBlank(message = "Street must either be null or not blank")
    @Size(min = 3, max = 100, message = "Street name must be between 3 and 100 characters long")
    private String street;
}
