package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventHallDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateLayoutDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateSeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventHallDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventHallOverviewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LayoutDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RowCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SectorCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventHallMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LayoutMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.EventHall;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.RectangleGeometry;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventHallRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GeometryRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LayoutRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.EventHallService;
import at.ac.tuwien.sepm.groupphase.backend.service.LayoutService;
import at.ac.tuwien.sepm.groupphase.backend.service.LocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class EventHallEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private EventHallRepository eventHallRepository;

    @Autowired
    private GeometryRepository geometryRepository;

    @Autowired
    private LayoutRepository layoutRepository;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private EventHallMapper eventHallMapper;

    @Autowired
    private LayoutMapper layoutMapper;

    @Autowired
    private LocationService locationService;

    @Autowired
    private EventHallService eventHallService;

    @Autowired
    private LayoutService layoutService;

    private Location location = Location.builder()
        .name("Test the location number 1")
        .country("Austria")
        .city("Vienna")
        .street("Teststraße")
        .zipCode("1120")
        .build();

    @BeforeEach
    public void beforeEach() {
        //delete repos
        this.layoutRepository.deleteAll();
        this.eventHallRepository.deleteAll();
        this.locationRepository.deleteAll();


        this.location = Location.builder()
            .name("Test the location number 1")
            .country("Austria")
            .city("Vienna")
            .street("Teststreet")
            .zipCode("1120")
            .build();
    }

    @Test
    public void givenTwoEventHalls_whenGetAll_thenListOfEventHalls() throws Exception {

        EventHall eventHall = EventHall.builder()
            .name("Eventhall1")
            .location(this.location)
            .build();

        EventHall eventHall2 = EventHall.builder()
            .name("Eventhall2")
            .location(this.location)
            .build();


        this.locationRepository.save(this.location);
        this.eventHallRepository.save(eventHall);
        this.eventHallRepository.save(eventHall2);

        MvcResult mvcResult = this.mockMvc.perform(get(EVENTHALL_BASE_URI)
            .header(this.securityProperties.getAuthHeader(), this.jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        List<EventHallOverviewDto> eventhalls = Arrays.asList(this.objectMapper.readValue(response.getContentAsString(),
            EventHallOverviewDto[].class));

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertAll(
            () -> assertThat(eventhalls.size()).isEqualTo(2),
            () -> assertEquals(eventhalls.get(0).getName(), eventHall.getName()),
            () -> assertEquals(eventhalls.get(1).getName(), eventHall2.getName())
        );

    }
    @Test
    public void givenTwoEventHalls_whenGetByName_thenEventHall() throws Exception {

        RectangleGeometry rectangleGeometry = RectangleGeometry.rectangleBuilder()
            .y(2)
            .x(1)
            .width(200)
            .height(200)
            .build();

        EventHall eventHall = EventHall.builder()
            .name("Eventhall1")
            .location(this.location)
            .geometry(rectangleGeometry)
            .build();

        EventHall eventHall2 = EventHall.builder()
            .name("Eventhall2")
            .location(this.location)
            .geometry(rectangleGeometry)
            .build();

        this.geometryRepository.save(rectangleGeometry);
        this.locationRepository.save(this.location);
        this.eventHallRepository.save(eventHall);
        this.eventHallRepository.save(eventHall2);

        MvcResult mvcResult = this.mockMvc.perform(get(EVENTHALL_BASE_URI + "?hallname=" + eventHall.getName())
                .header(this.securityProperties.getAuthHeader(), this.jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        List<EventHallOverviewDto> eventhalls = Arrays.asList(this.objectMapper.readValue(response.getContentAsString(),
            EventHallOverviewDto[].class));

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertAll(
            () -> assertThat(eventhalls.size()).isEqualTo(1),
            () -> assertEquals(eventhalls.get(0).getName(), eventHall.getName())
        );
    }

    @Test
    public void givenTwoEventHalls_whenGetByValidId_thenEventHall() throws Exception {

        RectangleGeometry rectangleGeometry = RectangleGeometry.rectangleBuilder()
            .y(2)
            .x(1)
            .width(200)
            .height(200)
            .build();

        EventHall eventHall = EventHall.builder()
            .name("Eventhall1")
            .location(this.location)
            .geometry(rectangleGeometry)
            .build();

        EventHall eventHall2 = EventHall.builder()
            .name("Eventhall2")
            .location(this.location)
            .geometry(rectangleGeometry)
            .build();


        this.geometryRepository.save(rectangleGeometry);
        this.locationRepository.save(this.location);
        long id = this.eventHallRepository.save(eventHall).getId();
        this.eventHallRepository.save(eventHall2);

        MvcResult mvcResult = this.mockMvc.perform(get(EVENTHALL_BASE_URI + "/" + id)
                .header(this.securityProperties.getAuthHeader(), this.jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        EventHallDto eventhalls = this.objectMapper.readValue(response.getContentAsString(),
            EventHallDto.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertAll(
            () -> assertEquals(eventhalls.getName(), eventHall.getName())
        );
    }

    @Test
    public void givenTwoEventHalls_whenGetByInvalidId_thenEventHall() throws Exception {

        RectangleGeometry rectangleGeometry = RectangleGeometry.rectangleBuilder()
            .y(2)
            .x(1)
            .width(200)
            .height(200)
            .build();

        EventHall eventHall = EventHall.builder()
            .name("Eventhall1")
            .location(this.location)
            .geometry(rectangleGeometry)
            .build();

        EventHall eventHall2 = EventHall.builder()
            .name("Eventhall2")
            .location(this.location)
            .geometry(rectangleGeometry)
            .build();


        this.geometryRepository.save(rectangleGeometry);
        this.locationRepository.save(this.location);
        this.eventHallRepository.save(eventHall);
        this.eventHallRepository.save(eventHall2);

        MvcResult mvcResult = this.mockMvc.perform(get(EVENTHALL_BASE_URI + "/-1")
                .header(this.securityProperties.getAuthHeader(), this.jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();


        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void givenNothing_whenCreatingEventhall_getPersistedEventhall() throws Exception {
        this.locationRepository.save(this.location);

        CreateEventHallDto createEventHallDto = CreateEventHallDto.builder()
            .name("MyCustomEventhall")
            .x(10)
            .y(20)
            .width(200)
            .height(200)
            .location(this.locationMapper.locationToLocationDto(this.location))
            .layout(CreateLayoutDto.builder()
                .name("The layout ")
                .sectors(new SectorCreateDto[]{
                    SectorCreateDto.builder()
                        .id(1)
                        .type("Standing")
                        .price(20)
                        .color("Red")
                        .capacity(200)
                        .x(20)
                        .y(20)
                        .width(200)
                        .height(200)
                        .build(),
                    SectorCreateDto.builder()
                        .id(2)
                        .type("Seating")
                        .price(44)
                        .color("Blue")
                        .capacity(-1)
                        .x(20)
                        .y(20)
                        .width(200)
                        .height(200)
                        .rows(new RowCreateDto[]{
                            RowCreateDto.builder()
                                .number(1)
                                .x(50)
                                .y(50)
                                .seats(new CreateSeatDto[]{
                                    CreateSeatDto.builder()
                                        .seatId("1,2")
                                        .rowNumber(1)
                                        .x(20)
                                        .y(20)
                                        .price(25)
                                        .polygonPoints("pooints")
                                        .sectorId(2)
                                        .chosen(true)
                                        .build()
                            })
                                .build()
                    })
                        .build()
                })
                .build())
            .build();

        String requestBody = this.objectMapper.writeValueAsString(createEventHallDto);

        MvcResult mvcResult = this.mockMvc.perform(post(EVENTHALL_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .header(this.securityProperties.getAuthHeader(), this.jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        EventHallOverviewDto eventHallOverviewDto = this.objectMapper.readValue(response.getContentAsString(),
            EventHallOverviewDto.class);

        assertEquals(eventHallOverviewDto.getName(), createEventHallDto.getName());

    }

    @Test
    public void givenEventHall_whenAddingLayoutToEventHall_thenEventHall() throws Exception {
        RectangleGeometry rectangleGeometry = RectangleGeometry.rectangleBuilder()
            .y(2)
            .x(1)
            .width(200)
            .height(200)
            .build();

        EventHall eventHall = EventHall.builder()
            .name("Eventhall1")
            .location(this.location)
            .geometry(rectangleGeometry)
            .build();

        this.geometryRepository.save(rectangleGeometry);
        this.locationRepository.save(this.location);
        long id = this.eventHallRepository.save(eventHall).getId();

        CreateEventHallDto createEventHallDto = CreateEventHallDto.builder()
            .name("MyCustomEventhall")
            .x(10)
            .y(20)
            .width(200)
            .height(200)
            .location(this.locationMapper.locationToLocationDto(this.location))
            .layout(CreateLayoutDto.builder()
                .name("The layout ")
                .sectors(new SectorCreateDto[]{
                    SectorCreateDto.builder()
                        .id(1)
                        .type("Standing")
                        .price(20)
                        .color("Red")
                        .capacity(200)
                        .x(20)
                        .y(20)
                        .width(200)
                        .height(200)
                        .build(),
                    SectorCreateDto.builder()
                        .id(2)
                        .type("Seating")
                        .price(44)
                        .color("Blue")
                        .capacity(-1)
                        .x(20)
                        .y(20)
                        .width(200)
                        .height(200)
                        .rows(new RowCreateDto[]{
                            RowCreateDto.builder()
                                .number(1)
                                .x(50)
                                .y(50)
                                .seats(new CreateSeatDto[]{
                                    CreateSeatDto.builder()
                                        .seatId("1,2")
                                        .rowNumber(1)
                                        .x(20)
                                        .y(20)
                                        .price(25)
                                        .polygonPoints("pooints")
                                        .sectorId(2)
                                        .chosen(true)
                                        .build()
                                })
                                .build()
                        })
                        .build()
                })
                .build())
            .build();

        String requestBody = this.objectMapper.writeValueAsString(createEventHallDto);

        MvcResult mvcResult = this.mockMvc.perform(patch(EVENTHALL_BASE_URI + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(this.securityProperties.getAuthHeader(), this.jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        EventHallOverviewDto eventHallOverviewDto = this.objectMapper.readValue(response.getContentAsString(),
            EventHallOverviewDto.class);

        assertEquals(eventHallOverviewDto.getName(), eventHall.getName());
    }

    //TODO: get all layouts from eventhall
    @Test
    public void givenEventHallWithLayout_getAllLayoutsOfEventHall_thenGetListOfLayouts() throws Exception {
        this.locationRepository.save(this.location);

        CreateEventHallDto createEventHallDto = CreateEventHallDto.builder()
            .name("MyCustomEventhall")
            .x(10)
            .y(20)
            .width(200)
            .height(200)
            .location(this.locationMapper.locationToLocationDto(this.location))
            .layout(CreateLayoutDto.builder()
                .name("The layout ")
                .sectors(new SectorCreateDto[]{
                    SectorCreateDto.builder()
                        .id(1)
                        .type("Standing")
                        .price(20)
                        .color("Red")
                        .capacity(200)
                        .x(20)
                        .y(20)
                        .width(200)
                        .height(200)
                        .build(),
                    SectorCreateDto.builder()
                        .id(2)
                        .type("Seating")
                        .price(44)
                        .color("Blue")
                        .capacity(-1)
                        .x(20)
                        .y(20)
                        .width(200)
                        .height(200)
                        .rows(new RowCreateDto[]{
                            RowCreateDto.builder()
                                .number(1)
                                .x(50)
                                .y(50)
                                .seats(new CreateSeatDto[]{
                                    CreateSeatDto.builder()
                                        .seatId("1,2")
                                        .rowNumber(1)
                                        .x(20)
                                        .y(20)
                                        .price(25)
                                        .polygonPoints("pooints")
                                        .sectorId(2)
                                        .chosen(true)
                                        .build()
                                })
                                .build()
                        })
                        .build()
                })
                .build())
            .build();

        String requestBody = this.objectMapper.writeValueAsString(createEventHallDto);

        MvcResult mvcResult = this.mockMvc.perform(post(EVENTHALL_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(this.securityProperties.getAuthHeader(), this.jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        EventHallOverviewDto eventHallOverviewDto = this.objectMapper.readValue(response.getContentAsString(),
            EventHallOverviewDto.class);


        MvcResult mvcResult2 = this.mockMvc.perform(get(EVENTHALL_BASE_URI + "/" + eventHallOverviewDto.getId()
            + "/layouts")
                .header(this.securityProperties.getAuthHeader(), this.jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response2 = mvcResult2.getResponse();

        List<LayoutDto> layoutDtos = Arrays.asList(this.objectMapper.readValue(response2.getContentAsString(),
            LayoutDto[].class));

        assertEquals(HttpStatus.OK.value(), response2.getStatus());
        assertThat(layoutDtos.size()).isEqualTo(1);
    }

    @Test
    public void givenNothing_addLayoutToNonExistingEventHall_then404() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(get(EVENTHALL_BASE_URI + "/-1" + "/layouts")
                .header(this.securityProperties.getAuthHeader(), this.jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
}
