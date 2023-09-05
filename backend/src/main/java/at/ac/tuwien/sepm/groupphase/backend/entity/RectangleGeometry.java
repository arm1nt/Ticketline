package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class RectangleGeometry extends Geometry {
    @Builder(builderMethodName = "rectangleBuilder")
    RectangleGeometry(double x, double y, double rotation, double width, double height) {
        super(x,y,rotation);
        this.height = height;
        this.width = width;
    }
    @Column
    private double width;
    @Column
    private double height;
}
