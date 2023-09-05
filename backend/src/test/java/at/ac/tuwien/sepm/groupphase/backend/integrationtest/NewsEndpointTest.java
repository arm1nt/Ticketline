package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.NewsShortDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.NewsMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.News;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class NewsEndpointTest implements TestData {

    private final ApplicationUser user = ApplicationUser.builder()
        .username(ADMIN_USERNAME)
        .firstName(DEFAULT_USER_FIRSTNAME)
        .lastName(DEFAULT_USER_LASTNAME)
        .email(ADMIN_USER)
        .password(DEFAULT_USER_PASSWORD)
        .country(DEFAULT_USER_COUNTRY)
        .city(DEFAULT_USER_CITY)
        .zipCode(DEFAULT_ZIP_CODE)
        .street(DEFAULT_STREET)
        .build();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserHasSeenNewsRepository userHasSeenNewsRepository;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private NewsMapper newsMapper;
    @Autowired
    private JwtTokenizer jwtTokenizer;
    @Autowired
    private SecurityProperties securityProperties;
    private News news = News.NewsBuilder.aNews()
        .withTitle(TEST_NEWS_TITLE)
        .withText(TEST_NEWS_TEXT)
        .withPublishedAt(TEST_NEWS_PUBLISHED_AT)
        .withImage(TEST_NEWS_IMAGE)
        .build();

    @BeforeEach
    public void beforeEach() {
        orderRepository.deleteAll();
        ticketRepository.deleteAll();
        userRepository.deleteAll();
        newsRepository.deleteAll();
        userHasSeenNewsRepository.deleteAll();
        news = News.NewsBuilder.aNews()
            .withTitle(TEST_NEWS_TITLE)
            .withText(TEST_NEWS_TEXT)
            .withPublishedAt(TEST_NEWS_PUBLISHED_AT)
            .withImage(TEST_NEWS_IMAGE)
            .build();
    }

    @Test
    public void givenNothing_whenFindAll_thenEmptyList() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<NewsShortDto> newsShortDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            NewsShortDto[].class));

        assertEquals(0, newsShortDtos.size());
    }

    @Test
    public void givenOneNews_whenFindAll_thenListWithSizeOneAndNewsWithAllPropertiesButShorterText()
        throws Exception {
        newsRepository.save(news);

        MvcResult mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<NewsShortDto> newsShortDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            NewsShortDto[].class));

        assertEquals(1, newsShortDtos.size());
        NewsShortDto newsShortDto = newsShortDtos.get(0);
        assertAll(
            () -> assertEquals(news.getId(), newsShortDto.getId()),
            () -> assertEquals(TEST_NEWS_TITLE, newsShortDto.getTitle()),
            () -> assertEquals(TEST_NEWS_TEXT.substring(0, 200) + "...", newsShortDto.getText()),
            () -> assertEquals(TEST_NEWS_PUBLISHED_AT, newsShortDto.getPublishedAt()),
            () -> assertEquals(TEST_NEWS_IMAGE, newsShortDto.getImage())
        );
    }

    @Test
    public void givenOneNews_whenFindAllNew_thenListWithSizeOneAndNewsWithAllPropertiesButShorterText()
        throws Exception {
        newsRepository.save(news);
        userRepository.save(user);

        MvcResult mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI + "/new")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<NewsShortDto> newsShortDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            NewsShortDto[].class));

        assertEquals(1, newsShortDtos.size());
        NewsShortDto newsShortDto = newsShortDtos.get(0);
        assertAll(
            () -> assertEquals(news.getId(), newsShortDto.getId()),
            () -> assertEquals(TEST_NEWS_TITLE, newsShortDto.getTitle()),
            () -> assertEquals(TEST_NEWS_TEXT.substring(0, 200) + "...", newsShortDto.getText()),
            () -> assertEquals(TEST_NEWS_PUBLISHED_AT, newsShortDto.getPublishedAt()),
            () -> assertEquals(TEST_NEWS_IMAGE, newsShortDto.getImage())
        );
    }

    @Test
    public void givenOneNews_whenFindAllOld_thenListWithSizeOneAndNewsWithAllPropertiesButShorterText()
        throws Exception {
        newsRepository.save(news);
        userRepository.save(user);

        MockHttpServletResponse responseById = this.mockMvc.perform(get(NEWS_BASE_URI + "/{id}", news.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn().getResponse();

        MvcResult mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI + "/old")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<NewsShortDto> newsShortDtos = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            NewsShortDto[].class));

        assertEquals(1, newsShortDtos.size());
        NewsShortDto newsShortDto = newsShortDtos.get(0);
        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), responseById.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, responseById.getContentType()),
            () -> assertEquals(news.getId(), newsShortDto.getId()),
            () -> assertEquals(TEST_NEWS_TITLE, newsShortDto.getTitle()),
            () -> assertEquals(TEST_NEWS_TEXT.substring(0, 200) + "...", newsShortDto.getText()),
            () -> assertEquals(TEST_NEWS_PUBLISHED_AT, newsShortDto.getPublishedAt()),
            () -> assertEquals(TEST_NEWS_IMAGE, newsShortDto.getImage())
        );
    }


    @Test
    public void givenNothing_whenPost_thenNewsWithAllSetPropertiesPlusIdAndPublishedDate() throws Exception {
        news.setPublishedAt(null);
        NewsDetailDto newsDetailDto = newsMapper.newsToNewsDetailDto(news);
        String body = objectMapper.writeValueAsString(newsDetailDto);

        MvcResult mvcResult = this.mockMvc.perform(post(NEWS_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        NewsDetailDto newsResponse = objectMapper.readValue(response.getContentAsString(), NewsDetailDto.class);

        assertNotNull(newsResponse.getId());
        assertNotNull(newsResponse.getPublishedAt());
        newsResponse.setId(null);
        newsResponse.setPublishedAt(null);
        assertEquals(news, newsMapper.newsDetailDtoToNews(newsResponse));
    }

    @Test
    public void givenNothing_whenPostInvalid_then400() throws Exception {
        news.setTitle(null);
        news.setImage(null);
        news.setText(null);
        NewsDetailDto newsDetailDto = newsMapper.newsToNewsDetailDto(news);
        String body = objectMapper.writeValueAsString(newsDetailDto);

        MvcResult mvcResult = this.mockMvc.perform(post(NEWS_BASE_URI)
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
                assertEquals(4, errors.length);
            }
        );
    }

    @Test
    public void givenOneNews_whenFindById_thenNewsWithAllProperties() throws Exception {
        newsRepository.save(news);
        userRepository.save(user);

        MvcResult mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI + "/{id}", news.getId())
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertAll(
            () -> assertEquals(HttpStatus.OK.value(), response.getStatus()),
            () -> assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType())
        );

        NewsDetailDto newsDetailDto = objectMapper.readValue(response.getContentAsString(),
            NewsDetailDto.class);

        assertEquals(news, newsMapper.newsDetailDtoToNews(newsDetailDto));
    }

    @Test
    public void givenOneMessage_whenFindByNonExistingId_then404() throws Exception {
        newsRepository.save(news);
        userRepository.save(user);
        MvcResult mvcResult = this.mockMvc.perform(get(NEWS_BASE_URI + "/{id}", -1)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USERNAME, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
}
