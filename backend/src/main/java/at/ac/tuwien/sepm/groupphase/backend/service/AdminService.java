package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserCreationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserShortDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminService {
    /**
     * Create a new user. The new password will be automatically generated and sent to the user per e-mail.
     *
     * @param userCreationDto A DTO containing the account data.
     * @return The newly created user
     */
    ApplicationUser createUser(UserCreationDto userCreationDto);

    /**
     * Get all users whose account is locked.
     * @return A list containing all users whose account is locked.
     *
     */
    List<ApplicationUser> findLockedUsers();

    /**
     * Unlock the account of user with username.
     *
     * @param userName The username of the user.
     * @throws NotFoundException In case the user is not found.
     */
    void unlockUser(String userName) throws NotFoundException;

    /**
     * Get all non-locked customers but paginated.
     *
     * @param page The page number.
     * @param size The size of the page.
     * @return A page containing the requested users.
     */
    Page<UserShortDto> getAllNonLockedCustomers(int page, int size);

    /**
     * Lock the account of user with username. The user should not be an admin.
     *
     * @param userName The username of the user.
     * @throws NotFoundException In case the user is not found.
     * @throws ForbiddenException In case the user exists but is an admin.
     */
    void lockUser(String userName) throws NotFoundException, ForbiddenException;

    /**
     * Generate a reset password token for a user.
     *
     * @param username The username of the user.
     * @param token The reset token.
     * @throws NotFoundException In case a user with the given username does not exist.
     */
    void generateResetPasswordTokenForUser(String username, String token) throws NotFoundException;
}
