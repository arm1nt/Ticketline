package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformerDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Band;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performer;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper
@Named("PerformerMapper")
public interface PerformerMapper {

    @IterableMapping(qualifiedByName = "performerToPerformerDto")
    List<PerformerDto> performerListToPerformerDtoList(List<Performer> performers);

    @Named("performerToPerformerDto")
    PerformerDto performerToPerformerDto(Performer performer);

    @Named("performerToPerformerDto")
    PerformerDto performerToPerformerDto(Artist artist);

    @Named("performerToPerformerDto")
    PerformerDto performerToPerformerDto(Band band);

    @Named("performerDtoToPerformer")
    Performer performerDtoToPerformer(PerformerDto performerDto);
}
