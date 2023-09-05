package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceDto {
    private Long id;

    @NotBlank(message = "Performance name must be given and not be blank")
    @Size(max = 50, message = "Performance name must not be longer than 50 characters")
    private String performanceName;

    @NotNull(message = "A start time must be given")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @NotNull(message = "A event id must be given")
    private Long eventId;

    @NotNull(message = "A layout id must be given")
    private Long layoutId;

    @NotNull(message = "A location id must be given")
    private Long locationId;

    @NotNull(message = "A eventhall id must be given")
    private Long eventhallId;
}
