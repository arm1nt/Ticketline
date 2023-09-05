package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformerDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("Artist")
public class Artist extends Performer {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @ManyToMany(mappedBy = "artists")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Band> band = new ArrayList<>();

    @Builder
    public Artist(String performerName, String firstName, String lastName) {
        super(performerName);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public PerformerDto toArtistDto() {
        PerformerDto dto = new PerformerDto();
        dto.setId(this.getId());
        dto.setPerformerName(this.getPerformerName());
        dto.setFirstName(this.getFirstName());
        dto.setLastName(this.getLastName());
        return dto;
    }

}
