package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.BandDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PerformerDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.*;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Band;
import at.ac.tuwien.sepm.groupphase.backend.entity.Performer;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static at.ac.tuwien.sepm.groupphase.backend.basetest.TestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PerformerEndpointTest {
    @Autowired
    private PerformerRepository performerRepository;
    @Autowired
    private BandRepository bandRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private PerformerMapper performerMapper;
    @Autowired
    private ArtistMapper artistMapper;
    @Autowired
    private BandMapper bandMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private JwtTokenizer jwtTokenizer;
    @Autowired
    private ObjectMapper objectMapper;

    Artist artistTest1 = Artist.builder()
        .performerName(TEST_ARTIST_PERFORMERNAME+"1")
        .firstName(TEST_ARTIST_FIRSTNAME)
        .lastName(TEST_ARTIST_LASTNAME)
        .build();

    Artist artistTest2 = Artist.builder()
        .performerName(TEST_ARTIST_PERFORMERNAME+"2")
        .firstName(TEST_ARTIST_FIRSTNAME)
        .lastName(TEST_ARTIST_LASTNAME)
        .build();

    Band bandTest1 = Band.builder()
        .performerName(TEST_BAND_PERFORMERNAME+"3")
        .artists(new ArrayList<>())
        .build();

    @BeforeEach
    public void beforeEach(){
        eventRepository.deleteAll();
        performerRepository.deleteAll();
        bandRepository.deleteAll();
        artistRepository.deleteAll();
    }

    @Test
    public void givenNothing_whenGetAll_thenEmptyList() throws Exception{
        MvcResult mvcResult = this.mockMvc.perform(get(PERFORMER_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );
        List<PerformerDto> performerDtoList =  Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            PerformerDto[].class));
        assertEquals(0, performerDtoList.size());
    }

    @Test
    public void givenTwo_whenGetAll_thenReturnTwo() throws Exception {
        performerRepository.save(artistTest1);
        performerRepository.save(artistTest2);


        PerformerDto performerDto = performerMapper.performerToPerformerDto(artistTest1);
        String body = objectMapper.writeValueAsString(performerDto);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(PERFORMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );

        List<PerformerDto> performerDtoList =  Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            PerformerDto[].class));
        assertEquals(2, performerDtoList.size());
    }

    @Test
    public void givenOneBand_whenGetAll_thenReturnOneBand() throws Exception {
        bandRepository.save(bandTest1);


        BandDto bandDto = bandMapper.bandToBandDto(bandTest1);
        String body = objectMapper.writeValueAsString(bandDto);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(BAND_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );

        List<BandDto> bandDtoList =  Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            BandDto[].class));
        assertEquals(1, bandDtoList.size());
    }

    @Test
    public void givenTwoArtists_whenGetAll_thenReturnTwoArtists() throws Exception {
        artistRepository.save(artistTest1);
        artistRepository.save(artistTest2);


        ArtistDto artistDto = artistMapper.artistToArtistDto(artistTest1);
        String body = objectMapper.writeValueAsString(artistDto);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get(ARTIST_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );

        List<ArtistDto> artistDtoList =  Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            ArtistDto[].class));
        assertEquals(2, artistDtoList.size());
    }

    @Test
    public void givenNoArtists_whenPost_thenArtistWithAllProperties() throws Exception {

        ArtistDto artistDto = artistMapper.artistToArtistDto(artistTest1);
        String body = objectMapper.writeValueAsString(artistDto);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(ARTIST_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        ArtistDto artistDto1 = objectMapper.readValue(response.getContentAsString(), ArtistDto.class);

        assertNotNull(artistDto1.getId());
        assertEquals(artistTest1.getPerformerName(), artistDto1.getPerformerName());
        assertEquals(artistTest1.getFirstName(), artistDto1.getFirstName());
        assertEquals(artistTest1.getLastName(), artistDto1.getLastName());
    }

    @Test
    public void givenNoBands_whenPost_thenBandWithAllProperties() throws Exception {
        BandDto bandDto = bandMapper.bandToBandDto(bandTest1);
        String body = objectMapper.writeValueAsString(bandDto);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(BAND_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        BandDto bandDto1 = objectMapper.readValue(response.getContentAsString(), BandDto.class);

        assertNotNull(bandDto1.getId());
        assertEquals(bandTest1.getPerformerName(), bandDto1.getPerformerName());
    }



    @Test
    public void givenNothing_whenPostInvalid_then400() throws Exception {
        artistTest1.setPerformerName(null);
        artistTest1.setFirstName(null);

        ArtistDto artistDto = artistMapper.artistToArtistDto(artistTest1);
        String body = objectMapper.writeValueAsString(artistDto);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(ARTIST_BASE_URI)
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
                assertEquals(1, errors.length);
            }
        );
    }

}
