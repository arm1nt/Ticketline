package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LayoutRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.type.EventType;
import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Set;

@Profile("generateData")
@DependsOn({"LayoutDataGenerator", "EventDataGenerator", "PerformanceDataGenerator"})
@Component("TicketDataGenerator")
public class TicketDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TicketRepository ticketRepository;

    private final EventRepository eventRepository;

    private final PerformanceRepository performanceRepository;

    private final LayoutRepository layoutRepository;

    public TicketDataGenerator(TicketRepository ticketRepository, EventRepository eventRepository, PerformanceRepository performanceRepository, LayoutRepository layoutRepository) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
        this.performanceRepository = performanceRepository;
        this.layoutRepository = layoutRepository;
    }


    @PostConstruct
    private void generateTickets() {

        LOGGER.trace("generateTickets()");
        if (this.ticketRepository.getTicketById(1L) == null) {
            List<Event> events = this.eventRepository.findAll();
            for (Event event:
                events  ) {
                for (Performance p:
                    event.getPerformances()) {
                    long layoutId = p.getLayout().getId();
                    Layout l = layoutRepository.getLayoutById(layoutId);
                    Ticket ticket;
                    for (Sector s :
                        l.getSectors()) {
                        if (s.getClass() == Seating.class) {
                            for (Row r : ((Seating) s).getRows()) {
                                for (Seat seat :
                                    r.getSeats()) {
                                    ticket = generateTicket(("Seat " + seat.getSeatId() + " - Row " + r.getRowNumber() + " - Sector " + s.getSectorId()), TicketStatus.FREE, seat, p, s.getPrice());
                                    ticketRepository.save(ticket);
                                }
                            }
                        } else if (s.getClass() == Standing.class) {
                            int k = 0;
                            for (Stand stand :
                                ((Standing) s).getStands()) {
                                ticket = generateTicket("Stand " + ++k + " - Sector " + s.getSectorId(), TicketStatus.FREE, stand, p, s.getPrice());
                                ticketRepository.save(ticket);
                            }
                        }
                    }
                }
            }

        }
    }

    public Ticket generateTicket(String ticketId, TicketStatus status, Seat seat, Performance performance, double price) {
        LOGGER.trace("generateTicket()");
        return Ticket.builder()
            .ticketId(ticketId)
            .ticketStatus(status)
            .seat(seat)
            .performance(performance)
            .price(price)
            .build();
    }

    public Ticket generateTicket(String ticketId, TicketStatus status, Stand stand, Performance performance, double price) {
        LOGGER.trace("generateTicket()");
        return Ticket.builder()
            .ticketId(ticketId)
            .ticketStatus(status)
            .stand(stand)
            .performance(performance)
            .price(price)
            .build();
    }

    public Performance generatePerformance(Layout layout) {
        Event event = Event.builder()
            .name("Baden Sommer Jazz")
            .eventType(EventType.CONCERT)
            .build();
        if (eventRepository.findAll().isEmpty()) {
            event = eventRepository.save(event);
        } else {
            if (eventRepository.findById(1L).isPresent()) {
                event = eventRepository.findById(1L).get();
            } else {
                event = eventRepository.save(event);
            }
        }
        LOGGER.trace("generating event {}", event.getName());

        Performance performance = Performance.builder()
            .performanceName("Jam Session")
            .event(event)
            .startTime(LocalDateTime.of(2023, Month.JUNE, 23, 18, 0))
            .endTime(LocalDateTime.of(2023, Month.JUNE, 23, 19, 30))
            .layout(layout)
            .build();
        if (performanceRepository.findAll().isEmpty()) {
            performance = performanceRepository.save(performance);
        } else {
            if (performanceRepository.findById(1L).isPresent()) {
                performance = performanceRepository.findById(1L).get();
            } else {
                performance = performanceRepository.save(performance);
            }
        }
        LOGGER.trace("saving performance {}", performance);

        return performance;
    }

}

