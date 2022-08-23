package hexlet.code.app.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.app.dto.TaskDto;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Service
@AllArgsConstructor
public final class TaskService {

    private final TaskRepository taskRepository;

    private final UserService userService;

    private final LabelService labelService;

    private final TaskStatusService statusService;

    public Iterable<Task> getTasks() {
        return taskRepository.findAll();
    }

    public Iterable<Task> getTasks(final Predicate predicate) {
        return taskRepository.findAll(predicate);
    }

    public Task getTask(final long id) {
        return taskRepository.findById(id).get();
    }

    public Task createTask(final TaskDto dto) {
        final User author = userService.getCurrentUser();
        final Task newTask = fromDto(dto);
        newTask.setAuthor(author);
        return taskRepository.save(newTask);
    }

    public Task updateTask(final long id, final TaskDto dto) {
        final Task updatedTask = taskRepository.findById(id).get();
        merge(updatedTask, dto);
        return taskRepository.save(updatedTask);
    }

    public void deleteTask(final long id) {
        taskRepository.deleteById(id);
    }

    private void merge(final Task task, final TaskDto dto) {
        final Task newTask = fromDto(dto);
        task.setName(newTask.getName() == null ? task.getName() : newTask.getName());
        task.setDescription(newTask.getDescription() == null ? task.getDescription() : newTask.getDescription());
        task.setTaskStatus(newTask.getTaskStatus() == null ? task.getTaskStatus() : newTask.getTaskStatus());
        task.setAuthor(newTask.getAuthor() == null ? task.getAuthor() : newTask.getAuthor());
        task.setExecutor(newTask.getExecutor() == null ? task.getExecutor() : newTask.getExecutor());
        task.setLabels(newTask.getLabels().isEmpty() ? task.getLabels() : newTask.getLabels());
    }

    private Task fromDto(final TaskDto dto) {
        final TaskStatus taskStatus = Optional.ofNullable(dto.getTaskStatusId())
                .map(TaskStatus::new)
                .orElse(null);
        final User author = Optional.ofNullable(dto.getAuthorId())
                .map(User::new)
                .orElse(null);
        final User executor = Optional.ofNullable(dto.getExecutorId())
                .map(User::new)
                .orElse(null);
        final Set<Label> labels = new HashSet<>();
        Optional.ofNullable(dto.getLabelIds())
                .orElse(new HashSet<>())
                .forEach(id -> labels.add(new Label(id)));
        return Task.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .taskStatus(taskStatus)
                .author(author)
                .executor(executor)
                .labels(labels)
                .build();
    }
}
