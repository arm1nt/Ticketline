package at.ac.tuwien.sepm.groupphase.backend.service;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.TokenExpiredException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {

    /**
     * Find a user in the context of Spring Security based on the email address
     * <br>
     * For more information have a look at this tutorial:
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * @param email the email address
     * @return a Spring Security user
     * @throws UsernameNotFoundException is thrown if the specified user does not exists
     */
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    /**
     * Find an application user based on the email address.
     *
     * @param email the email address
     * @return a application user
     */
    ApplicationUser findApplicationUserByEmail(String email);

    /**
     * Log in a user.
     *
     * @param userLoginDto login credentials
     * @return the JWT, if successful
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are bad
     */
    String login(UserLoginDto userLoginDto);

    /**
     * Find an application user based on his unique and immutable username.
     *
     * @param username the username
     * @return an application user
     */
    ApplicationUser findApplicationUserByUsername(String username);

    /**
     * Update the information of a user.
     *
     * @param username username of the user whose information should be updated
     * @param userUpdateDto dto containing the new information
     * @return edited application user entity
     */
    ApplicationUser updateApplicationUser(String username, UserUpdateDto userUpdateDto);

    /**
     * Check whether the given password matches the stored password for the user.
     *
     * @param username username of the users whose password we try to match
     * @param password password that should be compared to the persisted password of the user
     * @return true if the given password matches the stored one and false if not
     */
    boolean checkIfPasswordMatches(String username, UserUpdatePasswordDto password);

    /**
     * Deletes the user.
     *
     * @param username Username of the user who should be deleted.
     */
    void deleteUser(String username);



    /**
     * Register a new user account.
     *
     * @param userRegistrationDto Registration credentials
     * @return The user as dto, if successful.
     */
    ApplicationUser register(UserRegistrationDto userRegistrationDto);

    /**
     * Generate a new token for the given user to reset his password and send him an e-mail with a reset link.
     *
     * @param user The user for which the reset token should be generated, should not be null.
     * @param token The token.
     */
    void generatePasswordResetTokenForUser(ApplicationUser user, String token);

    /**
     * Generate a new token for the given user to reset his password and send him an e-mail with a reset link.
     *
     * @param username The username of the user for which the reset token should be generated, should not be null.
     * @param token The token.
     */
    void generatePasswordResetTokenForUser(String username, String token);

    /**
     * Update the password of the user to which the given token matches.
     *
     * @param userResetPasswordDto A DTO containing the new password and the token.
     */
    void resetPasswordForUser(UserResetPasswordDto userResetPasswordDto);

    /**
     *Checks if a given reset token is valid, i.e. exists and is not expired yet.
     *
     * @param token The token to check.
     * @return True, if the token is valid, false otherwise.
     */
    boolean isTokenValid(String token) throws NotFoundException, TokenExpiredException;


}
