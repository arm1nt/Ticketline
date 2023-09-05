package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ArtistDto {

    private Long id;

    private String firstName;

    private String lastName;

    @NotBlank(message = "Performer's name can't be blank")
    private String performerName;

    private List<EventDto> events = new ArrayList<>();

    private List<BandDto> bands = new ArrayList<>();
}
