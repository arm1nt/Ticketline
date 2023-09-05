package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserResetPasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.PasswordResetToken;
import at.ac.tuwien.sepm.groupphase.backend.repository.OrderRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.PasswordResetTokenRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.TicketRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserEndpointTest implements TestData {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    private ApplicationUser user = ApplicationUser.builder()
        .username(DEFAULT_USER_USERNAME)
        .firstName(DEFAULT_USER_FIRSTNAME)
        .lastName(DEFAULT_USER_LASTNAME)
        .email(DEFAULT_USER_EMAIL)
        .password(DEFAULT_USER_PASSWORD)
        .country(DEFAULT_USER_COUNTRY)
        .city(DEFAULT_USER_CITY)
        .zipCode(DEFAULT_ZIP_CODE)
        .street(DEFAULT_STREET)
        .build();

    /*private ApplicationUser user = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
        .withUsername(DEFAULT_USER_USERNAME)
        .withFirstname(DEFAULT_USER_FIRSTNAME)
        .withLastname(DEFAULT_USER_LASTNAME)
        .withEmail(DEFAULT_USER_EMAIL)
        .withPassword(DEFAULT_USER_PASSWORD)
        .withCountry(DEFAULT_USER_COUNTRY)
        .withCity(DEFAULT_USER_CITY)
        .withZipcode(DEFAULT_ZIP_CODE)
        .withStreet(DEFAULT_STREET)
        .build();*/

    private ApplicationUser updateUser = ApplicationUser.builder().build();

    @BeforeEach
    public void beforeEach() {
        orderRepository.deleteAll();
        ticketRepository.deleteAll();
        userRepository.deleteAll();
        user = ApplicationUser.builder()
            .username(DEFAULT_USER_USERNAME)
            .firstName(DEFAULT_USER_FIRSTNAME)
            .lastName(DEFAULT_USER_LASTNAME)
            .email(DEFAULT_USER_EMAIL)
            .password(DEFAULT_USER_PASSWORD)
            .country(DEFAULT_USER_COUNTRY)
            .city(DEFAULT_USER_CITY)
            .zipCode(DEFAULT_ZIP_CODE)
            .street(DEFAULT_STREET)
            .build();
        /*user = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
            .withUsername(DEFAULT_USER_USERNAME)
            .withFirstname(DEFAULT_USER_FIRSTNAME)
            .withLastname(DEFAULT_USER_LASTNAME)
            .withEmail(DEFAULT_USER_EMAIL)
            .withPassword(DEFAULT_USER_PASSWORD)
            .withCountry(DEFAULT_USER_COUNTRY)
            .withCity(DEFAULT_USER_CITY)
            .withZipcode(DEFAULT_ZIP_CODE)
            .withStreet(DEFAULT_STREET)
            .build();*/
        updateUser = ApplicationUser.builder().build();
    }

    private ApplicationUser buildDefaultUser() {
        return ApplicationUser.builder()
            .email(DEFAULT_USER_EMAIL)
            .password(DEFAULT_USER_PASSWORD)
            .username(DEFAULT_USER_USERNAME)
            .firstName(DEFAULT_USER_FIRSTNAME)
            .lastName(DEFAULT_USER_LASTNAME)
            .street(DEFAULT_STREET)
            .country(DEFAULT_USER_COUNTRY)
            .city(DEFAULT_USER_CITY)
            .zipCode(DEFAULT_ZIP_CODE)
            .build();
    }

    private UserRegistrationDto defaultRegistrationDto() {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setEmail(DEFAULT_USER_EMAIL);
        userRegistrationDto.setUsername(DEFAULT_USER_USERNAME);
        userRegistrationDto.setPassword(DEFAULT_USER_PASSWORD);
        userRegistrationDto.setFirstName(DEFAULT_USER_FIRSTNAME);
        userRegistrationDto.setLastName(DEFAULT_USER_LASTNAME);
        userRegistrationDto.setStreet(DEFAULT_STREET);
        userRegistrationDto.setCountry(DEFAULT_USER_COUNTRY);
        userRegistrationDto.setCity(DEFAULT_USER_CITY);
        userRegistrationDto.setZipCode(DEFAULT_ZIP_CODE);
        return userRegistrationDto;
    }

    @Test
    public void givenUser_whenGetDetails_thenUserDetailsDtoWithAllNonAdministrativeInformation()
        throws Exception {
        userRepository.save(user);

        MvcResult mvcResult = this.mockMvc.perform(get(CUSTOMER_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        UserDetailsDto userDetailsDto = objectMapper.readValue(response.getContentAsString(), UserDetailsDto.class);
        assertThat(userDetailsDto.getUsername()).isEqualTo(DEFAULT_USER_USERNAME);
        assertThat(userDetailsDto.getFirstName()).isEqualTo(DEFAULT_USER_FIRSTNAME);
        assertThat(userDetailsDto.getLastName()).isEqualTo(DEFAULT_USER_LASTNAME);
        assertThat(userDetailsDto.getEmail()).isEqualTo(DEFAULT_USER_EMAIL);
        assertThat(userDetailsDto.getCountry()).isEqualTo(DEFAULT_USER_COUNTRY);
        assertThat(userDetailsDto.getCity()).isEqualTo(DEFAULT_USER_CITY);
        assertThat(userDetailsDto.getZipCode()).isEqualTo(DEFAULT_ZIP_CODE);
        assertThat(userDetailsDto.getStreet()).isEqualTo(DEFAULT_STREET);
    }

    @Test
    void givenUser_whenPatchingWithValidFirstname_thenUserDetailsWithAllNonAdiministrativeInformationAndUpdatedFirstname()
        throws Exception {
        userRepository.save(user);

        updateUser.setFirstName("Update first name");
        UserUpdateDto userUpdateDto = userMapper.applicationUserToUserUpdateDto(updateUser);
        String body = objectMapper.writeValueAsString(userUpdateDto);


        MvcResult mvcResult = this.mockMvc.perform(patch(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        UserDetailsDto userDetailsDto = objectMapper.readValue(response.getContentAsString(), UserDetailsDto.class);
        assertThat(userDetailsDto.getUsername()).isEqualTo(DEFAULT_USER_USERNAME);
        assertThat(userDetailsDto.getFirstName()).isEqualTo(userUpdateDto.getFirstName());
        assertThat(userDetailsDto.getLastName()).isEqualTo(DEFAULT_USER_LASTNAME);
        assertThat(userDetailsDto.getEmail()).isEqualTo(DEFAULT_USER_EMAIL);
        assertThat(userDetailsDto.getCountry()).isEqualTo(DEFAULT_USER_COUNTRY);
        assertThat(userDetailsDto.getCity()).isEqualTo(DEFAULT_USER_CITY);
        assertThat(userDetailsDto.getZipCode()).isEqualTo(DEFAULT_ZIP_CODE);
        assertThat(userDetailsDto.getStreet()).isEqualTo(DEFAULT_STREET);
    }

    @Test
    public void givenUser_whenPatchingWithInvalidFirstname_then422()
        throws Exception {

        updateUser.setFirstName("            ");
        UserUpdateDto userUpdateDto = userMapper.applicationUserToUserUpdateDto(updateUser);
        String body = objectMapper.writeValueAsString(userUpdateDto);


        MvcResult mvcResult = this.mockMvc.perform(patch(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenUser_whenPatchingWithInvalidLastname_then422()
        throws Exception {

        updateUser.setLastName("          ");
        UserUpdateDto userUpdateDto = userMapper.applicationUserToUserUpdateDto(updateUser);
        String body = objectMapper.writeValueAsString(userUpdateDto);

        MvcResult mvcResult = this.mockMvc.perform(patch(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenUser_whenPatchingWithValidLastname_thenUserDetailsWithNonAdministrativeInformationAndChangedLastname()
        throws Exception {
        userRepository.save(user);

        updateUser.setLastName("My crazy new lastname");
        UserUpdateDto userUpdateDto = userMapper.applicationUserToUserUpdateDto(updateUser);
        String body = objectMapper.writeValueAsString(userUpdateDto);

        MvcResult mvcResult = this.mockMvc.perform(patch(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        UserDetailsDto userDetailsDto = objectMapper.readValue(response.getContentAsString(), UserDetailsDto.class);
        assertThat(userDetailsDto.getUsername()).isEqualTo(DEFAULT_USER_USERNAME);
        assertThat(userDetailsDto.getFirstName()).isEqualTo(DEFAULT_USER_FIRSTNAME);
        assertThat(userDetailsDto.getLastName()).isEqualTo(userUpdateDto.getLastName());
        assertThat(userDetailsDto.getEmail()).isEqualTo(DEFAULT_USER_EMAIL);
        assertThat(userDetailsDto.getCountry()).isEqualTo(DEFAULT_USER_COUNTRY);
        assertThat(userDetailsDto.getCity()).isEqualTo(DEFAULT_USER_CITY);
        assertThat(userDetailsDto.getZipCode()).isEqualTo(DEFAULT_ZIP_CODE);
        assertThat(userDetailsDto.getStreet()).isEqualTo(DEFAULT_STREET);
    }

    @Test
    public void givenUser_whenPatchingWithInvalidEmail_then422() throws Exception {
        updateUser.setEmail("invalidEmail@");

        UserUpdateDto userUpdateDto = userMapper.applicationUserToUserUpdateDto(updateUser);
        String body = objectMapper.writeValueAsString(userUpdateDto);

        MvcResult mvcResult = this.mockMvc.perform(patch(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenUser_whenPatchingWithInvalidEmail2_then422() throws Exception {

        updateUser.setEmail("invalid@email.at.");
        UserUpdateDto userUpdateDto = userMapper.applicationUserToUserUpdateDto(updateUser);
        String body = objectMapper.writeValueAsString(userUpdateDto);

        MvcResult mvcResult = this.mockMvc.perform(patch(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenUser_whenPatchingWithInvalidEmail3_then422() throws Exception {

        updateUser.setEmail("          ");
        UserUpdateDto userUpdateDto = userMapper.applicationUserToUserUpdateDto(updateUser);
        String body = objectMapper.writeValueAsString(userUpdateDto);

        MvcResult mvcResult = this.mockMvc.perform(patch(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenUser_whenPatchingWithInvalidEmail4_then422() throws Exception {

        updateUser.setEmail("invalid@email@still.com");
        UserUpdateDto userUpdateDto = userMapper.applicationUserToUserUpdateDto(updateUser);
        String body = objectMapper.writeValueAsString(userUpdateDto);

        MvcResult mvcResult = this.mockMvc.perform(patch(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenUser_whenPatchingWithEmailThatAlreadyExists_then409() throws Exception {
        ApplicationUser emailuser = ApplicationUser.builder()
            .username("generic username")
            .firstName("generic firstname")
            .lastName("generic lastname")
            .email("generic@email.com")
            .password("generic password")
            .country("generic country")
            .city("generic city")
            .zipCode("generic zipCode")
            .street("generic street")
            .build();

        userRepository.save(emailuser);
        userRepository.save(user);
        updateUser.setEmail("generic@email.com");
        UserUpdateDto userUpdateDto = userMapper.applicationUserToUserUpdateDto(updateUser);
        String body = objectMapper.writeValueAsString(userUpdateDto);

        MvcResult mvcResult = this.mockMvc.perform(patch(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    }

    @Test
    public void givenUser_whenPatchingWithValidEmail_thenUserDetailsWithNonAdministrativeInformationAndChangedEmail()
        throws Exception {
        userRepository.save(user);

        updateUser.setEmail("i.gotAnew@email.tuwien.ac.at");
        UserUpdateDto userUpdateDto = userMapper.applicationUserToUserUpdateDto(updateUser);
        String body = objectMapper.writeValueAsString(userUpdateDto);

        MvcResult mvcResult = this.mockMvc.perform(patch(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        UserDetailsDto userDetailsDto = objectMapper.readValue(response.getContentAsString(), UserDetailsDto.class);
        assertThat(userDetailsDto.getUsername()).isEqualTo(DEFAULT_USER_USERNAME);
        assertThat(userDetailsDto.getFirstName()).isEqualTo(DEFAULT_USER_FIRSTNAME);
        assertThat(userDetailsDto.getLastName()).isEqualTo(DEFAULT_USER_LASTNAME);
        assertThat(userDetailsDto.getEmail()).isEqualTo(userUpdateDto.getEmail());
        assertThat(userDetailsDto.getCountry()).isEqualTo(DEFAULT_USER_COUNTRY);
        assertThat(userDetailsDto.getCity()).isEqualTo(DEFAULT_USER_CITY);
        assertThat(userDetailsDto.getZipCode()).isEqualTo(DEFAULT_ZIP_CODE);
        assertThat(userDetailsDto.getStreet()).isEqualTo(DEFAULT_STREET);
    }

    @Test
    public void givenuser_whenUpdatingWithValidAddress_thenUserDetailWithNonAdministrativeInformationAndChangedAddress()
        throws Exception {
        userRepository.save(user);

        updateUser.setCountry("Swiss");
        updateUser.setCity("Zuerich");
        updateUser.setZipCode(DEFAULT_ZIP_CODE);
        updateUser.setStreet(DEFAULT_STREET);
        UserUpdateDto userUpdateDto = userMapper.applicationUserToUserUpdateDto(updateUser);
        String body = objectMapper.writeValueAsString(userUpdateDto);

        MvcResult mvcResult = this.mockMvc.perform(patch(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        UserDetailsDto userDetailsDto = objectMapper.readValue(response.getContentAsString(), UserDetailsDto.class);
        assertThat(userDetailsDto.getUsername()).isEqualTo(DEFAULT_USER_USERNAME);
        assertThat(userDetailsDto.getFirstName()).isEqualTo(DEFAULT_USER_FIRSTNAME);
        assertThat(userDetailsDto.getLastName()).isEqualTo(DEFAULT_USER_LASTNAME);
        assertThat(userDetailsDto.getEmail()).isEqualTo(DEFAULT_USER_EMAIL);
        assertThat(userDetailsDto.getCountry()).isEqualTo(userUpdateDto.getCountry());
        assertThat(userDetailsDto.getCity()).isEqualTo(userUpdateDto.getCity());
        assertThat(userDetailsDto.getZipCode()).isEqualTo(DEFAULT_ZIP_CODE);
        assertThat(userDetailsDto.getStreet()).isEqualTo(DEFAULT_STREET);
    }


    @Test
    public void givenUser_whenUpdatingWithInvalidAddress_then422() throws Exception {
        userRepository.save(user);

        updateUser.setCity("");
        UserUpdateDto userUpdateDto = userMapper.applicationUserToUserUpdateDto(updateUser);
        String body = objectMapper.writeValueAsString(userUpdateDto);

        MvcResult mvcResult = this.mockMvc.perform(patch(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenUser_whenUpdatingWithInvalidPassword_then422() throws Exception {
        updateUser.setPassword("1234567");
        UserUpdateDto userUpdateDto = userMapper.applicationUserToUserUpdateDto(updateUser);
        String body = objectMapper.writeValueAsString(userUpdateDto);

        MvcResult mvcResult = this.mockMvc.perform(patch(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenUser_whenUpdatingWithInvalidPassword2_then422() throws Exception {
        updateUser.setPassword("                ");
        UserUpdateDto userUpdateDto = userMapper.applicationUserToUserUpdateDto(updateUser);
        String body = objectMapper.writeValueAsString(userUpdateDto);

        MvcResult mvcResult = this.mockMvc.perform(patch(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenUser_whenUpdatingWithInvalidPassword3_then422() throws Exception {
        updateUser.setPassword("");
        UserUpdateDto userUpdateDto = userMapper.applicationUserToUserUpdateDto(updateUser);
        String body = objectMapper.writeValueAsString(userUpdateDto);

        MvcResult mvcResult = this.mockMvc.perform(patch(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void givenUser_whenUpdatingWithValidPassword_thenTrue() throws Exception {
        userRepository.save(user);

        updateUser.setPassword("myNewPasswordpg");
        UserUpdateDto userUpdateDto = userMapper.applicationUserToUserUpdateDto(updateUser);
        String body = objectMapper.writeValueAsString(userUpdateDto);

        MvcResult mvcResult = this.mockMvc.perform(patch(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        UserDetailsDto resp = objectMapper.readValue(response.getContentAsString(), UserDetailsDto.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
    }

    @Test
    public void givenUser_whenRegisteringWithValidData_thenUserIsPersisted() throws Exception {
        UserRegistrationDto userRegistrationDto = defaultRegistrationDto();
        String body = objectMapper.writeValueAsString(userRegistrationDto);
        MvcResult mvcResult = mockMvc.perform(post(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
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
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername(DEFAULT_USER_USERNAME);
        loginDto.setPassword(DEFAULT_USER_PASSWORD);
        String loginBody = objectMapper.writeValueAsString(loginDto);
        mvcResult = mockMvc.perform(post(AUTHENTICATION_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(200);

    }

    @Test
    public void givenUser_whenRegisteringWithBlankUsername_thenReturnsValidationError() throws Exception {
        UserRegistrationDto userRegistrationDto = defaultRegistrationDto();
        userRegistrationDto.setUsername("                   ");
        String body = objectMapper.writeValueAsString(userRegistrationDto);
        MvcResult mvcResult = mockMvc.perform(post(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(400);

    }

    @Test
    public void givenUser_whenRegisteringTwiceWithIdenticalUsername_thenReturnsConflictError() throws Exception {
        UserRegistrationDto userRegistrationDto = defaultRegistrationDto();
        String body = objectMapper.writeValueAsString(userRegistrationDto);
        MvcResult mvcResult = mockMvc.perform(post(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(201);
        userRegistrationDto.setEmail("new@mail.com");
        body = objectMapper.writeValueAsString(userRegistrationDto);
        mvcResult = mockMvc.perform(post(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(409);
        assertThat(response.getContentAsString().contains("Username already taken")).isTrue();
    }

    @Test
    public void givenUser_whenRegisteringTwiceWithIdenticalEmail_thenReturnsConflictError() throws Exception {
        UserRegistrationDto userRegistrationDto = defaultRegistrationDto();
        String body = objectMapper.writeValueAsString(userRegistrationDto);
        MvcResult mvcResult = mockMvc.perform(post(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(201);
        userRegistrationDto.setUsername("NewUser");
        body = objectMapper.writeValueAsString(userRegistrationDto);
        mvcResult = mockMvc.perform(post(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(409);
        assertThat(response.getContentAsString().contains("User with given email already exists")).isTrue();
    }

    @Test
    public void givenUser_whenRegisteringWithInvalidEmail_thenReturnsValidationError() throws Exception {
        UserRegistrationDto userRegistrationDto = defaultRegistrationDto();
        userRegistrationDto.setEmail("Invalid");
        String body = objectMapper.writeValueAsString(userRegistrationDto);
        MvcResult mvcResult = mockMvc.perform(post(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(400);

    }

    @Test
    public void givenUser_whenRegisteringWithTooShortCredentials_thenReturnsValidationError() throws Exception {
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultRegistrationDto().setUsername("A")))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultRegistrationDto().setPassword("A")))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultRegistrationDto().setFirstName("A")))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultRegistrationDto().setLastName("A")))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultRegistrationDto().setCountry("A")))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultRegistrationDto().setCity("A")))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultRegistrationDto().setUsername("A")))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultRegistrationDto().setStreet("A")))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultRegistrationDto().setZipCode("A")))
            .getStatus())
            .isEqualTo(400);

    }

    @Test
    public void givenUser_whenRegisteringWithTooLongCredentials_thenReturnsValidationError() throws Exception {
        String tooLong = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultRegistrationDto().setUsername(tooLong)))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultRegistrationDto().setFirstName(tooLong)))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultRegistrationDto().setLastName(tooLong)))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultRegistrationDto().setCountry(tooLong)))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultRegistrationDto().setCity(tooLong)))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultRegistrationDto().setUsername(tooLong)))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultRegistrationDto().setStreet(tooLong)))
            .getStatus())
            .isEqualTo(400);
        assertThat(makeMvcRequest(objectMapper.writeValueAsString(defaultRegistrationDto().setZipCode(tooLong)))
            .getStatus())
            .isEqualTo(400);

    }

    @Test
    public void givenValidToken_WhenResettingPassword_ThenPasswordShouldBeReset() throws Exception {
        String token = UUID.randomUUID().toString();
        userRepository.saveAndFlush(user);
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.saveAndFlush(passwordResetToken);
        String password = "newPassword";
        UserResetPasswordDto userResetPasswordDto = new UserResetPasswordDto(password, token);

        String body = objectMapper.writeValueAsString(userResetPasswordDto);
        MvcResult mvcResult = mockMvc.perform(put(CUSTOMER_BASE_URI + "/resetPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(200);

        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername(DEFAULT_USER_USERNAME);
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

    @Test
    public void givenUser_WhenResettingPasswordWithNonExistingToken_ThenReturns404() throws Exception {
        String token = UUID.randomUUID().toString();
        userRepository.saveAndFlush(user);
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.saveAndFlush(passwordResetToken);
        String password = "newPassword";
        token += "BLAAAA";
        UserResetPasswordDto userResetPasswordDto = new UserResetPasswordDto(password, token);

        String body = objectMapper.writeValueAsString(userResetPasswordDto);
        MvcResult mvcResult = mockMvc.perform(put(CUSTOMER_BASE_URI + "/resetPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(404);
    }

    //@Test
    public void givenUser_WhenRequestingResetAndThenResettingWithValidToken_ThenPasswordShouldBeReset() throws Exception {
        userRepository.saveAndFlush(user);


        MvcResult mvcResult = mockMvc.perform(post(CUSTOMER_BASE_URI + "/requestReset/" + user.getUsername()))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(200);

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findPasswordResetTokenByUser(user);
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
        loginDto.setUsername(DEFAULT_USER_USERNAME);
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

    //@Test
    public void givenUser_WhenRequestingResetAndThenResettingWithValidButExpiredToken_ThenReturns410() throws Exception {
        userRepository.saveAndFlush(user);


        MvcResult mvcResult = mockMvc.perform(post(CUSTOMER_BASE_URI + "/requestReset/" + user.getUsername()))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(200);

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findPasswordResetTokenByUser(user);
        assertThat(passwordResetToken).isNotNull();
        passwordResetToken.setExpiryDate(LocalDateTime.now().minusDays(1));
        passwordResetTokenRepository.saveAndFlush(passwordResetToken);
        String password = "newPassword";
        UserResetPasswordDto userResetPasswordDto = new UserResetPasswordDto(password, passwordResetToken.getToken());

        String body = objectMapper.writeValueAsString(userResetPasswordDto);
        mvcResult = mockMvc.perform(put(CUSTOMER_BASE_URI + "/resetPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(410);
    }

    //@Test
    public void givenUser_WhenRequestingResetAndThenResettingWithValidTokenButTwice_ThenReturns200And404() throws Exception {
        userRepository.saveAndFlush(user);

        MvcResult mvcResult = mockMvc.perform(post(CUSTOMER_BASE_URI + "/requestReset/" + user.getUsername()))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(200);

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findPasswordResetTokenByUser(user);
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

        mvcResult = mockMvc.perform(put(CUSTOMER_BASE_URI + "/resetPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(404);


    }


    private MockHttpServletResponse makeMvcRequest(String body) throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        return mvcResult.getResponse();
    }

    @Test
    public void givenUser_whenDeletingUser_then400() throws Exception {
        userRepository.save(user);

        MvcResult mvcResult = this.mockMvc.perform(delete(CUSTOMER_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void givenNothing_whenDeletingNonExistentUser_then404() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(delete(CUSTOMER_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(user.getUsername(), USER_ROLES)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void givenUser_whenRegisteringTwiceWithIdenticalUsernameButDifferentLetterCases_thenReturnsConflictError() throws Exception {
        UserRegistrationDto userRegistrationDto = defaultRegistrationDto();
        String body = objectMapper.writeValueAsString(userRegistrationDto);
        MvcResult mvcResult = mockMvc.perform(post(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(201);
        userRegistrationDto.setUsername(DEFAULT_USER_USERNAME.toLowerCase());
        userRegistrationDto.setEmail("new@mail.com");
        body = objectMapper.writeValueAsString(userRegistrationDto);
        mvcResult = mockMvc.perform(post(CUSTOMER_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(409);
        assertThat(response.getContentAsString().contains("Username already taken")).isTrue();
    }

}
