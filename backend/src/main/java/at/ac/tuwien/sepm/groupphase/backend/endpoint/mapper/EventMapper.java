package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventInfoDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = PerformanceMapper.class)
@Named("EventMapper")
public interface EventMapper {

    @IterableMapping(qualifiedByName = "eventToEventDto")
    List<EventDto> eventListToEventDtoList(List<Event> event);

    @Named("eventDtoToEvent")
    Event eventDtoToEvent(EventDto eventDto);

    @Named("eventToEventDto")
    EventDto eventToEventDto(Event event);

    @Named("eventToEventInfoDto")
    EventInfoDto eventToEventInfoDto(Event event);

    @IterableMapping(qualifiedByName = "eventToEventInfoDto")
    List<EventInfoDto> eventListToEventInfoDtoList(List<Event> event);
}
