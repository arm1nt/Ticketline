package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.EventMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.PerformerMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.type.EventType;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class EventEndpointTest implements TestData {
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventMapper eventMapper;
    @Autowired
    private PerformerRepository performerRepository;
    @Autowired
    LocationRepository locationRepository;
    @Autowired
    EventHallRepository eventHallRepository;
    @Autowired
    LayoutRepository layoutRepository;
    @Autowired
    PerformanceRepository performanceRepository;
    @Autowired
    private PerformerMapper performerMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private JwtTokenizer jwtTokenizer;
    @Autowired
    private ObjectMapper objectMapper;

    Artist artist1 = Artist.builder()
        .performerName(TEST_ARTIST_PERFORMERNAME)
        .firstName(TEST_ARTIST_FIRSTNAME)
        .lastName(TEST_ARTIST_LASTNAME)
        .build();

    Location location = Location.builder()
        .name("Test Location")
        .city("City")
        .country("Country")
        .street("Street")
        .zipCode("1111")
        .build();
    EventHall eventhall = EventHall.builder().name("Test Eventhall").location(location).build();
    Layout layout = Layout.builder().name("Test").eventHall(eventhall).sectors(new HashSet<>()).build();



    Event event = Event.builder()
        .name("Test")
        .eventType(EventType.valueOf("OPERA"))
        .performers(Collections.singletonList(artist1))
        .build();

    Event event2 = Event.builder()
        .name("Event2")
        .eventType(EventType.valueOf("CONCERT"))
        .performers(Collections.singletonList(artist1))
        .build();

    Event event3 = Event.builder()
        .name("Event3")
        .eventType(EventType.valueOf("OPERA"))
        .performers(Collections.singletonList(artist1))
        .build();

    Event event4 = Event.builder()
        .name("Event4")
        .eventType(EventType.valueOf("OPERA"))
        .performers(Collections.singletonList(artist1))
        .build();

    Event event_1 = Event.builder()
        .name(TestData.TEST_EVENT1_NAME)
        .eventType(TestData.TEST_EVENT1_TYPE)
        .duration(TestData.TEST_EVENT1_DURATION)
        .performers(Collections.singletonList(artist1))
        .build();
    Event event_2 = Event.builder()
        .name(TestData.TEST_EVENT2_NAME)
        .eventType(TestData.TEST_EVENT2_TYPE)
        .duration(TestData.TEST_EVENT2_DURATION)
        .performers(Collections.singletonList(artist1))
        .build();
    Event event_3 = Event.builder()
        .name(TestData.TEST_EVENT3_NAME)
        .eventType(TestData.TEST_EVENT3_TYPE)
        .duration(TestData.TEST_EVENT3_DURATION)
        .performers(Collections.singletonList(artist1))
        .build();
    Event event_4 = Event.builder()
        .name(TestData.TEST_EVENT4_NAME)
        .eventType(TestData.TEST_EVENT4_TYPE)
        .duration(TestData.TEST_EVENT4_DURATION)
        .performers(Collections.singletonList(artist1))
        .build();
    Event event_5 = Event.builder()
        .name(TestData.TEST_EVENT5_NAME)
        .eventType(TestData.TEST_EVENT5_TYPE)
        .duration(TestData.TEST_EVENT5_DURATION)
        .performers(Collections.singletonList(artist1))
        .build();
    Event event_6 = Event.builder()
        .name(TestData.TEST_EVENT6_NAME)
        .eventType(TestData.TEST_EVENT6_TYPE)
        .duration(TestData.TEST_EVENT6_DURATION)
        .performers(Collections.singletonList(artist1))
        .build();
    Event event_7 = Event.builder()
        .name(TestData.TEST_EVENT7_NAME)
        .eventType(TestData.TEST_EVENT7_TYPE)
        .duration(TestData.TEST_EVENT7_DURATION)
        .performers(Collections.singletonList(artist1))
        .build();
    Event event_8 = Event.builder()
        .name(TestData.TEST_EVENT8_NAME)
        .eventType(TestData.TEST_EVENT8_TYPE)
        .duration(TestData.TEST_EVENT8_DURATION)
        .performers(Collections.singletonList(artist1))
        .build();
    Event event_9 = Event.builder()
        .name(TestData.TEST_EVENT9_NAME)
        .eventType(TestData.TEST_EVENT9_TYPE)
        .duration(TestData.TEST_EVENT9_DURATION)
        .performers(Collections.singletonList(artist1))
        .build();
    Event event_10 = Event.builder()
        .name(TestData.TEST_EVENT10_NAME)
        .eventType(TestData.TEST_EVENT10_TYPE)
        .duration(TestData.TEST_EVENT10_DURATION)
        .performers(Collections.singletonList(artist1))
        .build();

    Performance performance1 = Performance.builder()
        .startTime(TestData.DEFAULT_PERFORMANCE_TOP10_START_TIME)
        .performanceName(TestData.TEST_PERFORMANCE1_NAME)
        .endTime(TestData.DEFAULT_PERFORMANCE_TOP10_START_TIME.plusMinutes(TestData.TEST_EVENT1_DURATION))
        .layout(layout)
        .soldTickets(TEST_PERFORMANCE1_SOLDTICKETS)
        .event(event_1)
        .build();
    Performance performance2 = Performance.builder()
        .startTime(TestData.DEFAULT_PERFORMANCE_TOP10_START_TIME)
        .performanceName(TestData.TEST_PERFORMANCE2_NAME)
        .endTime(TestData.DEFAULT_PERFORMANCE_TOP10_START_TIME.plusMinutes(TestData.TEST_EVENT2_DURATION))
        .layout(layout)
        .soldTickets(TEST_PERFORMANCE2_SOLDTICKETS)
        .event(event_2)
        .build();
    Performance performance3 = Performance.builder()
        .startTime(TestData.DEFAULT_PERFORMANCE_TOP10_START_TIME)
        .performanceName(TestData.TEST_PERFORMANCE3_NAME)
        .endTime(TestData.DEFAULT_PERFORMANCE_TOP10_START_TIME.plusMinutes(TestData.TEST_EVENT3_DURATION))
        .layout(layout)
        .soldTickets(TEST_PERFORMANCE3_SOLDTICKETS)
        .event(event_3)
        .build();
    Performance performance4 = Performance.builder()
        .startTime(TestData.DEFAULT_PERFORMANCE_TOP10_START_TIME)
        .performanceName(TestData.TEST_PERFORMANCE4_NAME)
        .endTime(TestData.DEFAULT_PERFORMANCE_TOP10_START_TIME.plusMinutes(TestData.TEST_EVENT4_DURATION))
        .layout(layout)
        .soldTickets(TEST_PERFORMANCE4_SOLDTICKETS)
        .event(event_4)
        .build();
    Performance performance5 = Performance.builder()
        .startTime(TestData.DEFAULT_PERFORMANCE_TOP10_START_TIME)
        .performanceName(TestData.TEST_PERFORMANCE5_NAME)
        .endTime(TestData.DEFAULT_PERFORMANCE1_START_TIME.plusMinutes(TestData.TEST_EVENT5_DURATION))
        .layout(layout)
        .soldTickets(TEST_PERFORMANCE5_SOLDTICKETS)
        .event(event_5)
        .build();
    Performance performance6 = Performance.builder()
        .startTime(TestData.DEFAULT_PERFORMANCE_TOP10_START_TIME)
        .performanceName(TestData.TEST_PERFORMANCE6_NAME)
        .endTime(TestData.DEFAULT_PERFORMANCE_TOP10_START_TIME.plusMinutes(TestData.TEST_EVENT6_DURATION))
        .layout(layout)
        .soldTickets(TEST_PERFORMANCE6_SOLDTICKETS)
        .event(event_6)
        .build();
    Performance performance7 = Performance.builder()
        .startTime(TestData.DEFAULT_PERFORMANCE_TOP10_START_TIME)
        .performanceName(TestData.TEST_PERFORMANCE7_NAME)
        .endTime(TestData.DEFAULT_PERFORMANCE_TOP10_START_TIME.plusMinutes(TestData.TEST_EVENT7_DURATION))
        .layout(layout)
        .soldTickets(TEST_PERFORMANCE7_SOLDTICKETS)
        .event(event_7)
        .build();
    Performance performance8 = Performance.builder()
        .startTime(TestData.DEFAULT_PERFORMANCE_TOP10_START_TIME)
        .performanceName(TestData.TEST_PERFORMANCE8_NAME)
        .endTime(TestData.DEFAULT_PERFORMANCE_TOP10_START_TIME.plusMinutes(TestData.TEST_EVENT8_DURATION))
        .layout(layout)
        .soldTickets(TEST_PERFORMANCE8_SOLDTICKETS)
        .event(event_8)
        .build();
    Performance performance9 = Performance.builder()
        .startTime(TestData.DEFAULT_PERFORMANCE_TOP10_START_TIME)
        .performanceName(TestData.TEST_PERFORMANCE9_NAME)
        .endTime(TestData.DEFAULT_PERFORMANCE_TOP10_START_TIME.plusMinutes(TestData.TEST_EVENT9_DURATION))
        .layout(layout)
        .soldTickets(TEST_PERFORMANCE9_SOLDTICKETS)
        .event(event_9)
        .build();
    Performance performance10 = Performance.builder()
        .startTime(TestData.DEFAULT_PERFORMANCE_TOP10_START_TIME)
        .performanceName(TestData.TEST_PERFORMANCE10_NAME)
        .endTime(TestData.DEFAULT_PERFORMANCE_TOP10_START_TIME.plusMinutes(TestData.TEST_EVENT10_DURATION))
        .layout(layout)
        .soldTickets(TEST_PERFORMANCE10_SOLDTICKETS)
        .event(event_10)
        .build();


    @BeforeEach
    public void beforeEach() {
        eventRepository.deleteAll();
        performerRepository.deleteAll();
        performanceRepository.deleteAll();
        layoutRepository.deleteAll();
        eventHallRepository.deleteAll();
        locationRepository.deleteAll();


    }

    @AfterEach
    public void afterEach() {
        eventRepository.deleteAll();
        performerRepository.deleteAll();
    }


    @Test
    public void givenMultipleEvents_whenGetAll_thenReturnsAllEvents() throws Exception {
        performerRepository.save(artist1);
        eventRepository.save(event2);
        eventRepository.save(event3);

        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );
        List<EventDto> eventDtoList = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EventDto[].class));
        assertEquals(2, eventDtoList.size());

    }

    @Test
    public void givenOneEvent_whenFindById_thenEventWithAllProperties() throws Exception {
        performerRepository.save(artist1);
        eventRepository.save(event);

        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI + "/{id}", event.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );
        EventDto eventDto = objectMapper.readValue(response.getContentAsString(), EventDto.class);

        assertEquals(event.getId(), eventDto.getId());
        assertEquals(event.getName(), eventDto.getName());
        assertEquals(event.getEventType(), eventDto.getEventType());
        assertEquals(event.getPerformers().get(0).getPerformerName(), eventDto.getPerformers().get(0).getPerformerName());
    }

    @Test
    public void givenNoEvents_whenPost_thenEventWithAllProperties() throws Exception {
        performerRepository.save(artist1);
        EventDto eventDto = eventMapper.eventToEventDto(event3);
        String body = objectMapper.writeValueAsString(eventDto);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(EVENT_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        EventDto eventDto1 = objectMapper.readValue(response.getContentAsString(), EventDto.class);

        assertNotNull(eventDto1.getId());
        assertEquals(event3.getEventType(), eventDto1.getEventType());
        assertEquals(event3.getPerformers().size(), eventDto1.getPerformers().size());
        assertEquals(event3.getName(), eventDto1.getName());
    }

    @Test
    public void givenNothing_whenGetAll_thenEmptyList() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(EVENT_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );
        List<EventDto> eventDtoList = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EventDto[].class));
        assertEquals(0, eventDtoList.size());
    }

    @Test
    public void givenNothing_whenPostInvalid_then400() throws Exception {
        event4.setName(null);
        event4.setEventType(null);

        EventDto eventDto = eventMapper.eventToEventDto(event4);
        String body = objectMapper.writeValueAsString(eventDto);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(EVENT_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus()),
            () -> {
                //Reads the errors from the body
                String content = response.getContentAsString();
                content = content.substring(content.indexOf('[') + 1, content.indexOf(']'));
                String[] errors = content.split(",");
                assertEquals(2, errors.length);
            }
        );
    }

    @Test
    public void givenEvents_whenGetTop10ForAllEventTypes_thenList() throws Exception {
        performerRepository.save(artist1);
        locationRepository.save(location);
        eventHallRepository.save(eventhall);
        layoutRepository.save(layout);
        eventRepository.save(event_1);
        eventRepository.save(event_2);
        eventRepository.save(event_3);
        eventRepository.save(event_4);
        eventRepository.save(event_5);
        eventRepository.save(event_6);
        eventRepository.save(event_7);
        eventRepository.save(event_8);
        eventRepository.save(event_9);
        eventRepository.save(event_10);
        performanceRepository.save(performance1);
        performanceRepository.save(performance2);
        performanceRepository.save(performance3);
        performanceRepository.save(performance4);
        performanceRepository.save(performance5);
        performanceRepository.save(performance6);
        performanceRepository.save(performance7);
        performanceRepository.save(performance8);
        performanceRepository.save(performance9);
        performanceRepository.save(performance10);


        MvcResult mvcResult = this.mockMvc.perform(
                get(EVENT_BASE_URI+"/top10?eventType="+"")
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<EventDto> result = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EventDto[].class));

        assertEquals(10, result.size());
        for (int i = 0; i < result.size()-1; i++){
            assertTrue(result.get(i).getSoldTickets() >= result.get(i+1).getSoldTickets());
        }
    }

    @Test
    public void givenNoEvents_whenGetTop10ForAllEventTypes_thenEmptyList() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(
                get(EVENT_BASE_URI+"/top10?eventType="+"")
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<EventDto> result = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EventDto[].class));

        assertEquals(0, result.size());

    }


    @Test
    public void givenEvents_whenGetTop10ForConcert_thenList() throws Exception {
        performerRepository.save(artist1);
        locationRepository.save(location);
        eventHallRepository.save(eventhall);
        layoutRepository.save(layout);
        eventRepository.save(event_1);
        eventRepository.save(event_2);
        eventRepository.save(event_3);
        eventRepository.save(event_4);
        eventRepository.save(event_5);
        eventRepository.save(event_6);
        eventRepository.save(event_7);
        eventRepository.save(event_8);
        eventRepository.save(event_9);
        eventRepository.save(event_10);
        performanceRepository.save(performance1);
        performanceRepository.save(performance2);
        performanceRepository.save(performance3);
        performanceRepository.save(performance4);
        performanceRepository.save(performance5);
        performanceRepository.save(performance6);
        performanceRepository.save(performance7);
        performanceRepository.save(performance8);
        performanceRepository.save(performance9);
        performanceRepository.save(performance10);


        MvcResult mvcResult = this.mockMvc.perform(
                get(EVENT_BASE_URI + "/top10?eventType=" + "CONCERT")
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<EventDto> result = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            EventDto[].class));

        assertEquals(7, result.size());
        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).getSoldTickets() >= result.get(i + 1).getSoldTickets());
        }
    }

    @Test
    public void givenEvents_whenGetTop10ForNonExistantEventType_then400() throws Exception {
        performerRepository.save(artist1);
        locationRepository.save(location);
        eventHallRepository.save(eventhall);
        layoutRepository.save(layout);
        eventRepository.save(event_1);
        eventRepository.save(event_2);
        eventRepository.save(event_3);
        eventRepository.save(event_4);
        eventRepository.save(event_5);
        eventRepository.save(event_6);
        eventRepository.save(event_7);
        eventRepository.save(event_8);
        eventRepository.save(event_9);
        eventRepository.save(event_10);
        performanceRepository.save(performance1);
        performanceRepository.save(performance2);
        performanceRepository.save(performance3);
        performanceRepository.save(performance4);
        performanceRepository.save(performance5);
        performanceRepository.save(performance6);
        performanceRepository.save(performance7);
        performanceRepository.save(performance8);
        performanceRepository.save(performance9);
        performanceRepository.save(performance10);


        MvcResult mvcResult = this.mockMvc.perform(
                get(EVENT_BASE_URI+"/top10?eventType="+"ROCK")
                    .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

    }
}
