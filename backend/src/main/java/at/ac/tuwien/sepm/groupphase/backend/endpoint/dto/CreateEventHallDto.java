package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateEventHallDto {

    @NotBlank(message = "Eventhall name must not be null or blank")
    @Size(min = 2, max = 50, message = "Eventhall name must be between 2 and 50 characters")
    private String name;
    private double x;
    private double y;
    private double width;
    private double height;

    @NotNull(message = "Location can't be null")
    private @Valid LocationDto location;

    @NotNull(message = "Layout must not be null")
    private @Valid CreateLayoutDto layout;
}
