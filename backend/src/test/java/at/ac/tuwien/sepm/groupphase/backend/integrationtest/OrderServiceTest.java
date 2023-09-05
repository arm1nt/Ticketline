package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.OrderMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Payment;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.repository.InvoiceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class OrderServiceTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    PerformanceRepository performanceRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private OrderService orderService;

    private ApplicationUser user;

    private ApplicationUser user2;

    private Set<Ticket> tickets;

    private Payment payment;

    private Performance performance;

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
        invoiceRepository.deleteAll();
        orderRepository.deleteAll();
        ticketRepository.deleteAll();

        user = ApplicationUser.builder()
            .username(DEFAULT_USER_USERNAME)
            .firstName(DEFAULT_USER_FIRSTNAME)
            .lastName(DEFAULT_USER_LASTNAME)
            .email(DEFAULT_USER_EMAIL)
            .password(DEFAULT_USER_PASSWORD)
            .country(DEFAULT_USER_COUNTRY)
            .city(DEFAULT_USER_CITY)
            .zipCode(DEFAULT_ZIP_CODE)
            .street(DEFAULT_STREET)
            .build();

        user2 = ApplicationUser.builder()
            .username(DEFAULT_USER_USERNAME + "2")
            .firstName(DEFAULT_USER_FIRSTNAME + "2")
            .lastName(DEFAULT_USER_LASTNAME + "2")
            .email(DEFAULT_USER_EMAIL + "2")
            .password(DEFAULT_USER_PASSWORD + "2")
            .country(DEFAULT_USER_COUNTRY + "2")
            .city(DEFAULT_USER_CITY + "2")
            .zipCode(DEFAULT_ZIP_CODE + "2")
            .street(DEFAULT_STREET + "2")
            .build();

        userRepository.save(user);
        userRepository.save(user2);

        performance = Performance.builder()
            .performanceName("performanceName")
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now())
            .build();

        performanceRepository.save(performance);


        tickets = new HashSet<>();

        for(int i = 0; i  < 15; i++) {
            Ticket ticket = Ticket.builder()
                .ticketId("ID" + i)
                .performance(performance)
                .build();
            tickets.add(ticket);
            ticketRepository.save(ticket);
        }

        payment = Payment.builder()
            .time(LocalDateTime.now())
            .total(200D)
            .performanceId(12)
            .applicationUser(user)
            .tickets(tickets)
            .build();
    }

    @AfterEach
    public void afterAll() {
        orderRepository.deleteAll();
        userRepository.deleteAll();
        invoiceRepository.deleteAll();
        ticketRepository.deleteAll();
    }


    @Test
    public void givenPayment_whenGeneratingInvoice_storeInvoiceInDatabaseAndReturnNothing() throws IOException {
        long id = orderRepository.save(payment).getId();

        this.orderService.createInvoice(user.getUsername(), id, LocalDateTime.now(), tickets);

        assertThat(invoiceRepository.findAll().size()).isEqualTo(1);
    }
}
