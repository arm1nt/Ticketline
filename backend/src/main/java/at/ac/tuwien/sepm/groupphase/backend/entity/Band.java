package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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
@DiscriminatorValue("Band")
public class Band extends Performer {

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Performer> artists = new ArrayList<>();

    @Builder
    public Band(String performerName, List<Performer> artists) {
        super(performerName);
        this.artists = artists;
    }

}
