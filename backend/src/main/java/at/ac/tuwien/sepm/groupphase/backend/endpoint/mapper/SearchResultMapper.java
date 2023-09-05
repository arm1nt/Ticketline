package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformerSearchResultDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Band;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class SearchResultMapper {
    private final PerformerMapper performerMapper;
    private final EventMapper eventMapper;


    public SearchResultMapper(PerformerMapper performerMapper, EventMapper eventMapper) {
        this.performerMapper = performerMapper;
        this.eventMapper = eventMapper;
    }

    public PerformerSearchResultDto performerAndEventListToPerformerSearchResult(Performer performer, List<Event> events) {
        if (performer.getClass() == Artist.class) {
            return new PerformerSearchResultDto(performerMapper.performerToPerformerDto((Artist) performer), eventMapper.eventListToEventInfoDtoList(events));
        }
        return new PerformerSearchResultDto(performerMapper.performerToPerformerDto((Band) performer), eventMapper.eventListToEventInfoDtoList(events));
    }

    public List<PerformerSearchResultDto> performerListAndEventMapToPerformerSearchResultList(Collection<Performer> performers, Map<Long, List<Event>> eventMap) {
        List<PerformerSearchResultDto> performerSearchResultDtos = new ArrayList<>();
        for (Performer p :
            performers) {
            performerSearchResultDtos.add(performerAndEventListToPerformerSearchResult(p, eventMap.get(p.getId())));
        }
        return performerSearchResultDtos;
    }
}
