package hexlet.code.app.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "labels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String name;

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

//    @ManyToMany(mappedBy = "labels")
//    private Set<Task> tasks = new HashSet<>();


    public Label(String newName) {
        this.name = newName;
    }
}
