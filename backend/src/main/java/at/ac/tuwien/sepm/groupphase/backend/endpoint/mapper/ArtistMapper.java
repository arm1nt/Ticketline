package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
@Named("ArtistMapper")
public interface ArtistMapper {

    @IterableMapping(qualifiedByName = "artistToArtistDto")
    List<ArtistDto> artistListToArtistDtoList(List<Artist> artist);

    @Named("artistToArtistDto")
    @Mapping(target = "events", ignore = true)
    ArtistDto artistToArtistDto(Artist artist);

    @Named("artistDtoToArtist")
    Artist artistDtoToArtist(ArtistDto artistDto);
}
