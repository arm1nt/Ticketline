package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.type.EventType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EventDto {

    private Long id;

    @NotBlank
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private String image;

    private Integer soldTickets = 0;

    private List<PerformerDto> performers = new ArrayList<>();

    private List<PerformanceDto> performances = new ArrayList<>();

    @NotNull(message = "Duration can't be null")
    private int duration;
}
