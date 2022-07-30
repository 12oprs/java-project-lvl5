package hexlet.code.app.repository;

import hexlet.code.app.model.QTask;
import hexlet.code.app.model.Task;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface TaskRepository extends CrudRepository<Task, Long>,
        QuerydslPredicateExecutor<Task>,
        QuerydslBinderCustomizer<QTask> {

    Optional<Task> findByName(String name);
    Iterable<Task> findByAuthorId(Long id);

    @Override
    default void customize(QuerydslBindings bindings, QTask task) {
        bindings.bind(task.taskStatus).first(
                (path, value) -> path.eq(value)
        );
        bindings.bind(task.author.id).first(
                (path, value) -> path.eq(value)
        );
        bindings.bind(task.executor.id).first(
                (path, value) -> path.eq(value)
        );
        bindings.bind(task.labels.any().id).first(
                ((path, value) -> path.eq(value))
        );

    }

}
