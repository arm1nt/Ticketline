package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformanceInfoDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Layout;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface PerformanceMapper {

    @IterableMapping(qualifiedByName = "performanceToPerformanceDto")
    List<PerformanceDto> performanceListToPerformanceDtoList(List<Performance> performance);

    @Named("performanceToPerformanceDto")
    @Mapping(target = "eventId", expression = "java(getEventId(performance.getEvent()))")
    @Mapping(target = "layoutId", expression = "java(getLayoutId(performance.getLayout()))")
    PerformanceDto performanceToPerformanceDto(Performance performance);

    @Named("performanceDtoToPerformance")
    Performance performanceDtoToPerformance(PerformanceDto performanceDto);

    @Named("performanceToPerformanceInfoDto")
    @Mapping(target = "eventHall", source = "layout.eventHall")
    PerformanceInfoDto performanceToPerformanceInfoDto(Performance performance);

    @IterableMapping(qualifiedByName = "performanceToPerformanceInfoDto")
    List<PerformanceInfoDto> performanceListToPerformanceInfoDtoList(List<Performance> performance);

    default Long getEventId(Event event) {
        if (event == null) {
            return null;
        }
        return event.getId();
    }

    default Long getLayoutId(Layout layout) {
        if (layout == null) {
            return null;
        }
        return layout.getId();
    }

}
