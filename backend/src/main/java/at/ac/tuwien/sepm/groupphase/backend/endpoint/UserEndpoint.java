package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserResetPasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserUpdateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserUpdatePasswordDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.TokenExpiredException;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.lang.invoke.MethodHandles;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/customers")
public class UserEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final UserService userService;
    private final UserMapper userMapper;
    private final SecurityProperties securityProperties;

    @Autowired
    public UserEndpoint(UserService userService, UserMapper userMapper, SecurityProperties securityProperties) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.securityProperties = securityProperties;
    }

    @Secured("ROLE_USER")
    @GetMapping
    @Operation(summary = "Get detailed information about the user", security = @SecurityRequirement(name = "apiKey"))
    public UserDetailsDto getUser(@RequestHeader (name = "Authorization") String token) {
        LOGGER.info("GET /api/v1/customers");

        String username = this.retrieveUsername(token);
        return userMapper.applicationUserToUserDetailsDto(userService.findApplicationUserByUsername(username));
    }

    @Secured("ROLE_USER")
    @PatchMapping
    @Operation(summary = "Update information of the user", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<UserDetailsDto> updateUser(@RequestHeader(name = "Authorization") String token, @Valid @RequestBody UserUpdateDto update,
                                                     Errors errors) throws ValidationException {
        LOGGER.info("PATCH /api/v1/customers");
        LOGGER.debug("Request body: {}", update);

        if (errors.hasErrors()) {
            if (errors.getFieldError() != null) {
                throw new javax.validation.ValidationException(errors.getFieldError().getDefaultMessage());
            } else {
                throw new ValidationException();
            }
        }

        String username = this.retrieveUsername(token);
        return new ResponseEntity<>(userMapper.applicationUserToUserDetailsDto(userService.updateApplicationUser(username, update)), HttpStatus.OK);

    }

    @Secured("ROLE_USER")
    @PostMapping("/password")
    @Operation(summary = "Check if given password matches with the stored password of the user", security = @SecurityRequirement(name = "apiKey"))
    public boolean passwordMatches(@RequestHeader(name = "Authorization") String token, @RequestBody UserUpdatePasswordDto password) {
        LOGGER.info("POST /api/v1/customers/password");

        String username = this.retrieveUsername(token);
        return userService.checkIfPasswordMatches(username, password);
    }

    @Secured("ROLE_USER")
    @DeleteMapping
    @Operation(summary = "Delete user who sends this request", security = @SecurityRequirement(name = "apiKey"))
    public void deleteUser(@RequestHeader(name = "Authorization") String token) {
        LOGGER.info("DELETE /api/v1/customers");

        String username = this.retrieveUsername(token);
        userService.deleteUser(username);
    }

    private String retrieveUsername(String jwtToken) {
        byte[] signingKey = securityProperties.getJwtSecret().getBytes();
        Claims claims = Jwts.parserBuilder().setSigningKey(signingKey).build()
            .parseClaimsJws(jwtToken.replace(securityProperties.getAuthTokenPrefix(), ""))
            .getBody();
        String user = claims.getSubject();
        return user;
    }

    @PostMapping
    @PermitAll
    public ResponseEntity<UserDto> register(@RequestBody @Valid UserRegistrationDto userRegistrationDto) {
        LOGGER.info("POST /api/v1/customers");
        LOGGER.debug("Registration dto: {}", userRegistrationDto);
        return new ResponseEntity<>(
            userMapper.userToUserDto(userService.register(userRegistrationDto)),
            HttpStatus.CREATED);
    }

    @PostMapping("/requestReset/{username}")
    @PermitAll
    public ResponseEntity<Void> requestResetEmail(@PathVariable String username) {
        LOGGER.info("POST /api/v1/customers/requestReset/{}", username);
        String token = UUID.randomUUID().toString();
        userService.generatePasswordResetTokenForUser(username, token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/resetPassword")
    @PermitAll
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid UserResetPasswordDto userResetPasswordDto) {
        LOGGER.info("PUT /api/v1/customers/resetPassword?token={}", userResetPasswordDto.getToken());
        userService.resetPasswordForUser(userResetPasswordDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/token")
    @PermitAll
    public ResponseEntity<Void> isTokenValid(@RequestParam String token) {
        LOGGER.info("GET /api/v1/customers/token?token={}", token);
        if (userService.isTokenValid(token)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        throw new TokenExpiredException("The password reset link is expired!");
    }
}
