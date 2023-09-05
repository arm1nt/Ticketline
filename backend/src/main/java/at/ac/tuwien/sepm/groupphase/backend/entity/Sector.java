package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "sector_type", discriminatorType = DiscriminatorType.STRING)
public class Sector {

    public Sector(String sectorId, double price, String color, Layout layout, RectangleGeometry geometry) {
        this.sectorId = sectorId;
        this.price = price;
        this.color = color;
        this.layout = layout;
        this.geometry = geometry;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String sectorId;

    @Column(nullable = false)
    private double price;
    @Column
    private String color;
    @ManyToOne(fetch = FetchType.EAGER)
    private RectangleGeometry geometry;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "layout_id")
    private Layout layout;

    @Column(name = "sector_type", insertable = false, updatable = false)
    private String discriminatorValue;
}
