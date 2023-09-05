package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class SeatTicketDto {
    private long id;
    private String seatId;
    private RowTicketDto rowTicketDto;

    public SeatTicketDto(long id, String seatId, RowTicketDto rowTicketDto) {
        this.id = id;
        this.seatId = seatId;
        this.rowTicketDto = rowTicketDto;
    }
}
