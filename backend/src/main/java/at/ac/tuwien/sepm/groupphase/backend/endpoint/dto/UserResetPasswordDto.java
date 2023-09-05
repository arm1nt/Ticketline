package at.ac.tuwien.sepm.groupphase.backend.endpoint.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResetPasswordDto {

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters long!")
    private String password;
    @NotBlank
    private String token;

}
