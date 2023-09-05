package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity(name = "reservation")
@DiscriminatorValue("Reservation")
public class Reservation extends Order {

    @Builder
    public Reservation(Long id, long performanceId, String reservationCode, LocalDateTime dueTime,
                       Set<Ticket> tickets, ApplicationUser applicationUser) {
        super(id, performanceId, applicationUser, tickets);
        this.reservationCode = reservationCode;
        this.dueTime = dueTime;
    }

    @Column
    private String reservationCode;

    @Column
    private LocalDateTime dueTime;
}
