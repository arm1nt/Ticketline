package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserShortDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.AdminService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.lang.invoke.MethodHandles;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String SEPM_EMAIL = "ticketline05inso@outlook.com";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final UserMapper mapper;
    private final UserService userService;

    public AdminServiceImpl(UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            JavaMailSender mailSender,
                            UserMapper mapper,
                            UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.mapper = mapper;
        this.userService = userService;
    }

    @Transactional
    @Override
    public ApplicationUser createUser(UserCreationDto userCreationDto) {
        LOGGER.trace("createUser({})", userCreationDto);

        if (userRepository.findApplicationUserByUsernameIgnoreCase(userCreationDto.getUsername()) != null) {
            throw new ConflictException("Username already taken!");
        }
        if (userRepository.findApplicationUserByEmail(userCreationDto.getEmail()) != null) {
            throw new ConflictException("User with given email already exists!");
        }
        ApplicationUser user = mapper.userCreationDtoToUser(userCreationDto);
        String password = RandomStringUtils.randomAlphanumeric(10);
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
        sendEmailWithUserNameAndPassword(userCreationDto, password);
        return userRepository.save(user);

    }

    @Override
    public List<ApplicationUser> findLockedUsers() {
        LOGGER.trace("findLockedUsers()");

        return userRepository.findApplicationUserByLockedIsTrue();
    }


    @Transactional
    @Override
    public void unlockUser(String userName) {
        LOGGER.trace("unlockUser({})", userName);

        ApplicationUser applicationUser = userRepository.findApplicationUserByUsernameIgnoreCase(userName);
        if (applicationUser == null) {
            throw new NotFoundException(String.format("Could not find the user with the username: %s", userName));
        }
        applicationUser.setLocked(false);
        userRepository.save(applicationUser);
    }


    @Override
    public Page<UserShortDto> getAllNonLockedCustomers(int page, int size) {
        LOGGER.trace("GetAllNonLockedCustomers({}, {})", page, size);

        return userRepository.findAllNonLockedCustomersOrderedByUsername(PageRequest.of(page, size)).map(mapper::userToUserShortDto);
    }

    @Transactional
    @Override
    public void lockUser(String userName) {
        LOGGER.trace("lockUser({})", userName);

        ApplicationUser applicationUser = userRepository.findApplicationUserByUsernameIgnoreCase(userName);
        if (applicationUser == null) {
            throw new NotFoundException(String.format("Could not find the user with the username: %s", userName));
        }

        if (applicationUser.isAdmin()) {
            throw new ForbiddenException("An admin account can not be locked!");
        }

        applicationUser.setLocked(true);
        userRepository.save(applicationUser);
    }

    @Override
    public void generateResetPasswordTokenForUser(String username, String token) throws NotFoundException {
        LOGGER.trace("generateResetPasswordTokenForUser({})", username);

        ApplicationUser user = userRepository.findApplicationUserByUsernameIgnoreCase(username);
        if (user == null) {
            throw new NotFoundException(String.format("User with username %s not found", username));
        }
        userService.generatePasswordResetTokenForUser(user, token);
    }


    private void sendEmailWithUserNameAndPassword(UserCreationDto userCreationDto, String password) {
        LOGGER.trace("sendEmailWithUserNameAndPassword({})", userCreationDto);

        String subject = "Your login credentials for Ticketline";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(SEPM_EMAIL);
        message.setTo(userCreationDto.getEmail());
        message.setSubject(subject);

        String content = """
            Hello [[name]],
            Your username: [[username]]\s
            Your password: [[password]]""";
        content = content.replace("[[name]]", userCreationDto.getFirstName());
        content = content.replace("[[username]]", userCreationDto.getUsername());
        content = content.replace("[[password]]", password);

        message.setText(content);

        mailSender.send(message);

    }
}
