package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateEventHallDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateLayoutDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateSeatDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LayoutOverviewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.RowCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SectorCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventHallRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LayoutRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RowRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SeatRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StandRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class LayoutEndpointTest implements TestData {

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
    private LayoutRepository layoutRepository;

    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private RowRepository rowRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private StandRepository standRepository;

    @Autowired
    private LocationMapper locationMapper;

    private Location location = Location.builder()
        .name("Test the location number 1")
        .country("Austria")
        .city("Vienna")
        .street("Teststraße")
        .zipCode("1120")
        .build();

    @BeforeEach
    public void beforeEach() {
        this.standRepository.deleteAll();
        this.seatRepository.deleteAll();
        this.rowRepository.deleteAll();
        this.sectorRepository.deleteAll();
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

    @AfterEach
    public void afterEach() {
        this.standRepository.deleteAll();
        this.seatRepository.deleteAll();
        this.rowRepository.deleteAll();
        this.sectorRepository.deleteAll();
        this.layoutRepository.deleteAll();
        this.eventHallRepository.deleteAll();
        this.locationRepository.deleteAll();


    }

    @Test
    public void givenLayout_whenGetAll_thenListWithLayouts() throws Exception {
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

        MvcResult mvcResult2 = this.mockMvc.perform(get(LAYOUTS_BASE_URI)
                .header(this.securityProperties.getAuthHeader(), this.jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response2 = mvcResult2.getResponse();

        assertEquals(HttpStatus.OK.value(), response2.getStatus());

        List<LayoutOverviewDto> layoutOverviewDtos = Arrays.asList(this.objectMapper.readValue(response2.getContentAsString(),
            LayoutOverviewDto[].class));

        assertThat(layoutOverviewDtos.size()).isEqualTo(1);
    }
}
