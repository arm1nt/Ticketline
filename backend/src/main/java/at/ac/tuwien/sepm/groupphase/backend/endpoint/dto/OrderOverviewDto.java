package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class OrderOverviewDto {

    private Long id;

    private Set<TicketDto> tickets;

    private String reservationCode;

    private LocalDateTime dueTime;

    private LocalDateTime time;

    private Double total;

    private Long performanceId;

    private String performanceName;

    private LocalDateTime performanceStartTime;

    private Long eventId;

    private String eventName;

    private List<PerformerDto> performers;

    private LocalDateTime creationDateTime;

    private String locationName;

    private String eventHallName;
}
