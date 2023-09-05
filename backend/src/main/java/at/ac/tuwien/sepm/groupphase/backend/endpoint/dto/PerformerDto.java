package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class PerformerDto {

    private Long id;

    @NotBlank(message = "A performer name must be given")
    private String performerName;

    private String firstName;

    private String lastName;
}
