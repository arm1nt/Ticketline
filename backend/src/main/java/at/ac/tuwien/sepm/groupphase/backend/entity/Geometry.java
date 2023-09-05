package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
public class Geometry {
    public Geometry(double x, double y, double rotation) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(columnDefinition = "DOUBLE DEFAULT 0")
    private double x;
    @Column(columnDefinition = "DOUBLE DEFAULT 0")
    private double y;
    @Column(columnDefinition = "DOUBLE DEFAULT 0")
    private double rotation;
}
