package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLayoutDto {

    @NotBlank(message = "Name can't be blank")
    @Size(min = 2, max = 50, message = "Layout name must be between 2 and 50 characters")
    private String name;

    @NotNull(message = "Sectors can't be null")
    private @Valid SectorCreateDto[] sectors;
}
