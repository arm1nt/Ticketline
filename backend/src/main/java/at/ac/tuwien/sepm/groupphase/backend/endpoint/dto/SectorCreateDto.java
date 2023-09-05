package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Enumerated;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorCreateDto {

    private int id;

    @NotBlank(message = "Type of sector must not be null")
    @Pattern(regexp = "Seating|Standing")
    private String type;

    private double price;

    @NotBlank(message = "A color must be provided for the sector")
    private String color;
    private double capacity;
    private int colorSector;
    private double x;
    private double y;
    private double width;
    private double height;
    private @Valid RowCreateDto[] rows;
}
