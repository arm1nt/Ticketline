package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@DiscriminatorValue("Standing")
public class Standing extends Sector {

    @Builder
    public Standing(int capacity, String sectorId, double price, String color, Layout layout, RectangleGeometry geometry) {
        super(sectorId, price, color, layout, geometry);
        this.capacity = capacity;
    }

    @Column
    private int capacity;

    @OneToMany(mappedBy = "standing", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Stand> stands = new HashSet<>();
}
