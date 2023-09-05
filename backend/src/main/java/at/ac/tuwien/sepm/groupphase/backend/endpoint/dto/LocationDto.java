package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.validation.NullOrNotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto {

    private long id;

    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name must not be blank")
    @Size(min = 2, max = 50, message = "Name length must be between 2 and 50 characters long")
    private String name;

    @NotNull(message = "Country must not be null")
    @NotBlank(message = "Country must not be blank")
    @Size(min = 2, max = 50, message = "Country's name length must be between 2 and 50 characters long")
    private String country;

    @NotNull(message = "City must not be null")
    @NotBlank(message = "City must not be blank")
    @Size(min = 2, max = 50, message = "City's name length must be between 2 and 50 characters long")
    private String city;

    @NotNull(message = "Street must not be null")
    @NotBlank(message = "Street must not be blank")
    @Size(min = 2, max = 50, message = "Street's name length must be between 2 and 50 characters long")
    private String street;

    @NotNull(message = "ZipCode must not be null")
    @NotBlank(message = "ZipCode must not be blank")
    @Size(min = 2, max = 15, message = "ZipCode's length must be between 2 and 15 characters long")
    private String zipCode;
}
