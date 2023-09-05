package at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    ApplicationUser userRegistrationDtoToUser(UserRegistrationDto userRegistrationDto);

    UserDto userToUserDto(ApplicationUser user);

    UserDetailsDto applicationUserToUserDetailsDto(ApplicationUser applicationUser);

    UserUpdateDto applicationUserToUserUpdateDto(ApplicationUser applicationUser);

    ApplicationUser userCreationDtoToUser(UserCreationDto userCreationDto);

    List<UserShortDto> userListToUserShortDtoList(List<ApplicationUser> applicationUsers);

    UserShortDto userToUserShortDto(ApplicationUser applicationUser);


}
