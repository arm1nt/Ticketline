package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.LayoutDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LayoutDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LayoutMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Layout;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.EventHallService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class EventsEndpointTest implements TestData {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTokenizer jwtTokenizer;
    @Autowired
    private SecurityProperties securityProperties;
    // Mapper
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LayoutMapper layoutMapper;
    // Repositories
    @Autowired
    private LayoutRepository layoutRepository;
    @Autowired
    private EventHallRepository eventHallRepository;
    @Autowired
    private SectorRepository sectorRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private StandRepository standRepository;
    @Autowired
    private RowRepository rowRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private TicketRepository ticketRepository;
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


    @BeforeEach
    public void beforeEach() {
        LayoutDataGenerator layoutDataGenerator = new LayoutDataGenerator(layoutRepository, eventHallRepository,
            sectorRepository, standRepository, rowRepository, seatRepository, ticketRepository, geometryRepository,
            orderRepository, performanceRepository, locationRepository, objectMapper, eventHallService);
        layoutDataGenerator.generateCustomLayout();
    }
    @Test
    public void givenLayout_whenNonExistingId_then404() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(LAYOUTS_BASE_URI + "/{id}", -1)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
    @Test
    public void onlyOneLayoutIsSavedWithGenerator() throws Exception {
        assertEquals(1,layoutRepository.findAll().size());
    }
    @Test
    public void givenLayout_whenIdExists_thenLayoutWithAllProperties() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(LAYOUTS_BASE_URI + "/{layoutId}", layoutRepository.findAll().get(0).getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );

        LayoutDto layoutDto = objectMapper.readValue(response.getContentAsString(),
            LayoutDto.class);
        LayoutDto fetchedLayout = layoutMapper.layoutToLayoutDto(layoutRepository.findAll().get(0));
        assertEquals(layoutDto, fetchedLayout);
    }
}
