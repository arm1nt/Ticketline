package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.type.EventType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDateTime;

@JsonDeserialize(as = PerformanceSearchResultDtoImpl.class)
public interface PerformanceSearchResultDto {
    Long getId();

    String getEventName();

    String getPerformanceName();

    LocalDateTime getStartTime();

    LocalDateTime getEndTime();

    EventType getEventType();
}
