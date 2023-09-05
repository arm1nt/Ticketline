package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformanceSearchResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.PerformanceMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventHall;
import at.ac.tuwien.sepm.groupphase.backend.entity.Seating;
import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.type.EventType;
import at.ac.tuwien.sepm.groupphase.backend.entity.Layout;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performance;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventHallRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LayoutRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformanceRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PerformerRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.ADMIN_USERNAME;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.DEFAULT_USER_USERNAME;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.PERFORMANCE_BASE_URI;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.TEST_ARTIST_FIRSTNAME;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.TEST_ARTIST_LASTNAME;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.TEST_ARTIST_PERFORMERNAME;
import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.USER_ROLES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PerformanceEndpointTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    Location location0 = Location.builder()
        .name("Test Location")
        .city("City")
        .country("Country")
        .street("Street")
        .zipCode("1111")
        .build();
    EventHall eventhall = EventHall.builder().name("Test Eventhall").location(location0).build();
    Layout layout0 = Layout.builder().name("Test").eventHall(eventhall).sectors(new HashSet<>()).build();
    LocalDateTime performanceStartTime = LocalDateTime.of(LocalDate.of(2023, 6, 1), LocalTime.of(18, 0));
    Artist artistTest = Artist.builder()
        .performerName(TEST_ARTIST_PERFORMERNAME)
        .firstName(TEST_ARTIST_FIRSTNAME)
        .lastName(TEST_ARTIST_LASTNAME)
        .build();
    Event eventTest = Event.builder()
        .name("TestTest")
        .eventType(EventType.valueOf("OPERA"))
        .performers(Collections.singletonList(artistTest))
        .duration(60)
        .performances(new ArrayList<>())
        .build();
    Performance performance = Performance.builder()
        .startTime(performanceStartTime)
        .performanceName("Test")
        .endTime(performanceStartTime.plusMinutes(eventTest.getDuration()))
        .layout(layout0)
        .event(eventTest)
        .build();
    Event eventTest2 = Event.builder()
        .eventType(EventType.valueOf("CONCERT"))
        .name("Event2Test")
        .performers(Collections.singletonList(artistTest))
        .duration(120)
        .performances(new ArrayList<>())
        .build();
    Performance performance2 = Performance.builder()
        .performanceName("Test")
        .startTime(performanceStartTime)
        .endTime(performanceStartTime.plusMinutes(eventTest2.getDuration()))
        .layout(layout0)
        .event(eventTest2)
        .build();
    Event eventTest3 = Event.builder()
        .name("EventTest3")
        .eventType(EventType.valueOf("OPERA"))
        .performers(Collections.singletonList(artistTest))
        .duration(90)
        .performances(new ArrayList<>())
        .build();
    Performance performance3 = Performance.builder()
        .performanceName("Test")
        .startTime(performanceStartTime)
        .endTime(performanceStartTime.plusMinutes(eventTest3.getDuration()))
        .layout(layout0)
        .event(eventTest3)
        .build();

    PerformanceDto performanceDto = PerformanceDto.builder()
        .performanceName("Test")
        .startTime(performanceStartTime)
        .endTime(performanceStartTime.plusMinutes(eventTest2.getDuration()))
        .build();
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private PerformerRepository performerRepository;
    @Autowired
    private PerformanceRepository performanceRepository;
    @Autowired
    private LayoutRepository layoutRepository;
    @Autowired
    private PerformanceMapper performanceMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private JwtTokenizer jwtTokenizer;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EventHallRepository eventHallRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private SectorRepository sectorRepository;

    @BeforeTestClass
    public void beforeEach() {
        eventRepository.deleteAll();
        performerRepository.deleteAll();
        performanceRepository.deleteAll();
        sectorRepository.deleteAll();
        layoutRepository.deleteAll();
        eventHallRepository.deleteAll();
        locationRepository.deleteAll();
    }

    @BeforeEach
    public void beforeEach2() {
        eventRepository.deleteAll();
        performerRepository.deleteAll();
        performanceRepository.deleteAll();
        sectorRepository.deleteAll();
        layoutRepository.deleteAll();
        eventHallRepository.deleteAll();
        locationRepository.deleteAll();

    }

    @AfterEach
    public void afterEach() {
        eventRepository.deleteAll();
        performerRepository.deleteAll();
        performanceRepository.deleteAll();
        sectorRepository.deleteAll();
        layoutRepository.deleteAll();
        eventHallRepository.deleteAll();
        locationRepository.deleteAll();
    }


    @Transactional
    @Test
    public void givenNoPerformances_whenPost_thenPerformanceWithAllProperties() throws Exception {
        Location location = locationRepository.save(location0);
        LOGGER.info("location id is {}", location.getId());
        performanceDto.setLocationId(location.getId());
        EventHall eventHall1 = eventHallRepository.save(eventhall);
        performanceDto.setEventhallId(eventHall1.getId());
        Layout layout = layoutRepository.save(layout0);
        LOGGER.info("layout after save: {} {}", layout.getId(), layout);
        Set<Layout> layouts = new HashSet<>();
        layouts.add(layout);
        eventHall1.setLayouts(layouts);
        performanceDto.setLayoutId(layout.getId());
        performerRepository.save(artistTest);
        Event event = eventRepository.save(eventTest2);
        performanceDto.setEventId(event.getId());

        String body = objectMapper.writeValueAsString(performanceDto);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(PERFORMANCE_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        PerformanceDto performanceDto1 = objectMapper.readValue(response.getContentAsString(), PerformanceDto.class);

        assertNotNull(performanceDto1.getId());
        assertEquals(performanceDto.getStartTime(), performanceDto1.getStartTime());
        assertEquals(performanceDto.getEndTime(), performanceDto1.getEndTime());
    }

    @Test
    public void givenNothing_whenPostInvalid_then400() throws Exception {
        performerRepository.save(artistTest);
        eventRepository.save(eventTest);
        performance.setLayout(null);

        PerformanceDto performanceDto = performanceMapper.performanceToPerformanceDto(performance);
        String body = objectMapper.writeValueAsString(performanceDto);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(PERFORMANCE_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }


    @Transactional
    @Test
    public void givenNothing_whenPostInvalidTimesStillSucceed_thenCreated() throws Exception {
        Location location = locationRepository.save(performance2.getLayout().getEventHall().getLocation());
        performance2.getLayout().getEventHall().setLocation(location);
        EventHall eventHall = eventHallRepository.save(performance2.getLayout().getEventHall());
        performance2.getLayout().setEventHall(eventHall);
        Layout layout = layoutRepository.save(performance2.getLayout());
        LOGGER.info("layout after save: {} {}", layout.getId(), layout);
        Set<Layout> layouts = new HashSet<>();
        layouts.add(layout);
        performance2.getLayout().getEventHall().setLayouts(layouts);
        eventHallRepository.save(performance2.getLayout().getEventHall());
        layout.setName("test layout");
        layoutRepository.save(layout);
        performerRepository.save(artistTest);
        eventRepository.save(eventTest3);


        PerformanceDto performanceDto = performanceMapper.performanceToPerformanceDto(performance3);
        performanceDto.setLocationId(performance3.getLayout().getEventHall().getLocation().getId());
        performanceDto.setEventhallId(performance3.getLayout().getEventHall().getId());
        String body = objectMapper.writeValueAsString(performanceDto);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(PERFORMANCE_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    public void givenUser_WhenSearchingForExistingAttributes_ThenSearchReturnsCorrectValues() throws Exception{
        Location location = locationRepository.save(location0);
        LOGGER.info("location id is {}", location.getId());
        performanceDto.setLocationId(location.getId());
        EventHall eventHall1 = eventHallRepository.save(eventhall);
        performanceDto.setEventhallId(eventHall1.getId());
        Layout layout = layoutRepository.saveAndFlush(layout0);
        Seating seating = new Seating();
        seating.setPrice(50);
        seating.setLayout(layout);
        seating.setSectorId("1");
        Sector sector = sectorRepository.saveAndFlush(seating);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector);
        layout.setSectors(sectors);
        layout = layoutRepository.saveAndFlush(layout);
        LOGGER.info("layout after save: {} {}", layout.getId(), layout);
        Set<Layout> layouts = new HashSet<>();
        layouts.add(layout);
        eventHall1.setLayouts(layouts);
        performanceDto.setLayoutId(layout.getId());
        performerRepository.save(artistTest);
        Event event = eventRepository.saveAndFlush(eventTest2);
        performanceDto.setEventId(event.getId());
        Performance newPerformance = performanceRepository.saveAndFlush(performanceMapper.performanceDtoToPerformance(performanceDto));
        newPerformance.setLayout(layout);
        newPerformance.setEvent(event);
        performanceRepository.saveAndFlush(newPerformance);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(PERFORMANCE_BASE_URI + "/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("performanceName", "Te")
                .param("minPrice", "20")
                .param("maxPrice", "55")
                .param("time", performanceStartTime.toString())
                .param("eventName", "eve")
                .param("hallName", "hal")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER_USERNAME, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        List<PerformanceSearchResultDto> results = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(results.size()).isEqualTo(1);
        PerformanceSearchResultDto result = results.get(0);
        assertThat(result.getId()).isEqualTo(newPerformance.getId());
        assertThat(result.getPerformanceName()).isEqualTo(newPerformance.getPerformanceName());
        assertThat(result.getEventName()).isEqualTo(newPerformance.getEvent().getName());
        assertThat(result.getStartTime()).isEqualTo(newPerformance.getStartTime());
        assertThat(result.getEndTime()).isEqualTo(newPerformance.getEndTime());
        assertThat(result.getEventType()).isEqualTo(newPerformance.getEvent().getEventType());
    }

    @Test
    public void givenUser_WhenSearchingForNonExistingMinPrice_ThenSearchReturnsEmptyResult() throws Exception{
        Location location = locationRepository.save(location0);
        LOGGER.info("location id is {}", location.getId());
        performanceDto.setLocationId(location.getId());
        EventHall eventHall1 = eventHallRepository.save(eventhall);
        performanceDto.setEventhallId(eventHall1.getId());
        Layout layout = layoutRepository.saveAndFlush(layout0);
        Seating seating = new Seating();
        seating.setPrice(50);
        seating.setLayout(layout);
        seating.setSectorId("1");
        Sector sector = sectorRepository.saveAndFlush(seating);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector);
        layout.setSectors(sectors);
        layout = layoutRepository.saveAndFlush(layout);
        LOGGER.info("layout after save: {} {}", layout.getId(), layout);
        Set<Layout> layouts = new HashSet<>();
        layouts.add(layout);
        eventHall1.setLayouts(layouts);
        performanceDto.setLayoutId(layout.getId());
        performerRepository.save(artistTest);
        Event event = eventRepository.saveAndFlush(eventTest2);
        performanceDto.setEventId(event.getId());
        Performance newPerformance = performanceRepository.saveAndFlush(performanceMapper.performanceDtoToPerformance(performanceDto));
        newPerformance.setLayout(layout);
        newPerformance.setEvent(event);
        performanceRepository.saveAndFlush(newPerformance);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(PERFORMANCE_BASE_URI + "/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("performanceName", "Te")
                .param("minPrice", "60")
                .param("time", performanceStartTime.toString())
                .param("eventName", "eve")
                .param("hallName", "hal")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER_USERNAME, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        List<PerformanceSearchResultDto> results = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(results.size()).isEqualTo(0);
    }

    @Test
    public void givenUser_WhenSearchingForNonExistingMaxPrice_ThenSearchReturnsEmptyResult() throws Exception{
        Location location = locationRepository.save(location0);
        LOGGER.info("location id is {}", location.getId());
        performanceDto.setLocationId(location.getId());
        EventHall eventHall1 = eventHallRepository.save(eventhall);
        performanceDto.setEventhallId(eventHall1.getId());
        Layout layout = layoutRepository.saveAndFlush(layout0);
        Seating seating = new Seating();
        seating.setPrice(50);
        seating.setLayout(layout);
        seating.setSectorId("1");
        Sector sector = sectorRepository.saveAndFlush(seating);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector);
        layout.setSectors(sectors);
        layout = layoutRepository.saveAndFlush(layout);
        LOGGER.info("layout after save: {} {}", layout.getId(), layout);
        Set<Layout> layouts = new HashSet<>();
        layouts.add(layout);
        eventHall1.setLayouts(layouts);
        performanceDto.setLayoutId(layout.getId());
        performerRepository.save(artistTest);
        Event event = eventRepository.saveAndFlush(eventTest2);
        performanceDto.setEventId(event.getId());
        Performance newPerformance = performanceRepository.saveAndFlush(performanceMapper.performanceDtoToPerformance(performanceDto));
        newPerformance.setLayout(layout);
        newPerformance.setEvent(event);
        performanceRepository.saveAndFlush(newPerformance);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(PERFORMANCE_BASE_URI + "/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("performanceName", "Te")
                .param("minPrice", "20")
                .param("maxPrice", "30")
                .param("time", performanceStartTime.toString())
                .param("eventName", "eve")
                .param("hallName", "hal")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER_USERNAME, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        List<PerformanceSearchResultDto> results = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(results.size()).isEqualTo(0);
    }

    @Test
    public void givenUser_WhenSearchingForNonExistingPerformanceName_ThenSearchReturnsEmptyResult() throws Exception{
        Location location = locationRepository.save(location0);
        LOGGER.info("location id is {}", location.getId());
        performanceDto.setLocationId(location.getId());
        EventHall eventHall1 = eventHallRepository.save(eventhall);
        performanceDto.setEventhallId(eventHall1.getId());
        Layout layout = layoutRepository.saveAndFlush(layout0);
        Seating seating = new Seating();
        seating.setPrice(50);
        seating.setLayout(layout);
        seating.setSectorId("1");
        Sector sector = sectorRepository.saveAndFlush(seating);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector);
        layout.setSectors(sectors);
        layout = layoutRepository.saveAndFlush(layout);
        LOGGER.info("layout after save: {} {}", layout.getId(), layout);
        Set<Layout> layouts = new HashSet<>();
        layouts.add(layout);
        eventHall1.setLayouts(layouts);
        performanceDto.setLayoutId(layout.getId());
        performerRepository.save(artistTest);
        Event event = eventRepository.saveAndFlush(eventTest2);
        performanceDto.setEventId(event.getId());
        Performance newPerformance = performanceRepository.saveAndFlush(performanceMapper.performanceDtoToPerformance(performanceDto));
        newPerformance.setLayout(layout);
        newPerformance.setEvent(event);
        performanceRepository.saveAndFlush(newPerformance);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(PERFORMANCE_BASE_URI + "/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("performanceName", "Teee")
                .param("minPrice", "20")
                .param("maxPrice", "60")
                .param("time", performanceStartTime.toString())
                .param("eventName", "eve")
                .param("hallName", "hal")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER_USERNAME, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        List<PerformanceSearchResultDto> results = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(results.size()).isEqualTo(0);
    }

    @Test
    public void givenUser_WhenSearchingForNonExistingTime_ThenSearchReturnsEmptyResult() throws Exception{
        Location location = locationRepository.save(location0);
        LOGGER.info("location id is {}", location.getId());
        performanceDto.setLocationId(location.getId());
        EventHall eventHall1 = eventHallRepository.save(eventhall);
        performanceDto.setEventhallId(eventHall1.getId());
        Layout layout = layoutRepository.saveAndFlush(layout0);
        Seating seating = new Seating();
        seating.setPrice(50);
        seating.setLayout(layout);
        seating.setSectorId("1");
        Sector sector = sectorRepository.saveAndFlush(seating);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector);
        layout.setSectors(sectors);
        layout = layoutRepository.saveAndFlush(layout);
        LOGGER.info("layout after save: {} {}", layout.getId(), layout);
        Set<Layout> layouts = new HashSet<>();
        layouts.add(layout);
        eventHall1.setLayouts(layouts);
        performanceDto.setLayoutId(layout.getId());
        performerRepository.save(artistTest);
        Event event = eventRepository.saveAndFlush(eventTest2);
        performanceDto.setEventId(event.getId());
        Performance newPerformance = performanceRepository.saveAndFlush(performanceMapper.performanceDtoToPerformance(performanceDto));
        newPerformance.setLayout(layout);
        newPerformance.setEvent(event);
        performanceRepository.saveAndFlush(newPerformance);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(PERFORMANCE_BASE_URI + "/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("performanceName", "Te")
                .param("minPrice", "20")
                .param("maxPrice", "60")
                .param("time", performanceStartTime.plusMinutes(151).toString())
                .param("eventName", "eve")
                .param("hallName", "hal")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER_USERNAME, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        List<PerformanceSearchResultDto> results = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(results.size()).isEqualTo(0);
    }

    @Test
    public void givenUser_WhenSearchingForNonExistingTime2_ThenSearchReturnsEmptyResult() throws Exception{
        Location location = locationRepository.save(location0);
        LOGGER.info("location id is {}", location.getId());
        performanceDto.setLocationId(location.getId());
        EventHall eventHall1 = eventHallRepository.save(eventhall);
        performanceDto.setEventhallId(eventHall1.getId());
        Layout layout = layoutRepository.saveAndFlush(layout0);
        Seating seating = new Seating();
        seating.setPrice(50);
        seating.setLayout(layout);
        seating.setSectorId("1");
        Sector sector = sectorRepository.saveAndFlush(seating);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector);
        layout.setSectors(sectors);
        layout = layoutRepository.saveAndFlush(layout);
        LOGGER.info("layout after save: {} {}", layout.getId(), layout);
        Set<Layout> layouts = new HashSet<>();
        layouts.add(layout);
        eventHall1.setLayouts(layouts);
        performanceDto.setLayoutId(layout.getId());
        performerRepository.save(artistTest);
        Event event = eventRepository.saveAndFlush(eventTest2);
        performanceDto.setEventId(event.getId());
        Performance newPerformance = performanceRepository.saveAndFlush(performanceMapper.performanceDtoToPerformance(performanceDto));
        newPerformance.setLayout(layout);
        newPerformance.setEvent(event);
        performanceRepository.saveAndFlush(newPerformance);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(PERFORMANCE_BASE_URI + "/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("performanceName", "Te")
                .param("minPrice", "20")
                .param("maxPrice", "60")
                .param("time", performanceStartTime.minusMinutes(31).toString())
                .param("eventName", "eve")
                .param("hallName", "hal")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER_USERNAME, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        List<PerformanceSearchResultDto> results = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(results.size()).isEqualTo(0);
    }

    @Test
    public void givenUser_WhenSearchingForNonExistingEventName_ThenSearchReturnsEmptyResult() throws Exception{
        Location location = locationRepository.save(location0);
        LOGGER.info("location id is {}", location.getId());
        performanceDto.setLocationId(location.getId());
        EventHall eventHall1 = eventHallRepository.save(eventhall);
        performanceDto.setEventhallId(eventHall1.getId());
        Layout layout = layoutRepository.saveAndFlush(layout0);
        Seating seating = new Seating();
        seating.setPrice(50);
        seating.setLayout(layout);
        seating.setSectorId("1");
        Sector sector = sectorRepository.saveAndFlush(seating);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector);
        layout.setSectors(sectors);
        layout = layoutRepository.saveAndFlush(layout);
        LOGGER.info("layout after save: {} {}", layout.getId(), layout);
        Set<Layout> layouts = new HashSet<>();
        layouts.add(layout);
        eventHall1.setLayouts(layouts);
        performanceDto.setLayoutId(layout.getId());
        performerRepository.save(artistTest);
        Event event = eventRepository.saveAndFlush(eventTest2);
        performanceDto.setEventId(event.getId());
        Performance newPerformance = performanceRepository.saveAndFlush(performanceMapper.performanceDtoToPerformance(performanceDto));
        newPerformance.setLayout(layout);
        newPerformance.setEvent(event);
        performanceRepository.saveAndFlush(newPerformance);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(PERFORMANCE_BASE_URI + "/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("performanceName", "Te")
                .param("minPrice", "20")
                .param("maxPrice", "60")
                .param("time", performanceStartTime.toString())
                .param("eventName", "evee")
                .param("hallName", "hal")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER_USERNAME, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        List<PerformanceSearchResultDto> results = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(results.size()).isEqualTo(0);
    }

    @Test
    public void givenUser_WhenSearchingForNonExistingHallName_ThenSearchReturnsEmptyResult() throws Exception{
        Location location = locationRepository.save(location0);
        LOGGER.info("location id is {}", location.getId());
        performanceDto.setLocationId(location.getId());
        EventHall eventHall1 = eventHallRepository.save(eventhall);
        performanceDto.setEventhallId(eventHall1.getId());
        Layout layout = layoutRepository.saveAndFlush(layout0);
        Seating seating = new Seating();
        seating.setPrice(50);
        seating.setLayout(layout);
        seating.setSectorId("1");
        Sector sector = sectorRepository.saveAndFlush(seating);
        Set<Sector> sectors = new HashSet<>();
        sectors.add(sector);
        layout.setSectors(sectors);
        layout = layoutRepository.saveAndFlush(layout);
        LOGGER.info("layout after save: {} {}", layout.getId(), layout);
        Set<Layout> layouts = new HashSet<>();
        layouts.add(layout);
        eventHall1.setLayouts(layouts);
        performanceDto.setLayoutId(layout.getId());
        performerRepository.save(artistTest);
        Event event = eventRepository.saveAndFlush(eventTest2);
        performanceDto.setEventId(event.getId());
        Performance newPerformance = performanceRepository.saveAndFlush(performanceMapper.performanceDtoToPerformance(performanceDto));
        newPerformance.setLayout(layout);
        newPerformance.setEvent(event);
        performanceRepository.saveAndFlush(newPerformance);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(PERFORMANCE_BASE_URI + "/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("performanceName", "Te")
                .param("minPrice", "20")
                .param("maxPrice", "60")
                .param("time", performanceStartTime.toString())
                .param("eventName", "eve")
                .param("hallName", "haaaaal")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(DEFAULT_USER_USERNAME, USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        List<PerformanceSearchResultDto> results = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(results.size()).isEqualTo(0);
    }





}
