package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class SeatDto {
    private long id;
    private String seatId;
    private TicketStatus reservationStatus;
    private SeatGeometryDto geometry;
    private TicketSpotDto ticket;
}
