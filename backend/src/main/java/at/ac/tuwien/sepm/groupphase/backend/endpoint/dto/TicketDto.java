package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;


import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class TicketDto {

    private Long id;

    private String ticketId;

    private TicketStatus ticketStatus;

    private SeatTicketDto seatTicketDto;

    private StandTicketDto standTicketDto;
    private double price;

    @Builder
    public TicketDto(Long id, String ticketId, TicketStatus ticketStatus, SeatTicketDto seatTicketDto,
                     StandTicketDto standTicketDto) {
        this.id = id;
        this.ticketId = ticketId;
        this.ticketStatus = ticketStatus;
        this.seatTicketDto = seatTicketDto;
        this.standTicketDto = standTicketDto;
    }

    public TicketDto(Long id, String ticketId, TicketStatus ticketStatus) {
        this.id = id;
        this.ticketId = ticketId;
        this.ticketStatus = ticketStatus;
    }

}
