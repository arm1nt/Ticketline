package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.BandDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Band;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(imports = Artist.class)
@Named("BandMapper")
public interface BandMapper {

    @IterableMapping(qualifiedByName = "bandToBandDto")
    List<BandDto> bandListToBandDtoList(List<Band> band);

    @Named("bandToBandDto")
    @Mapping(target = "events", ignore = true)
    BandDto bandToBandDto(Band band);

    @Named("bandDtoToBand")
    Band bandDtoToBand(BandDto bandDto);
}
