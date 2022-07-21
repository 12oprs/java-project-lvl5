package hexlet.code.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import hexlet.code.app.dto.TaskDTO;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 1)
    private String name;

    private String description;

    @NotNull
    @ManyToOne
    @JoinColumn(name="status_id")
    private TaskStatus status;

    @NotNull
    @ManyToOne
    @JoinColumn(name="author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name="executor_id")
    private User executor;

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

     public Task(TaskDTO dto) {
         this.name = dto.getName();
         this.description = dto.getDescription();
         this.status = dto.getStatus();
         this.author = dto.getAuthor();
         this.executor = dto.getExecutor();
     }

}
