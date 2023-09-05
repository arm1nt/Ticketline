package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RowDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RowTicketDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Row;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.Set;

@Mapper(uses = {SeatMapper.class})
public interface RowMapper {
    Set<RowDto> rowSetToRowSetDto(Set<Row> rows);

}
