package hexlet.code.app.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserCreationDTO extends UserDTO {

    private String password;

    public UserCreationDTO(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email);
        this.password = password;
    }
}
