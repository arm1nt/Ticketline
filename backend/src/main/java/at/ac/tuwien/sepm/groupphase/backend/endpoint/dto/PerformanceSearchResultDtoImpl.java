package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.type.EventType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PerformanceSearchResultDtoImpl implements PerformanceSearchResultDto {

    private Long id;
    private String eventName;
    private String performanceName;
    private EventType eventType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getEventName() {
        return eventName;
    }

    @Override
    public String getPerformanceName() {
        return performanceName;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }
}
