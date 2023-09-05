package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsShortDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface NewsMapper {

    News newsDetailDtoToNews(NewsDetailDto newsDetailDto);

    NewsDetailDto newsToNewsDetailDto(News news);

    @Named("shortNews")
    @Mapping(target = "text", source = "text", qualifiedByName = "shortenText")
    NewsShortDto newsToNewsShortDto(News news);

    @IterableMapping(qualifiedByName = "shortNews")
    List<NewsShortDto> newsToNewsShortDto(List<News> news);

    @Named("shortenText")
    default String shortenText(String s) {
        if (s == null || s.length() < 200) {
            return s;
        }
        return s.substring(0, 200) + "...";
    }
}
