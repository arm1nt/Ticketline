package at.ac.tuwien.sepm.groupphase.backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity(name = "order_")
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "order_type", discriminatorType = DiscriminatorType.STRING)
public class Order {

    public Order(Long id, long performanceId, ApplicationUser applicationUser, Set<Ticket> tickets) {
        this.id = id;
        this.performanceId = performanceId;
        this.applicationUser = applicationUser;
        this.tickets = tickets;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private long performanceId;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Ticket> tickets = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "application_users_id")
    private ApplicationUser applicationUser;

    //Read only discriminator value
    @Column(name = "order_type", insertable = false, updatable = false)
    private String discriminatorValue;

    @Column
    private LocalDateTime creationDateTime;

    public void removeTicket(Ticket ticket) {
        this.tickets.remove(ticket);
    }

    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
    }
}