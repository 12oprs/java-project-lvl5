package hexlet.code.app.model;

import com.sun.istack.NotNull;
import hexlet.code.app.dto.TaskDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.*;

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
    @JoinColumn(name = "status_id")
    private TaskStatus taskStatus;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "executor_id")
    private User executor;

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @ElementCollection
    @CollectionTable(name = "task_labels", joinColumns = @JoinColumn(name = "label_id"))
    @Column(name = "labels")
//    @JoinTable(name = "tasks_labels",
//            joinColumns = {
//                    @JoinColumn(name = "tasks_id", referencedColumnName = "id",
//                            nullable = false, updatable = false)},
//            inverseJoinColumns = {
//                    @JoinColumn(name = "labels_id", referencedColumnName = "id",
//                            nullable = false, updatable = false)})
    private Set<Label> labels = new HashSet<>();

    public Task(TaskDTO dto) {
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.taskStatus = dto.getStatus();
        this.author = dto.getAuthor();
        this.executor = dto.getExecutor();
        if (dto.getLabels() != null) {
            this.labels.addAll(dto.getLabels());
        }
    }

    public void addLabel(Label label) {
        labels.add(label);
    }

}
