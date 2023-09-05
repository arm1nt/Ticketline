package at.ac.tuwien.sepm.groupphase.backend.entity;

import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
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
import javax.persistence.OneToOne;
import javax.persistence.Version;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String ticketId;

    @Column
    private TicketStatus ticketStatus;

    @Column
    private double price;

    @Version
    private Integer version;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticketPdf_id")
    private TicketPdf ticketPdf;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "seats_id")
    private Seat seat;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "stands_id")
    private Stand stand;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "performances_id")
    private Performance performance;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "user_id")
    ApplicationUser applicationUser;

}