package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RowTicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatTicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StandTicketDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Row;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.Stand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpotTicketMapper {
    public final SectorMapper sectorMapper;

    @Autowired
    public SpotTicketMapper(SectorMapper sectorMapper) {
        this.sectorMapper = sectorMapper;
    }

    public StandTicketDto standToStandTicketDto(Stand stand) {
        if(stand != null) {
            return new StandTicketDto(stand.getId(), sectorMapper.sectorToSectorTicketDto(stand.getStanding()));
        }
        else return null;
    }

    public SeatTicketDto seatToSeatTicketDto(Seat seat) {
        if(seat != null) {
            return new SeatTicketDto(seat.getId(), seat.getSeatId(), rowToRowTicketDto(seat.getRow()));
        }
        else return null;
    }

    public RowTicketDto rowToRowTicketDto(Row row) {
       return new RowTicketDto(row.getId(), row.getRowNumber(), sectorMapper.sectorToSectorTicketDto(row.getSeating()));
    }

}
