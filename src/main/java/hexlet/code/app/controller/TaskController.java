package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.querydsl.core.types.Predicate;


@AllArgsConstructor
@RestController
@RequestMapping(value = "${base-url}" + "/tasks")
public class TaskController {


    @Autowired
    private TaskService service;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private static final String ONLY_AUTHORIZED = """
                authentication.isAuthenticated()
            """;

    private static final String ONLY_OWNER_BY_ID = """
            @userRepository.findById(#id).get().getEmail() == authentication.getName()
        """;

    @GetMapping
    public Iterable<Task> getTasks(
            @QuerydslPredicate(root = Task.class, bindings = TaskRepository.class) Predicate predicate) {
        return service.getTasks(predicate);
    }

    @GetMapping("/{id}")
    public Task getTask(@PathVariable long id) {
        Task task = null;
        try {
            task = service.getTask(id);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
        return task;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(ONLY_AUTHORIZED)
    public Task createTask(@RequestBody TaskDTO dto) {
        return service.createTask(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize(ONLY_AUTHORIZED)
    public Task updateTask(@PathVariable long id, @RequestBody TaskDTO dto) throws Exception {
        Task task = null;
        try {
            task = service.updateTask(id, dto);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
        return task;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public String deleteTask(@PathVariable long id) {
        try {
            return service.deleteTask(id);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }

}
