package hexlet.code.app.controller;

import hexlet.code.app.dto.UserCreationDTO;
import hexlet.code.app.model.User;
import hexlet.code.app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(value = "${base-url}" + "/users")
public class UserController {

    private static final String ONLY_OWNER_BY_ID = """
                @userRepository.findById(#id).get().getEmail() == authentication.getName()
            """;

    private final UserService service;

    @Operation(summary = "Get users")
    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Users found",
            content = @Content(schema = @Schema(implementation = User.class)))
    )
    @GetMapping
    public List<User> getUsers() {
        return service.getUsers();
    }

    @Operation(summary = "Get user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public User getUser(@PathVariable final long id) {
        return service.getUser(id);
    }

    @Operation(summary = "Create user")
    @ApiResponse(responseCode = "201", description = "User created")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User registration(@RequestBody final UserCreationDTO dto) {
        return service.createUser(dto);
    }

    @Operation(summary = "Update user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "404", description = "Can't update. User not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public User updateUser(@PathVariable final long id, @RequestBody final UserCreationDTO dto) {
        return service.updateUser(id, dto);
    }

    @Operation(summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "Can't delete. User not exist")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteUser(@PathVariable final long id) {
        service.deleteUser(id);
    }
}
