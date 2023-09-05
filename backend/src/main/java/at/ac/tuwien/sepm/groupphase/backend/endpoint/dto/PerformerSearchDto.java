package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class PerformerSearchDto {
    private String firstname;
    private String lastname;
    private String artistname;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PerformerSearchDto that = (PerformerSearchDto) o;
        return Objects.equals(firstname, that.firstname) && Objects.equals(lastname, that.lastname) && Objects.equals(artistname, that.artistname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstname, lastname, artistname);
    }

}

