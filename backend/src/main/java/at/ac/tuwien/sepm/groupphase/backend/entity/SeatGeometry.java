package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class SeatGeometry extends RectangleGeometry {
    @Builder(builderMethodName = "seatBuilder")
    public SeatGeometry(double x, double y, double rotation, double width, double height, double legSpaceDepth) {
        super(x, y, rotation, width, height);
        this.legSpaceDepth = legSpaceDepth;
    }
    @Column
    private double legSpaceDepth;
}
