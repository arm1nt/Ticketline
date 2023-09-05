package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventHall;
import at.ac.tuwien.sepm.groupphase.backend.entity.Layout;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.Stand;
import at.ac.tuwien.sepm.groupphase.backend.entity.Standing;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventHallRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LayoutRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StandRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.TicketService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class TicketServiceTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private LayoutRepository layoutRepository;

    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private StandRepository standRepository;

    @Autowired
    private TicketService ticketService;


    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private EventHallRepository eventHallRepository;

    @Autowired
    private UserService userService;

    private ApplicationUser user;

    private ApplicationUser otherUser;

    private Ticket ticket;

    private Ticket ticket2;

    private Location location;
    private EventHall eventHall;
    private Performance performance;
    private Standing standing;
    private Stand stand;
    private Layout layout;

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
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

        otherUser = ApplicationUser.builder()
            .username(DEFAULT_USER_USERNAME + "a")
            .firstName(DEFAULT_USER_FIRSTNAME + "a")
            .lastName(DEFAULT_USER_LASTNAME + "a")
            .email(2 + DEFAULT_USER_EMAIL)
            .password(DEFAULT_USER_PASSWORD + 2)
            .country(DEFAULT_USER_COUNTRY + "A")
            .city(DEFAULT_USER_CITY + "A")
            .zipCode(DEFAULT_ZIP_CODE + 2)
            .street(DEFAULT_STREET + "a")
            .build();

        location = Location.builder()
            .name("name")
            .country("county")
            .city("ciy")
            .street("street")
            .zipCode("zipCode")
            .build();

        eventHall = EventHall.builder()
            .name("eventhall")
            .build();

        performance = Performance.builder()
            .performanceName("performance")
            .startTime(LocalDateTime.now())
            .endTime(LocalDateTime.now())
            .build();

        layout = Layout.builder()
            .name("layout")
            .build();

        standing = Standing.builder()
            .capacity(20)
            .price(20)
            .sectorId("20")
            .build();

        stand = Stand.builder().build();

        ticket = Ticket.builder()
            .ticketStatus(TicketStatus.PURCHASED)
            .seat(null)
            .stand(null)
            .applicationUser(null)
            .build();

        ticket2 = Ticket.builder()
            .ticketStatus(TicketStatus.PURCHASED)
            .seat(null)
            .stand(null)
            .applicationUser(null)
            .build();

    }

    @AfterEach
    public void afterEach() {
        this.layoutRepository.deleteAll();
        this.eventHallRepository.deleteAll();
        this.locationRepository.deleteAll();
        this.performanceRepository.deleteAll();
        this.ticketRepository.deleteAll();
        this.standRepository.deleteAll();
        this.standRepository.deleteAll();
        this.userRepository.deleteAll();
    }

    @Test
    public void givenTicket_whenCreatingPdf_thenGetPdfAsBase64String() throws Exception {

        this.location = this.locationRepository.save(this.location);
        this.eventHall.setLocation(this.location);
        this.eventHall = this.eventHallRepository.save(this.eventHall);

        this.layout.setEventHall(this.eventHall);

        this.performance = this.performanceRepository.save(this.performance);
        Set<Performance> perf = new HashSet<>();
        perf.add(this.performance);
        this.layout.setPerformances(perf);
        this.layout = this.layoutRepository.save(this.layout);
        this.performance.setLayout(this.layout);
        this.performance = this.performanceRepository.save(this.performance);

        this.user = this.userRepository.save(this.user);
        this.ticket.setPerformance(this.performance);
        this.ticket.setApplicationUser(this.user);

        this.ticket = this.ticketRepository.save(this.ticket);

        Set<Ticket> tickets = new HashSet<>();
        tickets.add(this.ticket);
        this.stand.setTickets(tickets);
        this.stand = this.standRepository.save(this.stand);

        this.ticket.setStand(this.stand);
        this.ticket = this.ticketRepository.save(this.ticket);

        Set<Stand> stands = new HashSet<>();
        stands.add(this.stand);
        this.standing.setStands(stands);
        this.standing = this.sectorRepository.save(this.standing);

        this.stand.setStanding(this.standing);
        this.stand = this.standRepository.save(this.stand);

        Set<Sector> sectors = new HashSet<>();
        sectors.add(this.standing);
        this.layout.setSectors(sectors);
        this.layout = this.layoutRepository.save(this.layout);

        this.standing.setLayout(this.layout);
        this.standing = this.sectorRepository.save(this.standing);

        Set<Layout> layouts = new HashSet<>();
        layouts.add(this.layout);
        this.eventHall.setLayouts(layouts);
        this.eventHall = this.eventHallRepository.save(this.eventHall);

        Ticket ticket1 = this.ticket;

        String result = this.ticketService.createPdfOfTicket(ticket1.getId());

        assertThat(result).isNotNull();
        assertThat(result).isBase64();
    }

}
