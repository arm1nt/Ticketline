package at.ac.tuwien.sepm.groupphase.backend.service.impl;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserResetPasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserUpdatePasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.PasswordResetToken;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.SendingMailFailedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.TokenExpiredException;
import at.ac.tuwien.sepm.groupphase.backend.repository.PasswordResetTokenRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String SEPM_EMAIL = "ticketline05inso@outlook.com";
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final UserMapper mapper;
    private final JavaMailSender mailSender;
    private final Environment env;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenizer jwtTokenizer,
                           UserMapper mapper,
                           JavaMailSender mailSender,
                           Environment env) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.mapper = mapper;
        this.mailSender = mailSender;
        this.env = env;
    }


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        LOGGER.trace("loadUserByUsername({})", userName);

        try {
            ApplicationUser applicationUser = findApplicationUserByUsername(userName);

            List<GrantedAuthority> grantedAuthorities;
            if (applicationUser.isAdmin()) {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
            } else {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");
            }
            return new User(
                applicationUser.getUsername(),
                applicationUser.getPassword(),
                true,
                true,
                true,
                !applicationUser.isLocked(),
                grantedAuthorities);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }


    @Override
    public ApplicationUser findApplicationUserByEmail(String email) {
        LOGGER.trace("findApplicationUserByEmail({})", email);

        ApplicationUser applicationUser = userRepository.findApplicationUserByEmail(email);
        if (applicationUser != null) {
            return applicationUser;
        }
        throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
    }


    @Override
    public ApplicationUser findApplicationUserByUsername(String username) {
        LOGGER.trace("findApplicationUserByUsername({})", username);

        ApplicationUser applicationUser = userRepository.findApplicationUserByUsernameIgnoreCase(username);
        if (applicationUser != null) {
            return applicationUser;
        }
        throw new NotFoundException(String.format("Could not find the user with the username: %s", username));
    }

    @Transactional
    @Override
    public ApplicationUser updateApplicationUser(String username, UserUpdateDto userUpdateDto) {
        LOGGER.trace("updateApplicationUser({}, {})", username, userUpdateDto);

        ApplicationUser applicationUser = userRepository.findApplicationUserByUsernameIgnoreCase(username);
        if (applicationUser == null) {
            throw new NotFoundException(String.format("Could not find the user with the username: %s", username));
        }

        if (userUpdateDto.getFirstName() != null) {
            applicationUser = updateApplicationUserFirstname(username, userUpdateDto.getFirstName());
        } else if (userUpdateDto.getLastName() != null) {
            applicationUser = updateApplicationUserLastname(username, userUpdateDto.getLastName());
        } else if (userUpdateDto.getEmail() != null) {
            if (applicationUser.getEmail().equals(userUpdateDto.getEmail())) {
                return applicationUser;
            }
            if (userRepository.findApplicationUserByEmail(userUpdateDto.getEmail()) != null) {
                throw new ConflictException("There already exists an user with given email");
            }
            applicationUser = updateApplicationUserEmail(username, userUpdateDto.getEmail());
        } else if (userUpdateDto.getPassword() != null) {
            applicationUser = updateApplicationUserPassword(username, userUpdateDto.getPassword());
        } else if (userUpdateDto.getCountry() != null || userUpdateDto.getCity() != null
            || userUpdateDto.getZipCode() != null || userUpdateDto.getStreet() != null) {
            String country = (userUpdateDto.getCountry() == null) ? applicationUser.getCountry() : userUpdateDto.getCountry();
            String city = (userUpdateDto.getCity() == null) ? applicationUser.getCity() : userUpdateDto.getCity();
            String zipCode = (userUpdateDto.getZipCode() == null) ? applicationUser.getZipCode() : userUpdateDto.getZipCode();
            String street = (userUpdateDto.getStreet() == null) ? applicationUser.getStreet() : userUpdateDto.getStreet();
            applicationUser = updateApplicationUserAddress(username, country, city, zipCode, street);
        }

        return applicationUser;
    }


    @Override
    public boolean checkIfPasswordMatches(String username, UserUpdatePasswordDto password) {
        LOGGER.trace("checkIfPasswordMatches({})", username);

        ApplicationUser applicationUser = userRepository.findApplicationUserByUsernameIgnoreCase(username);
        if (applicationUser == null) {
            throw new NotFoundException(String.format("Could not find the user with the username: %s", username));
        }
        String storedPassword = applicationUser.getPassword();

        return passwordEncoder.matches(password.getPassword(), storedPassword);
    }

    @Transactional
    @Override
    public void deleteUser(String username) {
        LOGGER.debug("Delete user");
        ApplicationUser user = findApplicationUserByUsername(username);
        if (user != null) {
            user.setUsername("DEL_USER_" + user.getUsername());
            user.setDeleted(true);
            userRepository.save(user);
        } else {
            throw new NotFoundException(String.format("Could not find the user with the username: %s", username));
        }
    }

    @Override
    @Transactional(noRollbackFor = BadCredentialsException.class)
    public String login(UserLoginDto userLoginDto) {
        LOGGER.trace("login()");

        try {
            UserDetails userDetails = loadUserByUsername(userLoginDto.getUsername());
            ApplicationUser user = findApplicationUserByUsername(userLoginDto.getUsername());

            if (userDetails != null
                && userDetails.isAccountNonExpired()
                && userDetails.isAccountNonLocked()
                && userDetails.isCredentialsNonExpired()
                && !user.isDeleted()
                && passwordEncoder.matches(userLoginDto.getPassword(), userDetails.getPassword())
            ) {
                user.setFailedLoginAttempts(0);
                userRepository.save(user);
                List<String> roles = userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
                return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
            }
            LOGGER.debug("Failed login attempt for user {}", user.getUsername());
            if (!user.isAdmin()) {
                user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
                userRepository.saveAndFlush(user);
                if (user.getFailedLoginAttempts() >= 5) {
                    if (!user.isLocked()) {
                        user.setLocked(true);
                        userRepository.save(user);
                        LOGGER.debug("Account of user {} just got locked!", user.getUsername());
                        sendEmailToLockedUser(user);
                    }
                    LOGGER.debug("User {} tried logging in while account was locked", user.getUsername());
                }
            }
            throw new BadCredentialsException("Username or password is incorrect or account is locked");
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("Username or password is incorrect or account is locked");
        }
    }

    @Transactional
    @Override
    public ApplicationUser register(UserRegistrationDto userRegistrationDto) {
        LOGGER.trace("register()");

        userRegistrationDto.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        if (userRepository.findApplicationUserByUsernameIgnoreCase(userRegistrationDto.getUsername()) != null) {
            throw new ConflictException("Username already taken!");
        }
        if (userRepository.findApplicationUserByEmail(userRegistrationDto.getEmail()) != null) {
            throw new ConflictException("User with given email already exists!");
        }
        ApplicationUser newUser = mapper.userRegistrationDtoToUser(userRegistrationDto);
        return userRepository.save(newUser);
    }

    @Transactional
    @Override
    public void generatePasswordResetTokenForUser(ApplicationUser user, String token) {
        LOGGER.trace("generatePasswordResetTokenForUser({})", user.getUsername());

        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.saveAndFlush(passwordResetToken);
        sendResetEmailToUser(user, token);
    }

    @Override
    public void generatePasswordResetTokenForUser(String username, String token) {
        LOGGER.trace("generatePasswordResetTokenForUser({}) with username", username);

        try {
            ApplicationUser user = findApplicationUserByUsername(username);

            PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
            passwordResetTokenRepository.saveAndFlush(passwordResetToken);
            sendResetEmailToUser(user, token);
        } catch (NotFoundException ignored) {

        }
    }


    @Transactional
    @Override
    public void resetPasswordForUser(UserResetPasswordDto userResetPasswordDto) {
        LOGGER.trace("resetPasswordForUser({})", userResetPasswordDto.getToken());

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findPasswordResetTokenByToken(userResetPasswordDto.getToken());

        if (passwordResetToken == null) {
            throw new NotFoundException("Invalid reset link!");
        }

        if (LocalDateTime.now().isAfter(passwordResetToken.getExpiryDate())) {
            throw new TokenExpiredException("The password reset link is expired!");
        }

        ApplicationUser user = passwordResetToken.getUser();
        String encodedPassword = passwordEncoder.encode(userResetPasswordDto.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        passwordResetTokenRepository.delete(passwordResetToken);
    }

    @Override
    public boolean isTokenValid(String token) {
        LOGGER.trace("isTokenValid({})", token);

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findPasswordResetTokenByToken(token);

        if (passwordResetToken == null) {
            throw new NotFoundException("Invalid reset link!");
        }

        return !LocalDateTime.now().isAfter(passwordResetToken.getExpiryDate());
    }


    private void sendResetEmailToUser(ApplicationUser user, String token) {
        LOGGER.trace("sendResetEmailToUser({}, {})", user.getUsername(), token);

        String subject = "Your password reset email";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            message.setFrom(SEPM_EMAIL);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
            message.setSubject(subject);
            String hostname = null;
            String[] activeProfiles = this.env.getActiveProfiles();
            if (Arrays.asList(activeProfiles).contains("dev")) {
                hostname = "http://localhost:4200/#/";
            } else {
                hostname = "https://22ws-sepm-pr-inso-05-nsceklfwmn01-msroniay.apps.student.inso-w.at/#/";
            }

            String content =
                """
                    Hello [[name]],
                    You or an admin have requested to reset your password.
                    Please click this link to set a new password: [[link]]
                    The link is valid for 30 minutes. If you have not requested a password reset, you can ignore this e-mail.
                    """;
            content = content.replace("[[name]]", user.getFirstName());
            content = content.replace("[[link]]", "<a href=" + hostname + "reset-password?token=" + token + ">Reset password</a>");

            message.setContent(content, "text/html");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new SendingMailFailedException(e.getMessage(), e);
        }

    }


    private ApplicationUser updateApplicationUserFirstname(String username, String firstname) {
        LOGGER.trace("updateApplicationUserFirstname({}, {})", username, firstname);

        int affectedRow = userRepository.updateFirstName(username, firstname);

        if (affectedRow == 1) { //Username muss unique sein
            ApplicationUser applicationUser = userRepository.findApplicationUserByUsernameIgnoreCase(username);
            if (applicationUser != null) {
                return applicationUser;
            }
            throw new NotFoundException(String.format("Could not find the user with the username: %s", username));
        }
        return null;
    }

    private ApplicationUser updateApplicationUserLastname(String username, String lastname) {
        LOGGER.trace("updateApplicationUserLastname({}, {})", username, lastname);

        int affectedRow = userRepository.updateLastName(username, lastname);

        if (affectedRow == 1) {
            ApplicationUser applicationUser = userRepository.findApplicationUserByUsernameIgnoreCase(username);
            if (applicationUser != null) {
                return applicationUser;
            }
            throw new NotFoundException(String.format("Could not find the user with the username: %s", username));
        }
        return null;
    }

    private ApplicationUser updateApplicationUserEmail(String username, String email) {
        LOGGER.trace("updateApplicationUserEmail({}, {})", username, email);

        int affectedRow = userRepository.updateEmail(username, email);

        if (affectedRow == 1) {
            ApplicationUser applicationUser = userRepository.findApplicationUserByUsernameIgnoreCase(username);
            if (applicationUser != null) {
                return applicationUser;
            }
            throw new NotFoundException(String.format("Could not find the user with the username: %s", username));
        }
        return null;
    }

    private ApplicationUser updateApplicationUserAddress(String username, String country,
                                                         String city, String zipCode,
                                                         String street) {
        LOGGER.trace("updateApplicationUserAddress({}, {}, {}, {}, {})", username, country,
            city, zipCode, street);

        int affectedRow = userRepository.updateAddress(username, country, city,
            zipCode, street);

        if (affectedRow == 1) {
            ApplicationUser applicationUser = userRepository.findApplicationUserByUsernameIgnoreCase(username);
            if (applicationUser != null) {
                return applicationUser;
            }
            throw new NotFoundException(String.format("Could not find the user with the username: %s", username));
        }
        return null;
    }

    private ApplicationUser updateApplicationUserPassword(String username, String password) {
        LOGGER.trace("updateApplicationUserPassword({})", username);

        String hashedPassword = passwordEncoder.encode(password);
        int affectedRow = userRepository.updatePassword(username, hashedPassword);

        if (affectedRow == 1) {
            ApplicationUser applicationUser = userRepository.findApplicationUserByUsernameIgnoreCase(username);
            if (applicationUser != null) {
                return applicationUser;
            }
            throw new NotFoundException(String.format("Could not find the user with the username: %s", username));
        }
        return null;
    }

    private void sendEmailToLockedUser(ApplicationUser user) {
        LOGGER.trace("sendEmailToLockedUser({})", user.getUsername());

        String subject = "Your account has been locked";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(SEPM_EMAIL);
        message.setTo(user.getEmail());
        message.setSubject(subject);

        String content = """
            Hello [[name]],
            You have had 5 failed login attempts. Therefore, your account has been locked.
            If you want your account, please reply to this e-mail with a request to get your account unlocked.""";
        content = content.replace("[[name]]", user.getFirstName());

        message.setText(content);

        mailSender.send(message);

    }
}
