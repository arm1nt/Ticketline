package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;


import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class LayoutDto {
    @NotNull
    private long id;
    @NotBlank(message = "Name can't be blank")
    private String name;
    private EventHallDto eventHall;
    private Set<SectorDto> sectors;
}
