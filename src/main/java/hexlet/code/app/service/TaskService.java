package hexlet.code.app.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


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
        if (predicate == null) {
            final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            final User user = (User) auth.getPrincipal();
            return taskRepository.findAllByAuthorId(user.getId());
        }
        return taskRepository.findAll(predicate);
    }

    public Task getTask(final long id) {
        return taskRepository.findById(id).get();
    }

    public Task createTask(final TaskDTO dto) {
        final User author = userService.getCurrentUser();
        final TaskStatus taskStatus = statusService.getTaskStatus(dto.getTaskStatusId());
        final Task newTask = Task.builder()
                .name(dto.getName())
                .taskStatus(taskStatus)
                .author(author)
                .build();
        if (dto.getDescription() != null) {
            newTask.setDescription(dto.getDescription());
        }
        if (dto.getExecutorId() != null) {
            newTask.setExecutor(userService.getUser(dto.getExecutorId()));
        }
        if (dto.getLabelIds() != null) {
            newTask.setLabels(labelService.getLabels(dto.getLabelIds()));
        }
        return taskRepository.save(newTask);
    }

    public Task updateTask(final long id, final TaskDTO dto) {
        final Task updatedTask = taskRepository.findById(id).get();
        if (dto.getName() != null) {
            updatedTask.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            updatedTask.setDescription(dto.getDescription());
        }
        if (dto.getTaskStatusId() != null) {
            updatedTask.setTaskStatus(statusService.getTaskStatus(dto.getTaskStatusId()));
        }
        if (dto.getAuthorId() != null) {
            updatedTask.setAuthor(userService.getUser(dto.getAuthorId()));
        }
        if (dto.getExecutorId() != null) {
            updatedTask.setExecutor(userService.getUser(dto.getExecutorId()));
        }
        if (dto.getLabelIds() != null) {
            updatedTask.setLabels(labelService.getLabels(dto.getLabelIds()));
        }
        return taskRepository.save(updatedTask);
    }

    public void deleteTask(final long id) {
        taskRepository.deleteById(id);
    }
}
