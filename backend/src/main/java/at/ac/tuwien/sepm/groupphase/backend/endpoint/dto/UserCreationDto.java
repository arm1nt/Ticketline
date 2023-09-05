package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.validation.NullOrNotBlank;
import com.sun.istack.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@EqualsAndHashCode
@Accessors(chain = true)
public class UserCreationDto {
    @Email(regexp = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@"
        + "[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$",
        flags = Pattern.Flag.CASE_INSENSITIVE)
    private String email;
    @NotBlank
    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters long!")
    private String username;
    @NotNull
    private Boolean admin;
    @NotBlank
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters long")
    private String firstName;
    @NotBlank
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters long")
    private String lastName;
    @NullOrNotBlank
    @Size(min = 3, max = 100, message = "Street name must be between 3 and 100 characters long")
    private String street;
    @NullOrNotBlank
    @Size(min = 2, max = 100, message = "Country name must be between 2 and 100 characters long")
    private String country;
    @NullOrNotBlank
    @Size(min = 2, max = 100, message = "City name must be between 2 and 100 characters long")
    private String city;
    @NullOrNotBlank
    @Size(min = 2, max = 100, message = "City name must be between 2 and 100 characters long")
    private String zipCode;
}
