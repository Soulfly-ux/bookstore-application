package bg.softuni.app.web.dto;


import bg.softuni.app.user.model.Country;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotNull
    @Size(min = 6, message = "Username must be at least 5 characters long")
    public String username;

    @NotNull
    @Email
    public String email;

    @NotNull
    @Size(min = 6, message = "Password must be at least 5 characters long")
    public String password;

    @NotNull
    private Country country;

}
