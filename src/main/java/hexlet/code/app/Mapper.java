package hexlet.code.app;

import hexlet.code.app.dto.UserCreationDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.model.User;
import org.springframework.stereotype.Component;

@Component
public final class Mapper {

    public UserDTO toDTO(User user) {
        return new UserDTO(user.getFirstName(),
                user.getLastName(),
                user.getEmail());
    }

    public User toUser(UserCreationDTO dto) {
        return new User(dto);
    }
}
