package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserMapperTest implements TestData {

    @Autowired
    UserMapper userMapper;

    /*private final ApplicationUser user = ApplicationUser.ApplicationUserBuilder.aApplicationUser()
        .withUsername(DEFAULT_USER_USERNAME)
        .withFirstname(DEFAULT_USER_FIRSTNAME)
        .withLastname(DEFAULT_USER_LASTNAME)
        .withEmail(DEFAULT_USER_EMAIL)
        .withPassword(DEFAULT_USER_PASSWORD)
        .withCountry(DEFAULT_USER_COUNTRY)
        .withCity(DEFAULT_USER_CITY)
        .withZipcode(DEFAULT_ZIP_CODE)
        .build();*/

    private final ApplicationUser user = ApplicationUser.builder()
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

    @Test
    public void giveNothing_whenMapApplicationUserToUserDetailDto_thenDetailDtoHasAllNonAdministrativeData() {
        UserDetailsDto userDetailsDto = userMapper.applicationUserToUserDetailsDto(user);

        assertAll(
            () -> assertEquals(DEFAULT_USER_USERNAME, userDetailsDto.getUsername()),
            () -> assertEquals(DEFAULT_USER_FIRSTNAME, userDetailsDto.getFirstName()),
            () -> assertEquals(DEFAULT_USER_LASTNAME, userDetailsDto.getLastName()),
            () -> assertEquals(DEFAULT_USER_EMAIL, userDetailsDto.getEmail()),
            () -> assertEquals(DEFAULT_USER_COUNTRY, userDetailsDto.getCountry()),
            () -> assertEquals(DEFAULT_USER_CITY, userDetailsDto.getCity()),
            () -> assertEquals(DEFAULT_ZIP_CODE, userDetailsDto.getZipCode()),
            () -> assertEquals(DEFAULT_STREET, userDetailsDto.getStreet())
        );
    }

    @Test
    public void giveNothing_whenMapApplicationUserToUserUpdateDto_thenUserUpdateDtoHasAllDataThatCanBeUpdated() {
        UserUpdateDto userUpdateDto = userMapper.applicationUserToUserUpdateDto(user);

        assertAll(
            () -> assertEquals(DEFAULT_USER_FIRSTNAME, userUpdateDto.getFirstName()),
            () -> assertEquals(DEFAULT_USER_LASTNAME, userUpdateDto.getLastName()),
            () -> assertEquals(DEFAULT_USER_EMAIL, userUpdateDto.getEmail()),
            () -> assertEquals(DEFAULT_USER_PASSWORD, userUpdateDto.getPassword()),
            () -> assertEquals(DEFAULT_USER_COUNTRY, userUpdateDto.getCountry()),
            () -> assertEquals(DEFAULT_USER_CITY, userUpdateDto.getCity()),
            () -> assertEquals(DEFAULT_ZIP_CODE, userUpdateDto.getZipCode()),
            () -> assertEquals(DEFAULT_STREET, userUpdateDto.getStreet())
        );
    }

    @Test
    public void givenNothing_whenMapUserRegistrationDtoToUser_thenContainsAllFields() {
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
        ApplicationUser user = userMapper.userRegistrationDtoToUser(userRegistrationDto);
        assertAll(
            () -> assertEquals(DEFAULT_USER_USERNAME, user.getUsername()),
            () -> assertEquals(DEFAULT_USER_FIRSTNAME, user.getFirstName()),
            () -> assertEquals(DEFAULT_USER_LASTNAME, user.getLastName()),
            () -> assertEquals(DEFAULT_USER_EMAIL, user.getEmail()),
            () -> assertEquals(DEFAULT_USER_PASSWORD, user.getPassword()),
            () -> assertEquals(DEFAULT_USER_COUNTRY, user.getCountry()),
            () -> assertEquals(DEFAULT_USER_CITY, user.getCity()),
            () -> assertEquals(DEFAULT_ZIP_CODE, user.getZipCode()),
            () -> assertEquals(DEFAULT_STREET, user.getStreet()),
            () -> assertFalse(user.isAdmin()),
            () -> assertFalse(user.isLocked()),
            () -> assertEquals(0, user.getFailedLoginAttempts())

        );

    }

    @Test
    public void givenNothing_whenMapUserToUserDto_thenContainsAllFields() {
        user.setId(1);
        UserDto userDto = userMapper.userToUserDto(user);
        assertAll(
            () -> assertEquals(1, userDto.getId()),
            () -> assertEquals(DEFAULT_USER_USERNAME, userDto.getUsername()),
            () -> assertEquals(DEFAULT_USER_FIRSTNAME, userDto.getFirstName()),
            () -> assertEquals(DEFAULT_USER_LASTNAME, userDto.getLastName()),
            () -> assertEquals(DEFAULT_USER_EMAIL, userDto.getEmail()),
            () -> assertEquals(DEFAULT_USER_COUNTRY, userDto.getCountry()),
            () -> assertEquals(DEFAULT_USER_CITY, userDto.getCity()),
            () -> assertEquals(DEFAULT_ZIP_CODE, userDto.getZipCode()),
            () -> assertEquals(DEFAULT_STREET, userDto.getStreet())

        );
    }

    @Test
    public void givenNothing_whenMapUserCreationDtoToUser_thenContainsAllFields() {
        UserCreationDto userCreationDto = new UserCreationDto();
        userCreationDto.setEmail(DEFAULT_USER_EMAIL);
        userCreationDto.setUsername(DEFAULT_USER_USERNAME);
        userCreationDto.setAdmin(true);
        userCreationDto.setFirstName(DEFAULT_USER_FIRSTNAME);
        userCreationDto.setLastName(DEFAULT_USER_LASTNAME);
        userCreationDto.setStreet(DEFAULT_STREET);
        userCreationDto.setCountry(DEFAULT_USER_COUNTRY);
        userCreationDto.setCity(DEFAULT_USER_CITY);
        userCreationDto.setZipCode(DEFAULT_ZIP_CODE);
        ApplicationUser user = userMapper.userCreationDtoToUser(userCreationDto);
        assertAll(
            () -> assertEquals(DEFAULT_USER_USERNAME, user.getUsername()),
            () -> assertEquals(DEFAULT_USER_FIRSTNAME, user.getFirstName()),
            () -> assertEquals(DEFAULT_USER_LASTNAME, user.getLastName()),
            () -> assertEquals(DEFAULT_USER_EMAIL, user.getEmail()),
            () -> assertNull(user.getPassword()),
            () -> assertEquals(DEFAULT_USER_COUNTRY, user.getCountry()),
            () -> assertEquals(DEFAULT_USER_CITY, user.getCity()),
            () -> assertEquals(DEFAULT_ZIP_CODE, user.getZipCode()),
            () -> assertEquals(DEFAULT_STREET, user.getStreet()),
            () -> assertTrue(user.isAdmin()),
            () -> assertFalse(user.isLocked()),
            () -> assertEquals(0, user.getFailedLoginAttempts())

        );

    }
}
