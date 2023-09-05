package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BandDto {

    private Long id;

    @NotBlank(message = "Performer's name can't be blank")
    private String performerName;

    private List<EventDto> events = new ArrayList<>();

    private List<PerformerDto> artists = new ArrayList<>();
}
