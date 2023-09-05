package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import at.ac.tuwien.sepm.groupphase.backend.type.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventInfoDto {
    private Long id;

    @NotBlank(message = "Name can't be blank")
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private List<PerformanceInfoDto> performances;
}
