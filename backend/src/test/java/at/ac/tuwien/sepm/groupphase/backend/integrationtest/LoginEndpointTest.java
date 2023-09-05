package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class LoginEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
        ApplicationUser user = ApplicationUser.builder()
            .username(DEFAULT_USER_USERNAME)
            .firstName(DEFAULT_USER_FIRSTNAME)
            .lastName(DEFAULT_USER_LASTNAME)
            .email(DEFAULT_USER_EMAIL)
            .password(passwordEncoder.encode(DEFAULT_USER_PASSWORD))
            .country(DEFAULT_USER_COUNTRY)
            .city(DEFAULT_USER_CITY)
            .zipCode(DEFAULT_ZIP_CODE)
            .street(DEFAULT_STREET)
            .build();
        userRepository.saveAndFlush(user);

        ApplicationUser user2 = ApplicationUser.builder()
            .username(DEFAULT_USER_USERNAME + 2)
            .firstName(DEFAULT_USER_FIRSTNAME)
            .lastName(DEFAULT_USER_LASTNAME)
            .email(DEFAULT_USER_EMAIL + 2)
            .password(passwordEncoder.encode(DEFAULT_USER_PASSWORD))
            .admin(true)
            .country(DEFAULT_USER_COUNTRY)
            .city(DEFAULT_USER_CITY)
            .zipCode(DEFAULT_ZIP_CODE)
            .street(DEFAULT_STREET)
            .build();
        userRepository.saveAndFlush(user2);
    }

    @Test
    public void givenUser_whenLoggingInWithWrongPassword_thenFailsAndNumberOfFailedAttemptsIncreases() throws Exception {
        ApplicationUser user = userRepository.findApplicationUserByUsernameIgnoreCase(DEFAULT_USER_USERNAME);
        assertThat(user.getFailedLoginAttempts()).isEqualTo(0);
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUsername(DEFAULT_USER_USERNAME);
        userLoginDto.setPassword("Wrong");
        String body = objectMapper.writeValueAsString(userLoginDto);
        MvcResult mvcResult = mockMvc.perform(post(AUTHENTICATION_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(403);
        userRepository.flush();
        user = userRepository.findApplicationUserByUsernameIgnoreCase(DEFAULT_USER_USERNAME);
        assertThat(user.getFailedLoginAttempts()).isEqualTo(1);

    }

    @Test
    public void givenUser_whenLoggingInWithCorrectCredentials_thenResetsNumberOfFailedLoginAttempts() throws Exception {
        ApplicationUser user = userRepository.findApplicationUserByUsernameIgnoreCase(DEFAULT_USER_USERNAME);
        assertThat(user.getFailedLoginAttempts()).isEqualTo(0);
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUsername(DEFAULT_USER_USERNAME);
        userLoginDto.setPassword("Wrong");
        String body = objectMapper.writeValueAsString(userLoginDto);
        for (int i = 0; i < 3; i++) {
            MvcResult mvcResult = mockMvc.perform(post(AUTHENTICATION_BASE_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andDo(print())
                .andReturn();
            MockHttpServletResponse response = mvcResult.getResponse();
            assertThat(response.getStatus()).isEqualTo(403);
        }
        userRepository.flush();
        user = userRepository.findApplicationUserByUsernameIgnoreCase(DEFAULT_USER_USERNAME);
        assertThat(user.getFailedLoginAttempts()).isEqualTo(3);
        userLoginDto.setPassword(DEFAULT_USER_PASSWORD);
        body = objectMapper.writeValueAsString(userLoginDto);
        MvcResult mvcResult = mockMvc.perform(post(AUTHENTICATION_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(200);
        userRepository.flush();
        user = userRepository.findApplicationUserByUsernameIgnoreCase(DEFAULT_USER_USERNAME);
        assertThat(user.getFailedLoginAttempts()).isEqualTo(0);
    }

    //@Test
    public void givenUser_whenLoggingInFiveTimesWithWrongPassword_thenAccountIsLocked() throws Exception {
        ApplicationUser user = userRepository.findApplicationUserByUsernameIgnoreCase(DEFAULT_USER_USERNAME);
        assertThat(user.getFailedLoginAttempts()).isEqualTo(0);
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUsername(DEFAULT_USER_USERNAME);
        userLoginDto.setPassword("Wrong");
        String body = objectMapper.writeValueAsString(userLoginDto);
        for (int i = 0; i < 5; i++) {
            MvcResult mvcResult = mockMvc.perform(post(AUTHENTICATION_BASE_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andDo(print())
                .andReturn();
            MockHttpServletResponse response = mvcResult.getResponse();
            assertThat(response.getStatus()).isEqualTo(403);
        }
        userRepository.flush();
        user = userRepository.findApplicationUserByUsernameIgnoreCase(DEFAULT_USER_USERNAME);
        assertThat(user.getFailedLoginAttempts()).isEqualTo(5);
        assertTrue(user.isLocked());
        userLoginDto.setPassword(DEFAULT_USER_PASSWORD);
        body = objectMapper.writeValueAsString(userLoginDto);
        MvcResult mvcResult = mockMvc.perform(post(AUTHENTICATION_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(403);
        userRepository.flush();
        user = userRepository.findApplicationUserByUsernameIgnoreCase(DEFAULT_USER_USERNAME);
        assertThat(user.getFailedLoginAttempts()).isEqualTo(6);
        assertTrue(user.isLocked());
    }

    @Test
    public void givenUser_whenLoggingInWithExistingUsernameButWrongLetterCases_thenAccountIsLoggedIn() throws Exception {
        ApplicationUser user = userRepository.findApplicationUserByUsernameIgnoreCase(DEFAULT_USER_USERNAME.toLowerCase());
        assertNotNull(user);
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUsername(DEFAULT_USER_USERNAME.toLowerCase());
        userLoginDto.setPassword(DEFAULT_USER_PASSWORD);
        String body = objectMapper.writeValueAsString(userLoginDto);
        MvcResult mvcResult = mockMvc.perform(post(AUTHENTICATION_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    public void givenAdmin_whenLoggingInFiveTimesWithWrongPassword_thenAccountIsNotLocked() throws Exception {
        ApplicationUser user = userRepository.findApplicationUserByUsernameIgnoreCase(DEFAULT_USER_USERNAME + 2);
        assertThat(user.getFailedLoginAttempts()).isEqualTo(0);
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUsername(DEFAULT_USER_USERNAME + 2);
        userLoginDto.setPassword("Wrong");
        String body = objectMapper.writeValueAsString(userLoginDto);
        for (int i = 0; i < 5; i++) {
            MvcResult mvcResult = mockMvc.perform(post(AUTHENTICATION_BASE_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andDo(print())
                .andReturn();
            MockHttpServletResponse response = mvcResult.getResponse();
            assertThat(response.getStatus()).isEqualTo(403);
        }
        userRepository.flush();
        user = userRepository.findApplicationUserByUsernameIgnoreCase(DEFAULT_USER_USERNAME + 2);
        assertThat(user.getFailedLoginAttempts()).isEqualTo(0);
        assertFalse(user.isLocked());
        userLoginDto.setPassword(DEFAULT_USER_PASSWORD);
        body = objectMapper.writeValueAsString(userLoginDto);
        MvcResult mvcResult = mockMvc.perform(post(AUTHENTICATION_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(200);
    }
}
