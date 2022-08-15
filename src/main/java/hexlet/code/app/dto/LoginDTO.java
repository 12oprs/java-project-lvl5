package hexlet.code.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    private String firstName;

    private String lastName;

    private String email;

    private String password;

}
