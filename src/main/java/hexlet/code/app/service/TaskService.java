package hexlet.code.app.service;

import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.CaseBuilder;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.UserRepository;
import liquibase.repackaged.org.apache.commons.collections4.PredicateUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import com.querydsl.core.types.Predicate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;


@Service
@AllArgsConstructor
public final class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public Iterable<Task> getTasks(Predicate predicate) {
        if (predicate == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            return taskRepository.findByAuthorId(user.getId());
        }
        return taskRepository.findAll(predicate);
    }

    public Task getTask(long id) throws Exception {
        return taskRepository.findById(id).orElseThrow(() -> new Exception("Task not found"));
    }

    public Task createTask(TaskDTO dto) {
        Task newTask = new Task(dto);
        return taskRepository.save(newTask);
    }

    public Task updateTask(long id, TaskDTO dto) throws Exception {
        Task updatedTask = taskRepository.findById(id).orElseThrow(() -> new Exception("Can't update. Task not found"));
        updatedTask.setName(dto.getName());
        updatedTask.setDescription(dto.getDescription());
        updatedTask.setTaskStatus(dto.getStatus());
        updatedTask.setAuthor(dto.getAuthor());
        updatedTask.setExecutor(dto.getExecutor());
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
