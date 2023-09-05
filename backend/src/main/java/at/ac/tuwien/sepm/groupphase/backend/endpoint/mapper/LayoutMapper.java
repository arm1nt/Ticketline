package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventHallDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LayoutDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LayoutOverviewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RectangleGeometryDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RowDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SeatingDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SectorDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StandDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StandingDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventHall;
import at.ac.tuwien.sepm.groupphase.backend.entity.Layout;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seating;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.Standing;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class LayoutMapper {

    private final SectorMapper sectorMapper;

    private final TicketMapper ticketMapper;


    @Autowired
    public LayoutMapper(SectorMapper sectorMapper, TicketMapper ticketMapper) {
        this.sectorMapper = sectorMapper;
        this.ticketMapper = ticketMapper;
    }

    public Set<SectorDto> mapSectors(Set<Sector> source) {
        if (source == null) {
            return null;
        }
        Set<SectorDto> target = new HashSet<>();
        for (Sector sector : source) {
            if (sector instanceof Standing) {
                target.add(sectorMapper.toStandingDTO((Standing) sector));
            } else if (sector instanceof Seating) {
                target.add(sectorMapper.toSeatingDTO((Seating) sector));
            }
        }
        return target;
    }

    public List<LayoutDto> layoutListToLayoutDtoList(List<Layout> layouts) {
        List<LayoutDto> result = new ArrayList<>();
        if (layouts == null || layouts.isEmpty()) {
            return result;
        }
        for (Layout l : layouts) {
            result.add(layoutToLayoutDto(l));
        }
        return result;
    }

    public LayoutDto layoutToLayoutDto(Layout layout) {
        if (layout == null) {
            return null;
        }

        LayoutDto layoutDto = new LayoutDto();

        layoutDto.setName(layout.getName());
        layoutDto.setSectors(this.mapSectors(layout.getSectors()));
        layoutDto.setEventHall(eventHallToEventHallDto(layout.getEventHall()));
        layoutDto.setId(layout.getId());
        return layoutDto;
    }

    public EventHallDto eventHallToEventHallDto(EventHall value) {
        if (value == null) {
            return null;
        }

        EventHallDto eventHallDto = new EventHallDto();
        eventHallDto.setId(value.getId());
        eventHallDto.setName(value.getName());

        RectangleGeometryDto geometry = new RectangleGeometryDto();
        geometry.setX(value.getGeometry().getX());
        geometry.setY(value.getGeometry().getY());
        geometry.setRotation(value.getGeometry().getRotation());
        geometry.setWidth(value.getGeometry().getWidth());
        geometry.setHeight(value.getGeometry().getHeight());

        eventHallDto.setGeometry(geometry);

        return eventHallDto;
    }

    public LayoutOverviewDto layoutToLayoutOverviewDto(Layout layout) {
        if(layout == null) {
            return null;
        }

        return LayoutOverviewDto.builder()
            .name(layout.getName())
            .id(layout.getId())
            .eventHallName(layout.getEventHall().getName())
            .build();
    }

    public List<LayoutOverviewDto> layoutListToLayoutOverviewDtoList(List<Layout> layouts) {
        if(layouts == null) {
            return null;
        }

        List<LayoutOverviewDto> mappedList = new ArrayList<>();

        for(Layout l : layouts) {
            mappedList.add(LayoutOverviewDto.builder()
                .id(l.getId())
                .name(l.getName())
                .eventHallName(l.getEventHall().getName())
                .build());
        }

        return mappedList;
    }
}
