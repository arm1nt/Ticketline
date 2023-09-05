package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Invoice;
import at.ac.tuwien.sepm.groupphase.backend.entity.Payment;
import at.ac.tuwien.sepm.groupphase.backend.entity.Row;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seating;
import at.ac.tuwien.sepm.groupphase.backend.entity.Stand;
import at.ac.tuwien.sepm.groupphase.backend.entity.Standing;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RowRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StandRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.InvoiceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.OrderService;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Profile("generateData")
@Component
public class PdfDataGenerator {

    private static final long TEST_ADMIN_ID = 200_000;
    private static final int NUMBER_OF_ADMINS_TO_GENERATE = 2;
    private static final String TEST_ADMIN_USERNAME = "AdminUsername";
    private static final String TEST_ADMIN_FIRSTNAME = "Firstname";
    private static final String TEST_ADMIN_LASTNAME = "Lastname";
    private static final String TEST_ADMIN_EMAIL = "admin";
    private static final String TEST_ADMIN_PASSWORD = "password";
    private static final String TEST_ADMIN_COUNTRY = "Austria";
    private static final String TEST_ADMIN_CITY = "Vienna";
    private static final String TEST_ADMIN_ZIPCODE = "1120";
    private static final String TEST_ADMIN_STREET = "Generic Street Name";

    private final OrderRepository orderRepository;
    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final RowRepository rowRepository;
    private final StandRepository standRepository;
    private final SectorRepository sectorRepository;
    private final InvoiceRepository invoiceRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final OrderService orderService;

    public PdfDataGenerator(OrderRepository orderRepository,
                            TicketRepository ticketRepository,
                            SeatRepository seatRepository,
                            RowRepository rowRepository,
                            StandRepository standRepository,
                            SectorRepository sectorRepository,
                            InvoiceRepository invoiceRepository,
                            UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            OrderService orderService) {
        this.orderRepository = orderRepository;
        this.ticketRepository = ticketRepository;
        this.seatRepository = seatRepository;
        this.rowRepository = rowRepository;
        this.standRepository = standRepository;
        this.sectorRepository = sectorRepository;
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.orderService = orderService;
    }

    @PostConstruct
    private void generateDataNeededToGetPdf() throws IOException {

        //Obsolete because the orderGenerator creates pdfs implicitly
        //But this class is kept for testing purposes.
    }

}
