package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserResetPasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserShortDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.PasswordResetToken;
import at.ac.tuwien.sepm.groupphase.backend.repository.PasswordResetTokenRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.core.type.TypeReference;
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

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AdminEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
        for (int i = 0; i < 10; i++) {
            ApplicationUser user = ApplicationUser.builder()
                .username(DEFAULT_USER_USERNAME + i)
                .firstName(DEFAULT_USER_FIRSTNAME)
                .lastName(DEFAULT_USER_LASTNAME)
                .email(DEFAULT_USER_EMAIL + i)
                .password(passwordEncoder.encode(DEFAULT_USER_PASSWORD))
                .country(DEFAULT_USER_COUNTRY)
                .city(DEFAULT_USER_CITY)
                .zipCode(DEFAULT_ZIP_CODE)
                .street(DEFAULT_STREET)
                .build();
            if (i < 5) {
                user.setLocked(true);
            }
            userRepository.saveAndFlush(user);
        }


    }


    //@Test
    public void givenAdmin_whenCreatingUserWithValidData_thenUserIsPersisted() throws Exception {
        UserCreationDto userCreationDto = defaultCreationDto();
        boolean admin = true;
        userCreationDto.setAdmin(admin);
        String body = objectMapper.writeValueAsString(userCreationDto);
        MvcResult mvcResult = mockMvc.perform(post(ADMIN_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        UserDto userDto = objectMapper.readValue(response.getContentAsString(), UserDto.class);
        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(userDto.getId()).isNotNull();
        assertThat(userDto.getEmail()).isEqualTo(DEFAULT_USER_EMAIL);
        assertThat(userDto.getUsername()).isEqualTo(DEFAULT_USER_USERNAME);
        assertThat(userDto.getFirstName()).isEqualTo(DEFAULT_USER_FIRSTNAME);
        assertThat(userDto.getLastName()).isEqualTo(DEFAULT_USER_LASTNAME);
        assertThat(userDto.getCountry()).isEqualTo(DEFAULT_USER_COUNTRY);
        assertThat(userDto.getStreet()).isEqualTo(DEFAULT_STREET);
        assertThat(userDto.getCity()).isEqualTo(DEFAULT_USER_CITY);
        assertThat(userDto.getZipCode()).isEqualTo(DEFAULT_ZIP_CODE);
        assertNotNull(userRepository.findApplicationUserByUsernameIgnoreCase(DEFAULT_USER_USERNAME));
        assertNotNull(userRepository.findApplicationUserByEmail(DEFAULT_USER_EMAIL));

    }

    @Test
    public void givenUser_whenRegisteringWithInvalidEmail_thenReturnsValidationError() throws Exception {
        UserCreationDto userCreationDto = defaultCreationDto();
        userCreationDto.setEmail("Invalid");
        String body = objectMapper.writeValueAsString(userCreationDto);
        MvcResult mvcResult = mockMvc.perform(post(ADMIN_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(400);

    }

    @Test
    public void givenUser_whenRegisteringWithTooShortCredentials_thenReturnsValidationError() throws Exception {
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultCreationDto().setUsername("A")))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultCreationDto().setFirstName("A")))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultCreationDto().setLastName("A")))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultCreationDto().setCountry("A")))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultCreationDto().setCity("A")))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultCreationDto().setUsername("A")))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultCreationDto().setStreet("A")))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultCreationDto().setZipCode("A")))
            .getStatus())
            .isEqualTo(400);

    }

    @Test
    public void givenUser_whenRegisteringWithTooLongCredentials_thenReturnsValidationError() throws Exception {
        String tooLong = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultCreationDto().setUsername(tooLong)))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultCreationDto().setFirstName(tooLong)))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultCreationDto().setLastName(tooLong)))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultCreationDto().setCountry(tooLong)))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultCreationDto().setCity(tooLong)))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultCreationDto().setUsername(tooLong)))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultCreationDto().setStreet(tooLong)))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultCreationDto().setZipCode(tooLong)))
            .getStatus())
            .isEqualTo(400);

    }

    @Test
    public void givenAdmin_WhenGettingAllLockedUsers_ThenReturnsAllLockedUsers() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(ADMIN_BASE_URI + "/customers/locked")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        List<UserShortDto> lockedUsers = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(lockedUsers.size()).isEqualTo(5);
        assertThat(userRepository.findAll().size()).isGreaterThan(5);
    }

    @Test
    public void givenAdmin_WhenUnlockingUser_ThenUnlocksUser() throws Exception {
        MvcResult mvcResult = mockMvc.perform(put(ADMIN_BASE_URI + "/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("username", DEFAULT_USER_USERNAME + 1)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(200);
        mvcResult = mockMvc.perform(get(ADMIN_BASE_URI + "/customers/locked")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();
        List<UserShortDto> lockedUsers = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(lockedUsers.size()).isEqualTo(4);
        assertThat(userRepository.findAll().size()).isGreaterThan(4);
    }

    @Test
    public void givenAdmin_WhenGettingAllNonLockedUsers_ThenReturnsAllNonLockedUsers() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(ADMIN_BASE_URI + "/customers?page=0&size=5")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        String resp = response.getContentAsString();
        assertThat(resp.contains("\"numberOfElements\":5")).isTrue();
        assertThat(userRepository.findAll().size()).isGreaterThan(5);
    }

    @Test
    public void givenAdmin_WhenLockingUser_ThenLocksUser() throws Exception {
        assertThat(userRepository.findApplicationUserByLockedIsTrue().size()).isEqualTo(5);
        MvcResult mvcResult = mockMvc.perform(put(ADMIN_BASE_URI + "/lock")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("username", DEFAULT_USER_USERNAME + 5)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(200);
        mvcResult = mockMvc.perform(get(ADMIN_BASE_URI + "/customers/locked")
                .contentType(MediaType.APPLICATION_JSON)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();
        List<UserShortDto> lockedUsers = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
        assertThat(lockedUsers.size()).isEqualTo(6);
        assertThat(userRepository.findAll().size()).isGreaterThan(6);
    }

    //@Test
    public void givenAdmin_WhenRequestingResetAndThenUserResettingWithValidToken_ThenPasswordShouldBeReset() throws Exception {
        String testUsername = DEFAULT_USER_USERNAME + 6;

        MvcResult mvcResult = mockMvc.perform(post(ADMIN_BASE_URI + "/reset")
                .queryParam("username", testUsername)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(200);

        ApplicationUser user6 = userRepository.findApplicationUserByUsernameIgnoreCase(testUsername);

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findPasswordResetTokenByUser(user6);
        assertThat(passwordResetToken).isNotNull();
        String password = "newPassword";
        UserResetPasswordDto userResetPasswordDto = new UserResetPasswordDto(password, passwordResetToken.getToken());

        String body = objectMapper.writeValueAsString(userResetPasswordDto);
        mvcResult = mockMvc.perform(put(CUSTOMER_BASE_URI + "/resetPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(200);

        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername(testUsername);
        loginDto.setPassword(password);
        String loginBody = objectMapper.writeValueAsString(loginDto);
        mvcResult = mockMvc.perform(post(AUTHENTICATION_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(200);
    }


    private MockHttpServletResponse makeMvcRequest(String body) throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES)))
            .andDo(print())
            .andReturn();
        return mvcResult.getResponse();
    }

    private UserCreationDto defaultCreationDto() {
        UserCreationDto userCreationDto = new UserCreationDto();
        userCreationDto.setEmail(DEFAULT_USER_EMAIL);
        userCreationDto.setUsername(DEFAULT_USER_USERNAME);
        userCreationDto.setAdmin(false);
        userCreationDto.setFirstName(DEFAULT_USER_FIRSTNAME);
        userCreationDto.setLastName(DEFAULT_USER_LASTNAME);
        userCreationDto.setStreet(DEFAULT_STREET);
        userCreationDto.setCountry(DEFAULT_USER_COUNTRY);
        userCreationDto.setCity(DEFAULT_USER_CITY);
        userCreationDto.setZipCode(DEFAULT_ZIP_CODE);
        return userCreationDto;
    }
}
