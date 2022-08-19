package hexlet.code.app.controller;

import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.model.Task;
import hexlet.code.app.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
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

import com.querydsl.core.types.Predicate;


@AllArgsConstructor
@RestController
@RequestMapping(value = "${base-url}" + "/tasks")
public class TaskController {

    private static final String ONLY_AUTHORIZED = """
                authentication.isAuthenticated()
            """;

    private static final String ONLY_OWNER_BY_ID = """
                @userRepository.findById(#id).get().getEmail() == authentication.getName()
            """;

    private final TaskService service;

    @Operation(summary = "Get tasks")
    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Tasks found",
            content = @Content(schema = @Schema(implementation = Task.class)))
    )
    @GetMapping
    public Iterable<Task> getTasks(
            @QuerydslPredicate(root = Task.class) final Predicate predicate) {
        return service.getTasks(predicate);
    }

    @Operation(summary = "Get task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/{id}")
    public Task getTask(@PathVariable final long id) {
        return service.getTask(id);
    }

    @Operation(summary = "Create task")
    @ApiResponse(responseCode = "201", description = "Task created")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(ONLY_AUTHORIZED)
    public Task createTask(@RequestBody final TaskDTO dto) {
        return service.createTask(dto);
    }

    @Operation(summary = "Update task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated"),
            @ApiResponse(responseCode = "404", description = "Can't update. Task not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize(ONLY_AUTHORIZED)
    public Task updateTask(@PathVariable final long id, @RequestBody final TaskDTO dto) {
        return service.updateTask(id, dto);
    }

    @Operation(summary = "Delete task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted"),
            @ApiResponse(responseCode = "404", description = "Can't delete. Task not exist")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteTask(@PathVariable final long id) {
        service.deleteTask(id);
    }

}
