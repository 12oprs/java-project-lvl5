package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskStatusDTO;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.service.TaskStatusService;
import lombok.AllArgsConstructor;
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

@AllArgsConstructor
@RestController
@RequestMapping(value = "${base-url}" + "/statuses")
public class TaskStatusController {

    private static final String ONLY_AUTHORIZED = """
                authentication.isAuthenticated()
            """;

    private final TaskStatusService service;

    @GetMapping
    public List<TaskStatus> getStatuses() {
        return service.getStatuses();
    }

    @GetMapping("/{id}")
    public TaskStatus getTaskStatus(@PathVariable final long id) {
        try {
            return service.getTaskStatus(id);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(ONLY_AUTHORIZED)
    public TaskStatus createTaskStatus(@RequestBody final TaskStatusDTO dto) {
        return service.createTaskStatus(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize(ONLY_AUTHORIZED)
    public TaskStatus updateTaskStatus(@PathVariable final long id, @RequestBody final TaskStatusDTO dto) {
        try {
            return service.updateTaskStatus(id, dto);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(ONLY_AUTHORIZED)
    public String deleteTaskStatus(@PathVariable final long id) {
        try {
            return service.deleteTaskStatus(id);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }
}
