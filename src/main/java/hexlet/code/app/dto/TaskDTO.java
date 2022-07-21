package hexlet.code.app.dto;

import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    private String name;
    private String description;
    private TaskStatus status;
    private User author;
    private User executor;

}
