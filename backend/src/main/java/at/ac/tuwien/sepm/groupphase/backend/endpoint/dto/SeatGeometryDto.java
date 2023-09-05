package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class SeatGeometryDto extends RectangleGeometryDto{
    private double legSpaceDepth;
}
