package at.ac.tuwien.sepm.groupphase.backend.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "row_")
public class Row {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private int rowNumber;

    @OneToMany(mappedBy = "row", fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<Seat> seats = new HashSet<>();


    @ManyToOne(fetch = FetchType.EAGER)
    private Geometry geometry;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seatings_id")
    private Seating seating;
}
