package hexlet.code.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private String name;
    private String description;
    private Long taskStatusId;
    private Long authorId;
    private Long executorId;
    private Set<Long> labelIds;
}
