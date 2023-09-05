package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;

@Profile({"generateData"})
@Component("UserDataGenerator")
public class UserDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final int NUMBER_OF_USERS_TO_GENERATE = 900;
    private static final int NUMBER_OF_LOCKED_USERS_TO_GENERATE = 50;
    private static final String TEST_USER_USERNAME = "Username";
    private static final String TEST_USER_FIRSTNAME = "Firstname";
    private static final String TEST_USER_LASTNAME = "Lastname";
    private static final String TEST_USER_EMAIL = "user";
    private static final String TEST_USER_PASSWORD = "password";
    private static final String TEST_USER_COUNTRY = "Country";
    private static final String TEST_USER_CITY = "City";
    private static final String TEST_USER_ZIPCODE = "101";
    private static final String TEST_USER_STREET = "Street";

    private static final int NUMBER_OF_ADMINS_TO_GENERATE = 50;
    private static final String TEST_ADMIN_USERNAME = "AdminUsername";
    private static final String TEST_ADMIN_FIRSTNAME = "AdminFirstname";
    private static final String TEST_ADMIN_LASTNAME = "AdminLastname";
    private static final String TEST_ADMIN_EMAIL = "admin";
    private static final String TEST_ADMIN_PASSWORD = "password";
    private static final String TEST_ADMIN_COUNTRY = "Country";
    private static final String TEST_ADMIN_CITY = "City";
    private static final String TEST_ADMIN_ZIPCODE = "101";
    private static final String TEST_ADMIN_STREET = "Street";


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDataGenerator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    private void generateUser() {

        if (userRepository.findAll().size() == 0) {

            LOGGER.debug("generation {} user entries", NUMBER_OF_USERS_TO_GENERATE);
            for (int i = 0; i < NUMBER_OF_USERS_TO_GENERATE; i++) {
                ApplicationUser user = ApplicationUser.builder()
                    .username(TEST_USER_USERNAME + "" + i)
                    .firstName(TEST_USER_FIRSTNAME + "" + i)
                    .lastName(TEST_USER_LASTNAME + "" + i)
                    .email(TEST_USER_EMAIL + "" + i + "@email.com")
                    .password(passwordEncoder.encode(TEST_USER_PASSWORD))
                    .country(TEST_USER_COUNTRY + "" + i)
                    .city(TEST_USER_CITY + "" + i)
                    .zipCode(TEST_USER_ZIPCODE + "" + i)
                    .street(TEST_USER_STREET + "" + i)
                    .build();
                LOGGER.debug("saving user {}", user);
                userRepository.save(user);
            }
            for (int i = 0; i < NUMBER_OF_ADMINS_TO_GENERATE; i++) {
                ApplicationUser admin = ApplicationUser.builder()
                    .username(TEST_ADMIN_USERNAME + "" + i)
                    .firstName(TEST_ADMIN_FIRSTNAME + "" + i)
                    .lastName(TEST_ADMIN_LASTNAME + "" + i)
                    .email(TEST_ADMIN_EMAIL + "" + i + "@email.com")
                    .password(passwordEncoder.encode(TEST_ADMIN_PASSWORD))
                    .country(TEST_ADMIN_COUNTRY + "" + i)
                    .city(TEST_ADMIN_CITY + "" + i)
                    .zipCode(TEST_ADMIN_ZIPCODE + "" + i)
                    .street(TEST_ADMIN_STREET + "" + i)
                    .admin(true)
                    .build();
                LOGGER.debug("saving admin {}", admin);
                userRepository.save(admin);
            }

            for (int i = 900; i < NUMBER_OF_LOCKED_USERS_TO_GENERATE + 900; i++) {
                ApplicationUser user = ApplicationUser.builder()
                    .username(TEST_USER_USERNAME + "" + i)
                    .firstName(TEST_USER_FIRSTNAME + "" + i)
                    .lastName(TEST_USER_LASTNAME + "" + i)
                    .email(TEST_USER_EMAIL + "" + i + "@email.com")
                    .locked(true)
                    .password(passwordEncoder.encode(TEST_USER_PASSWORD))
                    .country(TEST_USER_COUNTRY + "" + i)
                    .city(TEST_USER_CITY + "" + i)
                    .zipCode(TEST_USER_ZIPCODE + "" + i)
                    .street(TEST_USER_STREET + "" + i)
                    .build();
                LOGGER.debug("saving locked user {}", user);
                userRepository.save(user);
            }
        }

    }
}
