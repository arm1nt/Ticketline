package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest implements TestData {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    TestEntityManager entityManager;

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
        .build();*/

    @BeforeEach
    public void beforeEach() {
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
    }

    @Test
    public void givenNothing_whenGetUser_thenGetSingleApplicationUser() {
        userRepository.save(user);

        ApplicationUser applicationUser = userRepository.findApplicationUserByUsernameIgnoreCase(DEFAULT_USER_USERNAME);

        assertAll(
            () -> assertEquals(DEFAULT_USER_USERNAME, applicationUser.getUsername()),
            () -> assertEquals(DEFAULT_USER_FIRSTNAME, applicationUser.getFirstName()),
            () -> assertEquals(DEFAULT_USER_LASTNAME, applicationUser.getLastName()),
            () -> assertEquals(DEFAULT_USER_EMAIL, applicationUser.getEmail()),
            () -> assertEquals(DEFAULT_USER_PASSWORD, applicationUser.getPassword()),
            () -> assertEquals(DEFAULT_USER_COUNTRY, applicationUser.getCountry()),
            () -> assertEquals(DEFAULT_USER_CITY, applicationUser.getCity()),
            () -> assertEquals(DEFAULT_ZIP_CODE, applicationUser.getZipCode()),
            () -> assertEquals(DEFAULT_STREET, applicationUser.getStreet())
        );
    }

    @Test
    public void givenNothing_whenUpdateFirstname_thenGetAsNumbersAffectedByOpertionOne() {
        //userRepository.save(user);
        entityManager.persist(user);
        int affected = userRepository.updateFirstName(DEFAULT_USER_USERNAME, "UPDATED FIRSTNAME");

        ApplicationUser applicationUser = userRepository.findApplicationUserByUsernameIgnoreCase(DEFAULT_USER_USERNAME);

        assertAll(
            () -> assertEquals(1, affected),
            () -> assertEquals(DEFAULT_USER_USERNAME, applicationUser.getUsername()),
            () -> assertEquals("UPDATED FIRSTNAME", applicationUser.getFirstName()),
            () -> assertEquals(DEFAULT_USER_LASTNAME, applicationUser.getLastName()),
            () -> assertEquals(DEFAULT_USER_EMAIL, applicationUser.getEmail()),
            () -> assertEquals(DEFAULT_USER_PASSWORD, applicationUser.getPassword()),
            () -> assertEquals(DEFAULT_USER_COUNTRY, applicationUser.getCountry()),
            () -> assertEquals(DEFAULT_USER_CITY, applicationUser.getCity()),
            () -> assertEquals(DEFAULT_ZIP_CODE, applicationUser.getZipCode()),
            () -> assertEquals(DEFAULT_STREET, applicationUser.getStreet())
        );
    }

    @Test
    public void givenNothing_whenUpdateLastname_thenGetAsNumbersAffectedByOperationOne() {
        userRepository.save(user);

        int affected = userRepository.updateLastName(DEFAULT_USER_USERNAME, "UPDATED LASTNAME");

        ApplicationUser applicationUser = userRepository.findApplicationUserByUsernameIgnoreCase(DEFAULT_USER_USERNAME);

        assertAll(
            () -> assertEquals(1, affected),
            () -> assertEquals(DEFAULT_USER_USERNAME, applicationUser.getUsername()),
            () -> assertEquals(DEFAULT_USER_FIRSTNAME, applicationUser.getFirstName()),
            () -> assertEquals("UPDATED LASTNAME", applicationUser.getLastName()),
            () -> assertEquals(DEFAULT_USER_EMAIL, applicationUser.getEmail()),
            () -> assertEquals(DEFAULT_USER_PASSWORD, applicationUser.getPassword()),
            () -> assertEquals(DEFAULT_USER_COUNTRY, applicationUser.getCountry()),
            () -> assertEquals(DEFAULT_USER_CITY, applicationUser.getCity()),
            () -> assertEquals(DEFAULT_ZIP_CODE, applicationUser.getZipCode()),
            () -> assertEquals(DEFAULT_STREET, applicationUser.getStreet())
        );
    }

    @Test
    public void givenNothing_whenUpdateEmail_thenGetAsNumberAffectedByOperationOne() {
        userRepository.save(user);

        int affected = userRepository.updateEmail(DEFAULT_USER_USERNAME, "UPDATED@EMAIL.TUWIEN.AC.AT");

        ApplicationUser applicationUser = userRepository.findApplicationUserByUsernameIgnoreCase(DEFAULT_USER_USERNAME);

        assertAll(
            () -> assertEquals(1, affected),
            () -> assertEquals(DEFAULT_USER_USERNAME, applicationUser.getUsername()),
            () -> assertEquals(DEFAULT_USER_FIRSTNAME, applicationUser.getFirstName()),
            () -> assertEquals(DEFAULT_USER_LASTNAME, applicationUser.getLastName()),
            () -> assertEquals("UPDATED@EMAIL.TUWIEN.AC.AT", applicationUser.getEmail()),
            () -> assertEquals(DEFAULT_USER_PASSWORD, applicationUser.getPassword()),
            () -> assertEquals(DEFAULT_USER_COUNTRY, applicationUser.getCountry()),
            () -> assertEquals(DEFAULT_USER_CITY, applicationUser.getCity()),
            () -> assertEquals(DEFAULT_ZIP_CODE, applicationUser.getZipCode()),
            () -> assertEquals(DEFAULT_STREET, applicationUser.getStreet())
        );
    }

    @Test
    public void givenNothing_whenUpdatePassword_thenGetAsNumberAffectedByOperationOne() {
        userRepository.save(user);

        int affected = userRepository.updatePassword(DEFAULT_USER_USERNAME, "UPDATED PASSWORD");

        ApplicationUser applicationUser = userRepository.findApplicationUserByUsernameIgnoreCase(DEFAULT_USER_USERNAME);

        assertAll(
            () -> assertEquals(1, affected),
            () -> assertEquals(DEFAULT_USER_USERNAME, applicationUser.getUsername()),
            () -> assertEquals(DEFAULT_USER_FIRSTNAME, applicationUser.getFirstName()),
            () -> assertEquals(DEFAULT_USER_LASTNAME, applicationUser.getLastName()),
            () -> assertEquals(DEFAULT_USER_EMAIL, applicationUser.getEmail()),
            () -> assertEquals("UPDATED PASSWORD", applicationUser.getPassword()),
            () -> assertEquals(DEFAULT_USER_COUNTRY, applicationUser.getCountry()),
            () -> assertEquals(DEFAULT_USER_CITY, applicationUser.getCity()),
            () -> assertEquals(DEFAULT_ZIP_CODE, applicationUser.getZipCode()),
            () -> assertEquals(DEFAULT_STREET, applicationUser.getStreet())
        );
    }

    @Test
    public void givenNothing_whenUpdateAddress_thenGetAsNumberAffectedByOperationOne() {
        userRepository.save(user);

        int affected = userRepository.updateAddress(DEFAULT_USER_USERNAME, DEFAULT_USER_COUNTRY, DEFAULT_USER_CITY,
            DEFAULT_ZIP_CODE, "UPDATE THE STREET");

        ApplicationUser applicationUser = userRepository.findApplicationUserByUsernameIgnoreCase(DEFAULT_USER_USERNAME);

        assertAll(
            () -> assertEquals(1, affected),
            () -> assertEquals(DEFAULT_USER_USERNAME, applicationUser.getUsername()),
            () -> assertEquals(DEFAULT_USER_FIRSTNAME, applicationUser.getFirstName()),
            () -> assertEquals(DEFAULT_USER_LASTNAME, applicationUser.getLastName()),
            () -> assertEquals(DEFAULT_USER_EMAIL, applicationUser.getEmail()),
            () -> assertEquals(DEFAULT_USER_PASSWORD, applicationUser.getPassword()),
            () -> assertEquals(DEFAULT_USER_COUNTRY, applicationUser.getCountry()),
            () -> assertEquals(DEFAULT_USER_CITY, applicationUser.getCity()),
            () -> assertEquals(DEFAULT_ZIP_CODE, applicationUser.getZipCode()),
            () -> assertEquals("UPDATE THE STREET", applicationUser.getStreet())
        );
    }

    @Test void givenUser_whenDeletingUser_thenNothing() {
        userRepository.save(user);

        userRepository.deleteApplicationUserByUsername(DEFAULT_USER_USERNAME);

        assertNull(userRepository.findApplicationUserByUsernameIgnoreCase(DEFAULT_USER_USERNAME));
    }



}
