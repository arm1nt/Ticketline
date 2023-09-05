package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RowDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatingDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SectorTicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StandingDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Row;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seating;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.Standing;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(uses = {RowMapper.class})
public interface SectorMapper {
    // custom mapping method for Standing
    @SubclassMapping(source = Standing.class, target = StandingDto.class)
    StandingDto toStandingDTO(Standing source);

    // custom mapping method for Seating
    @SubclassMapping(source = Seating.class, target = SeatingDto.class)
    SeatingDto toSeatingDTO(Seating source);

    SectorTicketDto sectorToSectorTicketDto( Sector sector );
}

