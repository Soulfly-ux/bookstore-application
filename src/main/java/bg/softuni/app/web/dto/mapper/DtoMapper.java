package bg.softuni.app.web.dto.mapper;

import bg.softuni.app.user.model.User;
import bg.softuni.app.web.dto.UserEditRequest;

public class DtoMapper {

    public static UserEditRequest mapUserToUserEditRequest (User user) {

        return UserEditRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .profilePicture(user.getProfilePicture())
                .build();
    }
}
