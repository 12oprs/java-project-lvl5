package hexlet.code.app.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public final class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private LabelService labelService;

    @Autowired
    private TaskStatusService statusService;

    public Iterable<Task> getTasks() {
        return taskRepository.findAll();
    }

    public Iterable<Task> getTasks(Predicate predicate) {
        if (predicate == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            return taskRepository.findAllByAuthorId(user.getId());
        }
        return taskRepository.findAll(predicate);
    }

    public Task getTask(long id) throws Exception {
        return taskRepository.findById(id).orElseThrow(() -> new Exception("Task not found"));
    }

    public Task createTask(TaskDTO dto) throws Exception {
        final User author = userService.getCurrentUser();
        Task newTask = Task.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .taskStatus(new TaskStatus(dto.getTaskStatusId()))
                .author(author)
                .build();
        if (dto.getExecutorId() != null) {
            newTask.setExecutor(userService.getUser(dto.getExecutorId()));
        }
        if (dto.getLabelIds() != null) {
            newTask.setLabels(labelService.getLabels(dto.getLabelIds()));
        }

        return taskRepository.save(newTask);
    }

    public Task updateTask(long id, TaskDTO dto) throws Exception {
        Task updatedTask = taskRepository.findById(id).orElseThrow(() -> new Exception("Can't update. Task not found"));
        updatedTask.setName(dto.getName());
        updatedTask.setDescription(dto.getDescription());
        updatedTask.setTaskStatus(statusService.getTaskStatus(dto.getTaskStatusId()));
        updatedTask.setAuthor(userService.getUser(dto.getAuthorId()));
        if (dto.getExecutorId() != null) {
            updatedTask.setExecutor(userService.getUser(dto.getExecutorId()));
        }
        if (dto.getLabelIds() != null) {
            updatedTask.setLabels(labelService.getLabels(dto.getLabelIds()));
        }
        return taskRepository.save(updatedTask);
    }

    public String deleteTask(long id) throws Exception {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return "Task deleted";
        }
        throw new Exception("Can't delete. Task not exist");
    }
}
