package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LocationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.LocationMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class LocationEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private LocationService locationService;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;

    private Location location = Location.builder()
        .name("Test the location number 1")
        .country("Austria")
        .city("Vienna")
        .street("Teststraße")
        .zipCode("1120")
        .build();

    private Location location2 = Location.builder()
        .name("Test the location number 2")
        .country("Austria")
        .city("Vienna")
        .street("Teststraße")
        .zipCode("1120")
        .build();

    private Location invalidLocation = Location.builder()
        .country("Austria")
        .city("Vienna")
        .street("streetname")
        .zipCode("1120")
        .build();

    @BeforeEach
    public void beforeEach() {
        this.locationRepository.deleteAll();

        this.location = Location.builder()
            .name("Test the location number 1")
            .country("Austria")
            .city("Vienna")
            .street("Teststreet")
            .zipCode("1120")
            .build();

        this.location2 = Location.builder()
            .name("Test the location number 2")
            .country("Austria")
            .city("Vienna")
            .street("Teststreet")
            .zipCode("1120")
            .build();

        this.invalidLocation = Location.builder()
            .country("Austria")
            .city("Vienna")
            .street("streetname")
            .zipCode("1120")
            .build();
    }


    @Test
    public void givenTwoLocations_whenFindAll_thenEmptyList() throws Exception {
        this.locationRepository.save(this.location);
        this.locationRepository.save(this.location2);

        MvcResult mvcResult = this.mockMvc.perform(get(LOCATION_BASE_URI)
            .header(this.securityProperties.getAuthHeader(), this.jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        List<LocationDto> locations = Arrays.asList(this.objectMapper.readValue(response.getContentAsString(),
            LocationDto[].class));

        assertEquals(locations.size(), 2);

    }

    @Test
    public void givenLocation_whenFindById_thenLocationDto() throws Exception {
        long id = this.locationRepository.save(this.location).getId();

        MvcResult mvcResult = this.mockMvc.perform(get(LOCATION_BASE_URI + "/" + id)
                .header(this.securityProperties.getAuthHeader(), this.jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        LocationDto locationDto = objectMapper.readValue(response.getContentAsString(), LocationDto.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertAll(
            () -> assertEquals(locationDto.getName(), this.location.getName()),
            () -> assertEquals(locationDto.getCountry(), this.location.getCountry()),
            () -> assertEquals(locationDto.getCity(), this.location.getCity()),
            () -> assertEquals(locationDto.getStreet(), this.location.getStreet()),
            () -> assertEquals(locationDto.getZipCode(), this.location.getZipCode())
        );
    }

    @Test
    public void givenLocation_whenFindByInvalidId_then404() throws Exception {
        long id = this.locationRepository.save(this.location).getId();

        MvcResult mvcResult = this.mockMvc.perform(get(LOCATION_BASE_URI + "/-1")
                .header(this.securityProperties.getAuthHeader(), this.jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void givenNothing_whenCreatingValidLocation_thenLocationAndOk() throws Exception {
        LocationDto locationDto = this.locationMapper.locationToLocationDto(this.location);
        String requestBody = this.objectMapper.writeValueAsString(locationDto);

        MvcResult mvcResult = this.mockMvc.perform(post(LOCATION_BASE_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody)
            .header(this.securityProperties.getAuthHeader(), this.jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        LocationDto responseLocationDto = this.objectMapper.readValue(response.getContentAsString(), LocationDto.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        assertAll(
            () -> assertThat(responseLocationDto.getId()).isGreaterThanOrEqualTo(1),
            () -> assertEquals(responseLocationDto.getName(), this.location.getName()),
            () -> assertEquals(responseLocationDto.getCountry(), this.location.getCountry()),
            () -> assertEquals(responseLocationDto.getCity(), this.location.getCity()),
            () -> assertEquals(responseLocationDto.getStreet(), this.location.getStreet()),
            () -> assertEquals(responseLocationDto.getZipCode(), this.location.getZipCode())
        );

    }

    @Test
    public void givenLocation_whenCreatingDuplicateLocation_then409() throws Exception {
        this.locationRepository.save(this.location);
        LocationDto locationDto = this.locationMapper.locationToLocationDto(this.location);
        String requestBody = this.objectMapper.writeValueAsString(locationDto);

        MvcResult mvcResult = this.mockMvc.perform(post(LOCATION_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(this.securityProperties.getAuthHeader(), this.jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    }

    @Test
    public void givenNothing_whenCreatingInvalidLocation_then400() throws Exception {
        LocationDto locationDto = this.locationMapper.locationToLocationDto(this.invalidLocation);
        String requestBody = this.objectMapper.writeValueAsString(locationDto);

        MvcResult mvcResult = this.mockMvc.perform(post(LOCATION_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(this.securityProperties.getAuthHeader(), this.jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

}
