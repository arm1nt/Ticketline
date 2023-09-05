package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserShortDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/admin")
@Secured("ROLE_ADMIN")
public class AdminEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final AdminService adminService;
    private final UserMapper userMapper;

    public AdminEndpoint(AdminService adminService, UserMapper userMapper) {
        this.adminService = adminService;
        this.userMapper = userMapper;
    }

    @PostMapping
    @Operation(summary = "Create new user", security = @SecurityRequirement(name = "apiKey"))
    @Secured("ROLE_ADMIN")
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserCreationDto userCreationDto) {
        LOGGER.info("POST /api/v1/admin");
        LOGGER.debug("userCreationDto: {}", userCreationDto);
        ApplicationUser newUser = adminService.createUser(userCreationDto);
        return new ResponseEntity<>(userMapper.userToUserDto(newUser), HttpStatus.CREATED);
    }

    @GetMapping("/customers/locked")
    @Secured("ROLE_ADMIN")
    List<UserShortDto> getLockedUsers() {
        LOGGER.info("GET /api/v1/admin/customers/locked");
        List<ApplicationUser> lockedUsers = adminService.findLockedUsers();
        return userMapper.userListToUserShortDtoList(lockedUsers);
    }

    @PutMapping("/customers")
    @Operation(summary = "Unlock user account", security = @SecurityRequirement(name = "apiKey"))
    @Secured("ROLE_ADMIN")
    void unlockUser(@RequestParam String username) {
        LOGGER.info("PUT /api/v1/admin/customers?userName={}", username);
        adminService.unlockUser(username);
    }

    @GetMapping("/customers")
    @Secured("ROLE_ADMIN")
    Page<UserShortDto> getUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        LOGGER.info("GET /api/v1/admin/customers?page={}&size={}", page, size);
        return adminService.getAllNonLockedCustomers(page, size);
    }

    @PutMapping("/lock")
    @Operation(summary = "Lock user account", security = @SecurityRequirement(name = "apiKey"))
    @Secured("ROLE_ADMIN")
    void lockUser(@RequestParam String username) {
        LOGGER.info("PUT /api/v1/admin/lock?userName={}", username);
        adminService.lockUser(username);
    }

    @PostMapping("/reset")
    @Secured("ROLE_ADMIN")
    void requestResetEmail(@RequestParam String username) {
        LOGGER.info("PUT /api/v1/admin/reset?userName={}", username);
        String token = UUID.randomUUID().toString();
        adminService.generateResetPasswordTokenForUser(username, token);
    }
}
