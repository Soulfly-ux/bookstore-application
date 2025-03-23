package bg.softuni.app.web.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEditRequest {

    @Size(max = 20, message = "First name can't have more than 20 characters")
    private String firstName;

    @Size(max = 20, message = "Last name can't have more than 20 characters")
    private String lastName;

    @URL(message = "Requires correct link format")

    private String profilePicture;
}
