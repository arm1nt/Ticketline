package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface LocationMapper {

    Location locationDtoToLocation(LocationDto locationDto);

    @Named(value = "locationToLocationDto")
    LocationDto locationToLocationDto(Location location);

    @IterableMapping(qualifiedByName = "locationToLocationDto")
    List<LocationDto> locationListToLocationDtoList(List<Location> locations);
}
