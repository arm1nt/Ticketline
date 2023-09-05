package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private Long id;

    @NotNull
    @Size(min = 1)
    private Set<TicketDto> tickets;

    private String reservationCode;

    private LocalDateTime dueTime;

    private LocalDateTime time;

    private Double total;

    @NotNull
    private Long performanceId;

    private LocalDateTime creationDateTime;


    @Builder
    public OrderDto(Long id, Set<TicketDto> tickets, String reservationCode, LocalDateTime dueTime, LocalDateTime time,
                    Double total, Long performanceId) {
        this.id = id;
        this.tickets = tickets;
        this.reservationCode = reservationCode;
        this.time = time;
        this.dueTime = dueTime;
        this.total = total;
        this.performanceId = performanceId;
    }

    public OrderDto(Long id, Set<TicketDto> tickets, LocalDateTime time, Double total, Long performanceId) {
        this.id = id;
        this.tickets = tickets;
        this.time = time;
        this.total = total;
        this.performanceId = performanceId;
    }

    public OrderDto(Long id, Set<TicketDto> tickets, String reservationCode, LocalDateTime dueTime, Long performanceId) {
        this.id = id;
        this.tickets = tickets;
        this.reservationCode = reservationCode;
        this.dueTime = dueTime;
        this.performanceId = performanceId;
    }

}
