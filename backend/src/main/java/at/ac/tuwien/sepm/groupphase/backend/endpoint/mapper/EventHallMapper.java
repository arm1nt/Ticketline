package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventHallDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventHallOverviewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RectangleGeometryDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventHall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventHallMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final LocationMapper locationMapper;

    public EventHallMapper(LocationMapper locationMapper) {
        this.locationMapper = locationMapper;
    }

    public EventHallOverviewDto eventHallToEventHallOverviewDto(EventHall eventHall) {
        LOGGER.trace("eventHallToEventHallOverviewDto({})", eventHall);

        if (eventHall == null) {
            return null;
        }

        return EventHallOverviewDto.builder()
            .id(eventHall.getId())
            .name(eventHall.getName())
            .location(this.locationMapper.locationToLocationDto(eventHall.getLocation()))
            .build();
    }

    public List<EventHallOverviewDto> eventHallListToEventHallOverviewDtoList(List<EventHall> eventHalls) {
        LOGGER.trace("eventHallListToEventHallOverviewDtoList()");

        if (eventHalls == null) {
            return null;
        }

        List<EventHallOverviewDto> dtoList = new ArrayList<>();

        for (EventHall eventHall : eventHalls) {
            dtoList.add(this.eventHallToEventHallOverviewDto(eventHall));
        }

        return dtoList;
    }

    public EventHallDto eventHallToEventHallDto(EventHall eventHall) {
        LOGGER.trace("eventHallToEventHallDto({})", eventHall);

        if (eventHall == null) {
            return null;
        }

        EventHallDto eventHallDto = new EventHallDto();
        eventHallDto.setId(eventHall.getId());
        eventHallDto.setName(eventHall.getName());

        RectangleGeometryDto geometryDto = new RectangleGeometryDto();
        geometryDto.setX(eventHall.getGeometry().getX());
        geometryDto.setY(eventHall.getGeometry().getY());
        geometryDto.setRotation(eventHall.getGeometry().getRotation());
        geometryDto.setWidth(eventHall.getGeometry().getWidth());
        geometryDto.setHeight(eventHall.getGeometry().getHeight());

        eventHallDto.setGeometry(geometryDto);
        eventHallDto.setLocation(this.locationMapper.locationToLocationDto(eventHall.getLocation()));

        return eventHallDto;
    }
}
