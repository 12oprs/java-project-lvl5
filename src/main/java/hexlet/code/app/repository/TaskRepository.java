package hexlet.code.app.repository;

import hexlet.code.app.model.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {

    Optional<Task> findByName(String name);

}
