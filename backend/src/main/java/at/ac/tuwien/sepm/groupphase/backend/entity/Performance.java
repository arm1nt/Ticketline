package at.ac.tuwien.sepm.groupphase.backend.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, name = "performance_name")
    private String performanceName;

    @NotNull
    @Column(nullable = false, name = "start_time")
    private LocalDateTime startTime;

    @NotNull
    @Column(nullable = false, name = "end_time")
    private LocalDateTime endTime;

    @Column(name="sold_tickets")
    private int soldTickets = 0;

    @ManyToOne
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "layouts_id")
    private Layout layout;
}
