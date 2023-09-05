package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.LayoutDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.TicketDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.InvoicePdfDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.OrderDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.OrderOverviewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.TicketDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.OrderMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.TicketMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Layout;
import at.ac.tuwien.sepm.groupphase.backend.entity.Payment;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.entity.Reservation;
import at.ac.tuwien.sepm.groupphase.backend.entity.Row;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seat;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seating;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.entity.Stand;
import at.ac.tuwien.sepm.groupphase.backend.entity.Standing;
import at.ac.tuwien.sepm.groupphase.backend.entity.Ticket;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventHallRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GeometryRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.InvoiceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LayoutRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RowRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StandRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.EventHallService;
import at.ac.tuwien.sepm.groupphase.backend.service.OrderService;
import at.ac.tuwien.sepm.groupphase.backend.type.TicketStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class OrderEndpointTest implements TestData {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

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
    private TicketRepository ticketRepository;

    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private LayoutRepository layoutRepository;
    @Autowired
    private EventHallRepository eventHallRepository;
    @Autowired
    private SectorRepository sectorRepository;
    @Autowired
    private StandRepository standRepository;
    @Autowired
    private RowRepository rowRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private GeometryRepository geometryRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private PerformanceRepository performanceRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private EventHallService eventHallService;


    private ApplicationUser user;

    private ApplicationUser otherUser;

    private ApplicationUser admin;

    private ApplicationUser user2;

    private ApplicationUser user3;

    private Set<Ticket> tickets;

    private ArrayList<Set<Ticket>> ticketsBought;

    private ArrayList<Set<Ticket>> ticketsReserved;

    private Payment[] payment;

    private Reservation[] reservation;

    private long currentLayoutId = 0;

    private long id = 0;

    private long performanceId;

    @BeforeEach()
    public void beforeEach() {
        eventHallRepository.deleteAll();
        locationRepository.deleteAll();
        orderRepository.deleteAll();
        invoiceRepository.deleteAll();
        ticketRepository.deleteAll();
        performanceRepository.deleteAll();
        layoutRepository.deleteAll();
        userRepository.deleteAll();



        LayoutDataGenerator layoutDataGenerator = new LayoutDataGenerator(layoutRepository, eventHallRepository,
            sectorRepository, standRepository, rowRepository, seatRepository, ticketRepository, geometryRepository,
            orderRepository, performanceRepository, locationRepository, objectMapper, eventHallService);
        currentLayoutId = layoutDataGenerator.generateCustomLayout();
        TicketDataGenerator ticketDataGenerator = new TicketDataGenerator(ticketRepository, eventRepository, performanceRepository, layoutRepository);


        LOGGER.trace("generateTickets()");
        Layout l = this.layoutRepository.getLayoutById(currentLayoutId);
        Performance performance = ticketDataGenerator.generatePerformance(l);
        performanceId = performance.getId();
        Ticket ticket;
        int amountOfRows;
        int i = 0;
        int j = 0;
        int k = 0;
        for (Sector s :
            l.getSectors()) {
            if (s.getClass() == Seating.class) {
                amountOfRows = ((Seating) s).getRows().size();
                for (Row r : ((Seating) s).getRows()) {
                    i++;
                    for (Seat seat :
                        r.getSeats()) {
                        j++;
                        ticket = ticketDataGenerator.generateTicket(("Seat " + seat.getSeatId() + " - Row " + r.getRowNumber() + " - Sector " + s.getSectorId()), TicketStatus.FREE, seat, performance, s.getPrice());
                        ticketRepository.save(ticket);
                    }
                }
            } else if (s.getClass() == Standing.class) {
                for (Stand stand :
                    ((Standing) s).getStands()) {
                    ticket = ticketDataGenerator.generateTicket("Stand " + ++k, TicketStatus.FREE, stand, performance, s.getPrice());
                    ticketRepository.save(ticket);
                }
            }
        }

        Set<Ticket> ticketsForThisPerformance = ticketRepository.getTicketsByPerformanceId(performance.getId());
        id = 10000000;
        for (Ticket t :
            ticketsForThisPerformance) {
            if (id > t.getId()) {
                id = t.getId(); //min id for this performance
            }
        }

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

        admin = ApplicationUser.builder()
            .username(TEST_ADMIN_USERNAME)
            .firstName(TEST_ADMIN_FIRSTNAME)
            .lastName(TEST_ADMIN_LASTNAME)
            .email(TEST_ADMIN_EMAIL + "@email.com")
            .password(TEST_ADMIN_PASSWORD)
            .country(TEST_ADMIN_COUNTRY)
            .city(TEST_ADMIN_CITY)
            .zipCode(TEST_ADMIN_ZIPCODE)
            .street(TEST_ADMIN_STREET)
            .admin(true)
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

        user3 = ApplicationUser.builder()
            .username(DEFAULT_USER_USERNAME + "3")
            .firstName(DEFAULT_USER_FIRSTNAME + "3")
            .lastName(DEFAULT_USER_LASTNAME + "3")
            .email(DEFAULT_USER_EMAIL + "3")
            .password(DEFAULT_USER_PASSWORD + "3")
            .country(DEFAULT_USER_COUNTRY + "3")
            .city(DEFAULT_USER_CITY + "3")
            .zipCode(DEFAULT_ZIP_CODE + "3")
            .street(DEFAULT_STREET + "3")
            .admin(true)
            .build();

        userRepository.save(user);
        userRepository.save(otherUser);
        userRepository.save(admin);
        userRepository.save(user2);
        userRepository.save(user3);

        ticketsBought = new ArrayList<>();
        ticketsReserved = new ArrayList<>();
        payment = new Payment[2];
        reservation = new Reservation[2];


        for (int x = 0; x < 2; x++) {

            for (int y = 0; y < 2; y++) {

                Set<Long> ids;
                Set<Ticket> tickets;
                if (y == 0) {
                    ids = new HashSet<>(Arrays.asList((id + 2) + x * 2L, (id + 3) + x * 2L));
                } else {
                    ids = new HashSet<>(Arrays.asList((id + 100) + x * 2L + 4, (id + 101) + x * 2L));
                }
                tickets = ticketRepository.getTicketsByIds(ids);
                for (Ticket t :
                    tickets) {
                    if (x % 2 == 0) {
                        t.setTicketStatus(TicketStatus.PURCHASED);
                    } else {
                        t.setTicketStatus(TicketStatus.RESERVED);
                    }
                    ticketRepository.updateTicket(t.getId(), t.getTicketStatus());
                }

                if (x % 2 == 0) {
                    ticketsBought.add(tickets);
                    payment[y] = Payment.builder()
                        .time(LocalDateTime.now())
                        .tickets(tickets)
                        .performanceId(performance.getId())
                        .applicationUser((y % 2 == 0) ? user : otherUser)
                        .build();
                    LOGGER.info("saving payment {}", payment[y]);
                    orderRepository.save(payment[y]);
                } else {
                    ticketsReserved.add(tickets);
                    reservation[y] = Reservation.builder()
                        .reservationCode(TEST_RESERVATION_CODE)
                        .dueTime(performance.getStartTime().minusMinutes(30L))
                        .tickets(tickets)
                        .performanceId(performance.getId())
                        .applicationUser((y % 2 == 0) ? user : otherUser)
                        .build();
                    LOGGER.info("saving reservation {}", reservation[y]);
                    orderRepository.save(reservation[y]);
                }

            }
        }


    }


    @AfterEach
    public void afterAll() {
        orderRepository.deleteAll();
        invoiceRepository.deleteAll();
        ticketRepository.deleteAll();
        performanceRepository.deleteAll();
        layoutRepository.deleteAll();
        userRepository.deleteAll();
        eventHallRepository.deleteAll();
        locationRepository.deleteAll();
    }


    @Test
    public void givenPayment_whenRequestingInvoiceWithValidCredentials_storeInvoiceInDatabaseAndReturnNothing() throws Exception {

        this.orderService.createInvoice(payment[1].getApplicationUser().getUsername(), payment[1].getId(), LocalDateTime.now(), ticketsBought.get(0));

        MvcResult mvcResult = this.mockMvc.perform(get(ORDER_BASE_URI + "/invoice" + "/" + payment[1].getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(payment[1].getApplicationUser().getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        InvoicePdfDto invoice = objectMapper.readValue(response.getContentAsString(), InvoicePdfDto.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertThat(invoice.getPdf()).isBase64();
    }

    @Test
    public void givenPayment_whenRequestingInvoiceWithInvalidCredentials_get403() throws Exception {

        this.orderService.createInvoice(payment[1].getApplicationUser().getUsername(), payment[1].getId(), LocalDateTime.now(), ticketsBought.get(0));

        MvcResult mvcResult = this.mockMvc.perform(get(ORDER_BASE_URI + "/invoice" + "/" + payment[1].getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(payment[0].getApplicationUser().getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    public void givenPayment_whenRequestingInvoiceOfDifferentUserButWithAdminRole_getInvoice() throws Exception {

        this.orderService.createInvoice(payment[1].getApplicationUser().getUsername(), payment[1].getId(), LocalDateTime.now(), ticketsBought.get(0));

        MvcResult mvcResult = this.mockMvc.perform(get(ORDER_BASE_URI + "/invoice" + "/" + payment[1].getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user3.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        InvoicePdfDto invoice = objectMapper.readValue(response.getContentAsString(), InvoicePdfDto.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertThat(invoice.getPdf()).isBase64();
    }

    @Test
    public void givenPayment_whenRequestingInvoiceOfNotExistingOrder_get404() throws Exception {
        this.orderService.createInvoice(payment[1].getApplicationUser().getUsername(), payment[1].getId(), LocalDateTime.now(), ticketsBought.get(0));

        MvcResult mvcResult = this.mockMvc.perform(get(ORDER_BASE_URI + "/invoice" + "/" + -999)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());

    }

    @Test
    public void givenReservation_whenRequestingInvoiceOfReservation_get403() throws Exception {
        long id = reservation[0].getId();

        MvcResult mvcResult = this.mockMvc.perform(get(ORDER_BASE_URI + "/invoice" + "/" + id)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());

    }


    @Test
    public void givenReservation_whenGetByIdAndBeingItsOwner_thenOrderDto()
        throws Exception {

        reservation[0] = orderRepository.save(reservation[0]);

        /*for(Ticket t : ticketsReserved) {
            t.setOrder(reservation);
        }

        ticketRepository.saveAll(ticketsReserved);*/
        LOGGER.debug("Getting reservation of " + reservation[0].getApplicationUser().getId() + " being user " + user.getId());


        MvcResult mvcResult = this.mockMvc.perform(get(BOOKING_BASE_URI + "/" + reservation[0].getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        OrderDto orderDto = objectMapper.readValue(response.getContentAsString(), OrderDto.class);
        assertNotNull(orderDto.getId());
        assertThat(orderDto.getTickets()).isEqualTo(ticketMapper.ticketToTicketDto(ticketsReserved.get(0)));
        assertThat(orderDto.getReservationCode()).isEqualTo(TEST_RESERVATION_CODE);
        assertNotNull(orderDto.getDueTime());
        assertNull(orderDto.getTime());
        assertNull(orderDto.getTotal());

    }

    @Test
    public void givenPayment_whenGetByIdAndBeingItsOwner_thenOrderDto()
        throws Exception {

        payment[0] = orderRepository.save(payment[0]);

        /*for(Ticket t : ticketsBought) {
            t.setOrder(payment);
        }

        ticketRepository.saveAll(ticketsBought);*/
        LOGGER.debug("Getting payment of " + payment[0].getApplicationUser().getId() + " being user " + user.getId());

        MvcResult mvcResult = this.mockMvc.perform(get(BOOKING_BASE_URI + "/" + payment[0].getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        System.out.println("SAIHSJDKLASHDAKLSDHJ: " + ticketRepository.findAll().size());

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        OrderDto orderDto = objectMapper.readValue(response.getContentAsString(), OrderDto.class);
        assertNotNull(orderDto.getId());
        assertThat(orderDto.getTickets()).isEqualTo(ticketMapper.ticketToTicketDto(ticketsBought.get(0)));
        assertNotNull(orderDto.getTime());
        assertNull(orderDto.getReservationCode());
        assertNull(orderDto.getDueTime());

    }

    @Test
    public void givenOrder_whenGetByIdAndBeingAnAdmin_thenOrderDto()
        throws Exception {

        payment[1] = orderRepository.save(payment[1]);

        /*for(Ticket t : ticketsBought) {
            t.setOrder(payment);
        }*/

        ticketRepository.saveAll(ticketsBought.get(1));

        MvcResult mvcResult = this.mockMvc.perform(get(BOOKING_BASE_URI + "/" + payment[1].getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(admin.getUsername(), ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        OrderDto orderDto = objectMapper.readValue(response.getContentAsString(), OrderDto.class);
        assertNotNull(orderDto.getId());
        assertThat(orderDto.getTickets()).isEqualTo(ticketMapper.ticketToTicketDto(ticketsBought.get(1)));
    }

    @Test
    public void givenOrder_whenGetByIdAndNotBeingOwnerOrAdmin_then401()
        throws Exception {

        payment[1] = orderRepository.save(payment[1]);

        MvcResult mvcResult = this.mockMvc.perform(get(BOOKING_BASE_URI + "/" + payment[1].getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());

    }

    @Test
    public void givenPayment_whenPost_thenOrderDto()
        throws Exception {

        Set<Long> ids;
        Set<Ticket> tickets;
        Set<TicketDto> ticketDtos = new HashSet<>();
        ids = new HashSet<>(Arrays.asList(id, id + 1));
        tickets = ticketRepository.getTicketsByIds(ids);
        for (Ticket t :
            tickets) {
            t.setTicketStatus(TicketStatus.PURCHASED);
            ticketDtos.add(new TicketDto(t.getId(), t.getTicketId(), t.getTicketStatus()));
        }

        OrderDto paymentDto = OrderDto.builder()
            .tickets(ticketDtos)
            .performanceId(performanceId)
            .build();

        String body = objectMapper.writeValueAsString(paymentDto);

        MvcResult mvcResult = this.mockMvc.perform(post(BOOKING_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(otherUser.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        OrderDto orderDto = objectMapper.readValue(response.getContentAsString(), OrderDto.class);
        assertNotNull(orderDto.getId());
        assertThat(orderDto.getPerformanceId()).isEqualTo(performanceId);
        assertNotNull(orderDto.getTotal());
        assertNotNull(orderDto.getTime());
        assertNull(orderDto.getReservationCode());
        assertNull(orderDto.getDueTime());

        ticketDtos = orderDto.getTickets();
        for (TicketDto t : ticketDtos
        ) {
            assertEquals(t.getTicketStatus(), TicketStatus.PURCHASED);
        }
    }

    @Test
    public void givenReservation_whenPost_thenOrderDto()
        throws Exception {
        Set<Long> ids;
        Set<Ticket> tickets;
        Set<TicketDto> ticketDtos = new HashSet<>();
        ids = new HashSet<>(Arrays.asList(id, id + 1));
        tickets = ticketRepository.getTicketsByIds(ids);
        for (Ticket t :
            tickets) {
            t.setTicketStatus(TicketStatus.RESERVED);
            ticketDtos.add(new TicketDto(t.getId(), t.getTicketId(), t.getTicketStatus()));
        }

        OrderDto reservationDto = OrderDto.builder()
            .tickets(ticketDtos)
            .performanceId(performanceId)
            .build();

        String body = objectMapper.writeValueAsString(reservationDto);

        MvcResult mvcResult = this.mockMvc.perform(post(BOOKING_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(otherUser.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        OrderDto orderDto = objectMapper.readValue(response.getContentAsString(), OrderDto.class);
        assertNotNull(orderDto.getId());
        assertThat(orderDto.getPerformanceId()).isEqualTo(performanceId);
        assertNull(orderDto.getTotal());
        assertNull(orderDto.getTime());
        assertNotNull(orderDto.getReservationCode());
        assertNotNull(orderDto.getDueTime());

        ticketDtos = orderDto.getTickets();
        for (TicketDto t : ticketDtos
        ) {
            assertEquals(t.getTicketStatus(), TicketStatus.RESERVED);
        }
    }

    @Test
    public void givenReservation_whenBoughtBeingOwner_thenOrderDtoWithReservedTickets()
        throws Exception {

        Set<Long> ids;
        Set<Ticket> tickets;
        Set<TicketDto> ticketDtos = new HashSet<>();
        ids = new HashSet<>(Arrays.asList(id, id + 1));
        tickets = ticketRepository.getTicketsByIds(ids);
        LOGGER.info("Current layout: " + currentLayoutId + "\n");
        LOGGER.info("Current id: " + id + "\n");
        LOGGER.info("Tickets:\n");
        for (Ticket t :
            tickets) {
            LOGGER.info("TicketId: " + t.getId() + "\n");
            t.setTicketStatus(TicketStatus.RESERVED);
            ticketDtos.add(new TicketDto(t.getId(), t.getTicketId(), t.getTicketStatus()));
        }
        System.out.println();

        OrderDto reservationDto = OrderDto.builder()
            .tickets(ticketDtos)
            .performanceId(performanceId)
            .build();

        String body = objectMapper.writeValueAsString(reservationDto);

        MvcResult mvcResult = this.mockMvc.perform(post(BOOKING_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(otherUser.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        System.out.println(response.getContentAsString());

        OrderDto orderDto = objectMapper.readValue(response.getContentAsString(), OrderDto.class);
        ticketDtos = orderDto.getTickets();
        for (TicketDto t : ticketDtos
        ) {
            t.setTicketStatus(TicketStatus.PURCHASED);
        }
        orderDto.setTickets(ticketDtos);

        body = objectMapper.writeValueAsString(orderDto);

        mvcResult = this.mockMvc.perform(put(BOOKING_BASE_URI + "/" + orderDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(otherUser.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        orderDto = objectMapper.readValue(response.getContentAsString(), OrderDto.class);
        assertNotNull(orderDto.getId());
        assertThat(orderDto.getTickets().size()).isEqualTo(2);
        assertThat(orderDto.getPerformanceId()).isEqualTo(performanceId);
        assertNotNull(orderDto.getTotal());
        assertNotNull(orderDto.getTime());
        assertNull(orderDto.getReservationCode());
        assertNull(orderDto.getDueTime());

        ticketDtos = orderDto.getTickets();
        for (TicketDto t : ticketDtos
        ) {
            assertEquals(t.getTicketStatus(), TicketStatus.PURCHASED);
        }

    }

    @Test
    public void givenPayment_whenCanceledBeingOwner_thenOrderDtoWithCanceledTickets()
        throws Exception {

        Set<Long> ids;
        Set<Ticket> tickets;
        Set<TicketDto> ticketDtos = new HashSet<>();

        ids = new HashSet<>(Arrays.asList(id, id + 1));
        tickets = ticketRepository.getTicketsByIds(ids);
        LOGGER.info("Current layout: " + currentLayoutId + "\n");
        LOGGER.info("Current id: " + id + "\n");
        LOGGER.info("Tickets:\n");
        for (Ticket t :
            tickets) {
            LOGGER.info("TicketId: " + t.getId() + "\n");
            t.setTicketStatus(TicketStatus.PURCHASED);
            ticketDtos.add(new TicketDto(t.getId(), t.getTicketId(), t.getTicketStatus()));
        }

        OrderDto paymentDto = OrderDto.builder()
            .tickets(ticketDtos)
            .performanceId(performanceId)
            .build();

        String body = objectMapper.writeValueAsString(paymentDto);

        MvcResult mvcResult = this.mockMvc.perform(post(BOOKING_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(otherUser.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        System.out.println(response.getContentAsString());

        OrderDto orderDto = objectMapper.readValue(response.getContentAsString(), OrderDto.class);
        ticketDtos = orderDto.getTickets();
        for (TicketDto t : ticketDtos
        ) {
            t.setTicketStatus(TicketStatus.CANCELED);
        }
        orderDto.setTickets(ticketDtos);

        body = objectMapper.writeValueAsString(orderDto);

        mvcResult = this.mockMvc.perform(put(BOOKING_BASE_URI + "/" + orderDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(otherUser.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();

        orderDto = objectMapper.readValue(response.getContentAsString(), OrderDto.class);
        assertNotNull(orderDto.getId());
        //assertThat(orderDto.getTickets().size()).isEqualTo(NUMBER_OF_TICKETS_TO_GENERATE);
        assertThat(orderDto.getTickets().size()).isEqualTo(0); //wollen nicht die gerade gecancellted tickets zurückgeben
        assertThat(orderDto.getPerformanceId()).isEqualTo(performanceId);
        assertNotNull(orderDto.getTotal());
        assertNotNull(orderDto.getTime());
        assertNull(orderDto.getReservationCode());
        assertNull(orderDto.getDueTime());
    }

    @Test
    public void givenReservation_whenCanceledBeingOwner_thenOrderDtoWithCanceledTickets()
        throws Exception {

        Set<Long> ids;
        Set<Ticket> tickets;
        Set<TicketDto> ticketDtos = new HashSet<>();
        ids = new HashSet<>(Arrays.asList(id, id + 1));
        tickets = ticketRepository.getTicketsByIds(ids);
        for (Ticket t :
            tickets) {
            t.setTicketStatus(TicketStatus.RESERVED);
            ticketDtos.add(new TicketDto(t.getId(), t.getTicketId(), t.getTicketStatus()));
        }

        OrderDto reservationDto = OrderDto.builder()
            .tickets(ticketDtos)
            .performanceId(performanceId)
            .build();

        String body = objectMapper.writeValueAsString(reservationDto);

        MvcResult mvcResult = this.mockMvc.perform(post(BOOKING_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(otherUser.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        System.out.println(response.getContentAsString());

        OrderDto orderDto = objectMapper.readValue(response.getContentAsString(), OrderDto.class);
        ticketDtos = orderDto.getTickets();
        for (TicketDto t : ticketDtos
        ) {
            t.setTicketStatus(TicketStatus.CANCELED);
        }
        orderDto.setTickets(ticketDtos);

        body = objectMapper.writeValueAsString(orderDto);

        mvcResult = this.mockMvc.perform(put(BOOKING_BASE_URI + "/" + orderDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(otherUser.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();

        orderDto = objectMapper.readValue(response.getContentAsString(), OrderDto.class);
        assertNotNull(orderDto.getId());
        //assertThat(orderDto.getTickets().size()).isEqualTo(NUMBER_OF_TICKETS_TO_GENERATE);
        assertThat(orderDto.getTickets().size()).isEqualTo(0); //wollen nicht die gerade gecancellted tickets zurückgeben
        assertThat(orderDto.getPerformanceId()).isEqualTo(performanceId);
        assertNull(orderDto.getTotal());
        assertNull(orderDto.getTime());
        assertNull(orderDto.getReservationCode());
        assertNull(orderDto.getDueTime());

    }

    @Test
    public void givenOrder_whenUpdatedAndBeingAdmin_thenUpdatedOrderDto()
        throws Exception {

        Set<Long> ids;
        Set<Ticket> tickets;
        Set<TicketDto> ticketDtos = new HashSet<>();
        ids = new HashSet<>(Arrays.asList(id, id + 1));
        tickets = ticketRepository.getTicketsByIds(ids);
        for (Ticket t :
            tickets) {
            t.setTicketStatus(TicketStatus.RESERVED);
            ticketDtos.add(new TicketDto(t.getId(), t.getTicketId(), t.getTicketStatus()));
        }

        OrderDto reservationDto = OrderDto.builder()
            .tickets(ticketDtos)
            .performanceId(performanceId)
            .build();

        String body = objectMapper.writeValueAsString(reservationDto);

        MvcResult mvcResult = this.mockMvc.perform(post(BOOKING_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(otherUser.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        System.out.println(response.getContentAsString());

        OrderDto orderDto = objectMapper.readValue(response.getContentAsString(), OrderDto.class);
        ticketDtos = orderDto.getTickets();
        for (TicketDto t : ticketDtos
        ) {
            t.setTicketStatus(TicketStatus.CANCELED);
        }
        orderDto.setTickets(ticketDtos);

        body = objectMapper.writeValueAsString(orderDto);

        mvcResult = this.mockMvc.perform(put(BOOKING_BASE_URI + "/" + orderDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(admin.getUsername(), ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();

        orderDto = objectMapper.readValue(response.getContentAsString(), OrderDto.class);
        assertNotNull(orderDto.getId());
        //assertThat(orderDto.getTickets().size()).isEqualTo(NUMBER_OF_TICKETS_TO_GENERATE);
        assertThat(orderDto.getTickets().size()).isEqualTo(0); //wollen nicht die gerade gecancellted tickets zurückgeben
        assertThat(orderDto.getPerformanceId()).isEqualTo(performanceId);
        assertNull(orderDto.getTotal());
        assertNull(orderDto.getTime());
        assertNull(orderDto.getReservationCode());
        assertNull(orderDto.getDueTime());

    }

    @Test
    public void givenOrder_whenUpdatedAndNotBeingOwnerOrAdmin_then401()
        throws Exception {

        Set<Long> ids;
        Set<Ticket> tickets;
        Set<TicketDto> ticketDtos = new HashSet<>();
        ids = new HashSet<>(Arrays.asList(id, id + 1));
        tickets = ticketRepository.getTicketsByIds(ids);
        for (Ticket t :
            tickets) {
            t.setTicketStatus(TicketStatus.RESERVED);
            ticketDtos.add(new TicketDto(t.getId(), t.getTicketId(), t.getTicketStatus()));
        }

        OrderDto reservationDto = OrderDto.builder()
            .tickets(ticketDtos)
            .performanceId(performanceId)
            .build();

        String body = objectMapper.writeValueAsString(reservationDto);

        MvcResult mvcResult = this.mockMvc.perform(post(BOOKING_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        System.out.println(response.getContentAsString());

        OrderDto orderDto = objectMapper.readValue(response.getContentAsString(), OrderDto.class);
        ticketDtos = orderDto.getTickets();
        for (TicketDto t : ticketDtos
        ) {
            t.setTicketStatus(TicketStatus.CANCELED);
        }
        orderDto.setTickets(ticketDtos);

        body = objectMapper.writeValueAsString(orderDto);

        mvcResult = this.mockMvc.perform(put(BOOKING_BASE_URI + "/" + orderDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(otherUser.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }


    @Test
    public void givenNothing_whenMakingValidPayment_thenPdfInvoiceCreated() throws Exception {
        Set<Long> ids;
        Set<Ticket> tickets;
        Set<TicketDto> ticketDtos = new HashSet<>();
        ids = new HashSet<>(Arrays.asList(id, id + 1));
        tickets = ticketRepository.getTicketsByIds(ids);
        for (Ticket t :
            tickets) {
            t.setTicketStatus(TicketStatus.PURCHASED);
            ticketDtos.add(new TicketDto(t.getId(), t.getTicketId(), t.getTicketStatus()));
        }

        OrderDto reservationDto = OrderDto.builder()
            .tickets(ticketDtos)
            .performanceId(performanceId)
            .build();

        String body = objectMapper.writeValueAsString(reservationDto);

        MvcResult mvcResult = this.mockMvc.perform(post(BOOKING_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(otherUser.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        System.out.println(response.getContentAsString());

        OrderDto orderDto = objectMapper.readValue(response.getContentAsString(), OrderDto.class);
        assertThat(invoiceRepository.findAll().size()).isGreaterThanOrEqualTo(1);
        assertThat(invoiceRepository.findAll().get(0).getUsername()).isEqualTo(otherUser.getUsername());
    }


    @Test
    public void givenPayment_CancelPartsOfPayment_GetOKAndUpdatedOrder() throws Exception {
        Set<Long> ids;
        Set<Ticket> tickets;
        Set<TicketDto> ticketDtos = new HashSet<>();
        ids = new HashSet<>(Arrays.asList(id, id + 1));
        tickets = ticketRepository.getTicketsByIds(ids);
        for (Ticket t :
            tickets) {
            t.setTicketStatus(TicketStatus.PURCHASED);
            ticketDtos.add(new TicketDto(t.getId(), t.getTicketId(), t.getTicketStatus()));
        }

        OrderDto reservationDto = OrderDto.builder()
            .tickets(ticketDtos)
            .performanceId(performanceId)
            .build();

        String body = objectMapper.writeValueAsString(reservationDto);

        MvcResult mvcResult = this.mockMvc.perform(post(BOOKING_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(otherUser.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        System.out.println(response.getContentAsString());

        OrderDto orderDto = objectMapper.readValue(response.getContentAsString(), OrderDto.class);

        List<TicketDto> ticketDtoList = new ArrayList<>();//orderDto.getTickets().stream().toList();
        orderDto.getTickets().stream().toList().get(0).setTicketStatus(TicketStatus.CANCELED);
        ticketDtoList.add(orderDto.getTickets().stream().toList().get(0));

        orderDto.setTickets(new HashSet<>(ticketDtoList));

        body = objectMapper.writeValueAsString(orderDto);

        mvcResult = this.mockMvc.perform(put(BOOKING_BASE_URI + "/" + orderDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(otherUser.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();


        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        orderDto = objectMapper.readValue(response.getContentAsString(), OrderDto.class);
        assertNotNull(orderDto.getId());
        assertThat(orderDto.getTickets().size()).isEqualTo(1); //wollen nicht die gerade gecancellted tickets zurückgeben
        assertThat(orderDto.getPerformanceId()).isEqualTo(performanceId);
        assertNotNull(orderDto.getTotal());
        assertNotNull(orderDto.getTime());
        assertNull(orderDto.getReservationCode());
        assertNull(orderDto.getDueTime());
    }

    @Test
    public void givenExistingOrdersBy2Users_getOrders_ListofOrderOverViewDtosOfUserWhoMakesTheRequest() throws Exception {
        //Setup
        orderRepository.deleteAll();

        Set<Long> ids;
        Set<Ticket> tickets;
        Set<TicketDto> ticketDtos = new HashSet<>();
        ids = new HashSet<>(List.of(id + 1));
        tickets = ticketRepository.getTicketsByIds(ids);
        for (Ticket t :
            tickets) {
            t.setTicketStatus(TicketStatus.PURCHASED);
            ticketDtos.add(new TicketDto(t.getId(), t.getTicketId(), t.getTicketStatus()));
        }

        OrderDto paymentDto = OrderDto.builder()
            .tickets(ticketDtos)
            .performanceId(performanceId)
            .build();

        String body = objectMapper.writeValueAsString(paymentDto);

        MvcResult mvcResult = this.mockMvc.perform(post(BOOKING_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(otherUser.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        ticketDtos = new HashSet<>();
        ids = new HashSet<>(List.of(id));
        tickets = ticketRepository.getTicketsByIds(ids);
        for (Ticket t :
            tickets) {
            t.setTicketStatus(TicketStatus.PURCHASED);
            ticketDtos.add(new TicketDto(t.getId(), t.getTicketId(), t.getTicketStatus()));
        }

        paymentDto = OrderDto.builder()
            .tickets(ticketDtos)
            .performanceId(performanceId)
            .build();

        body = objectMapper.writeValueAsString(paymentDto);

        mvcResult = this.mockMvc.perform(post(BOOKING_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        // Execution of test

        mvcResult = this.mockMvc.perform(get(BOOKING_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<OrderOverviewDto> orderOverviewDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            OrderOverviewDto[].class));

        assertEquals(1, orderOverviewDtos.size());
        OrderOverviewDto orderDto = orderOverviewDtos.get(0);
        Set<TicketDto> finalTicketDtos = ticketDtos;
        OrderDto finalPaymentDto = paymentDto;
        assertAll(
            () -> assertEquals(finalTicketDtos.size(), orderDto.getTickets().size()),
            () -> assertEquals(finalPaymentDto.getPerformanceId(), orderDto.getPerformanceId())
        );
    }
}