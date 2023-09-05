package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("Seating")
public class Seating extends Sector {

    @Builder
    public Seating(String sectorId, double price, String color, Layout layout, RectangleGeometry geometry) {
        super(sectorId, price, color, layout, geometry);
    }

    @OneToMany(mappedBy = "seating", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Row> rows = new HashSet<>();
}