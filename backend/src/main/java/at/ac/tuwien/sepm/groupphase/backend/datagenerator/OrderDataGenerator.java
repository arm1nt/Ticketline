package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.service.OrderService;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Profile("generateData")
@Component
@DependsOn({"LayoutDataGenerator", "UserDataGenerator", "TicketDataGenerator", "EventDataGenerator", "PerformanceDataGenerator"})
public class OrderDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_ORDERS_TO_GENERATE_PER_PERFORMANCE = 5;

    private static final int NUMBER_OF_PERFORMANCES_PER_EVENT = 2; //set in performance generator

    private static final int MAX_NUMBER_OF_EVENTS_TO_GENERATE_ORDERS_FOR = 220; // max possible, if more or equal number given

    private static final String TEST_RESERVATION_CODE = "ReservationCode";

    private final LayoutRepository layoutRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrderRepository orderRepository;

    private final PerformanceRepository performanceRepository;

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final OrderService orderService;
    private final TicketService ticketService;
    private final TicketPdfRepository ticketPdfRepository;

    public OrderDataGenerator(LayoutRepository layoutRepository, OrderRepository orderRepository, TicketRepository ticketRepository,
                              PasswordEncoder passwordEncoder,
                              UserRepository userRepository, PerformanceRepository performanceRepository,
                              EventRepository eventRepository, OrderService orderService,
                              TicketService ticketService, TicketPdfRepository ticketPdfRepository) {
        this.layoutRepository = layoutRepository;
        this.orderRepository = orderRepository;
        this.ticketRepository = ticketRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.performanceRepository = performanceRepository;
        this.eventRepository = eventRepository;
        this.orderService = orderService;
        this.ticketPdfRepository = ticketPdfRepository;
        this.ticketService = ticketService;
    }

    @PostConstruct
    private void generateOrders() throws IOException {
        Order order;

        LOGGER.debug("generation of up to {} order entries", MAX_NUMBER_OF_EVENTS_TO_GENERATE_ORDERS_FOR * NUMBER_OF_PERFORMANCES_PER_EVENT * NUMBER_OF_ORDERS_TO_GENERATE_PER_PERFORMANCE );
        if (orderRepository.getOrderById(1L) == null) {
            int i = 0;
            List<Event> events = this.eventRepository.findAll();
            for (Event event:
                events  ) {
                i++;
                if (i > MAX_NUMBER_OF_EVENTS_TO_GENERATE_ORDERS_FOR)
                {
                    return;
                }
                for (Performance p :
                    event.getPerformances()) {

                    List<Ticket> tickets = new java.util.ArrayList<>(this.ticketRepository.getTicketsByPerformanceId(p.getId()).stream().toList());
                    for (int j = 1; j < NUMBER_OF_ORDERS_TO_GENERATE_PER_PERFORMANCE + 1; j++) {
                        double total = 0;
                        long userId = (long) (Math.random() * 500) + 1;
                        ApplicationUser user = this.userRepository.findById(userId).get();
                        int orderedTicketsCount = (int) (Math.random() * 2) + 1;
                        int id = (int) (Math.random() * tickets.size());
                        if (id > 3) {
                            id -= 3;
                        }
                        Set<Ticket> orderedTickets = new HashSet<>();
                        for (int k = 0; k < orderedTicketsCount; k++) {
                            if (tickets.get(id + k).getTicketStatus() == TicketStatus.FREE) {
                                Ticket t = tickets.get(id + k);
                                TicketPdf ticketPdf = null;
                                if (j % 2 == 0) {

                                    try {
                                        ticketPdf = TicketPdf.builder()
                                            .ticketPdf(this.ticketService.createPdfOfTicket(t.getId()))
                                            .build();
                                        this.ticketPdfRepository.save(ticketPdf);
                                        t.setTicketPdf(ticketPdf);
                                    } catch (Exception e) {
                                        LOGGER.debug("Error creating pdf of ticket");
                                    }

                                    t.setTicketStatus(TicketStatus.PURCHASED);
                                    t.setApplicationUser(user);
                                    tickets.set(id + k, t);
                                    total += t.getPrice();
                                    int soldTickets = p.getSoldTickets();
                                    p.setSoldTickets(soldTickets+1);
                                } else {
                                    t.setTicketStatus(TicketStatus.RESERVED);
                                    t.setApplicationUser(user);
                                    tickets.set(id + k, t);
                                }

                                ticketRepository.saveAndFlush(t);

                                orderedTickets.add(t);
                            }
                        }


                        if (!orderedTickets.isEmpty()) {
                            if (j % 2 == 0) {
                                performanceRepository.save(p);
                                order = Payment.builder()
                                    .time(LocalDateTime.now())
                                    .tickets(orderedTickets)
                                    .performanceId(p.getId())
                                    .total(total)
                                    .applicationUser(user)
                                    .build();
                            } else {
                                order = Reservation.builder()
                                    .reservationCode(TEST_RESERVATION_CODE + "" + i + "" + j + "" + orderedTicketsCount)
                                    .dueTime(LocalDateTime.of(2024, ((i * j) % 12) + 1, ((i + j) % 29) + 1, 12, 00, 00))
                                    .tickets(orderedTickets)
                                    .performanceId(p.getId())
                                    .applicationUser(user)
                                    .build();
                            }
                            order = orderRepository.save(order);
                            if(order.getClass() == Payment.class) {
                                this.orderService.createInvoice(order.getApplicationUser().getUsername(), order.getId(), ((Payment) order).getTime(), order.getTickets());
                            }
                        }
                    }

                }
            }
        }

    }

}
