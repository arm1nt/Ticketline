package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class SectorDto {
    @NotNull
    private long id;
    private String sectorId;
    private double price;
    private String color;
    private RectangleGeometryDto geometry;
}
