package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class StandTicketDto {
    private long id;
    private SectorTicketDto sectorTicketDto;

    public StandTicketDto(long id, SectorTicketDto sectorTicketDto) {
        this.id = id;
        this.sectorTicketDto = sectorTicketDto;
    }

}