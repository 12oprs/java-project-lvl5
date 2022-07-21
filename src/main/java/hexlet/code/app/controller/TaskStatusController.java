package hexlet.code.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.TaskStatusService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping(value = "${base-url}" + "/statuses")
public class TaskStatusController {

    @Autowired
    private TaskStatusService service;

    @Autowired
    private TaskStatusRepository statusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper mapper;

    private static final String ONLY_AUTHORIZED = """
                authentication.isAuthenticated()
            """;

    @GetMapping
    public List<TaskStatus> getStatuses() {
        return service.getStatuses();
    }

    @GetMapping("/{id}")
    public TaskStatus getTaskStatus(@PathVariable long id) {
        TaskStatus status = null;
        try {
            status = service.getTaskStatus(id);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
        return status;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(ONLY_AUTHORIZED)
    public TaskStatus createTaskStatus(@RequestBody String json) {
        String name = null;
        try {
            name = (String) mapper.readValue(json, Map.class).get("name");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return service.createTaskStatus(name);
    }

    @PutMapping("/{id}")
    @PreAuthorize(ONLY_AUTHORIZED)
    public TaskStatus updateTaskStatus(@PathVariable long id, @RequestBody String json) throws Exception {
        String name = null;
        try {
            name = (String) mapper.readValue(json, Map.class).get("name");
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
        return service.updateTaskStatus(id, name);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ONLY_AUTHORIZED)
    public String deleteTaskStatus(@PathVariable long id) {
        try {
            return service.deleteTaskStatus(id);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }
}
