package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSeatDto {

    @NotBlank(message = "A seat id must be given")
    private String seatId;

    private int rowNumber;
    private double x;
    private double y;
    private double price;
    private String polygonPoints;
    private int sectorId;
    private boolean chosen;
}
