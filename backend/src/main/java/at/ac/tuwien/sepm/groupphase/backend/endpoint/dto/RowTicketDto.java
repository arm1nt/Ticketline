package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class RowTicketDto {
    private long id;
    private int rowNumber;
    private SectorTicketDto sectorTicketDto;

    public RowTicketDto (long id, int rowNumber, SectorTicketDto sectorTicketDto) {
        this.id = id;
        this.rowNumber = rowNumber;
        this.sectorTicketDto = sectorTicketDto;
    }
}

