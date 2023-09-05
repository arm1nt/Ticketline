package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "payment")
@DiscriminatorValue("Payment")
public class Payment extends Order {

    @Builder
    public Payment(Long id, long performanceId, LocalDateTime time, Set<Ticket> tickets, Double total, ApplicationUser applicationUser, Invoice invoice) {
        super(id, performanceId, applicationUser, tickets);
        this.invoice = invoice;
        this.total = total;
        this.time = time;
    }

    @Column
    private LocalDateTime time;

    @Column
    private Double total;

    @OneToOne(cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "invoices_id")
    private Invoice invoice;

    @OneToOne(cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "cancellationInvoice_id")
    private CancellationInvoice cancellation;

}