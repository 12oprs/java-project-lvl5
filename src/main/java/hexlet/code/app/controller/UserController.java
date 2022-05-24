package hexlet.code.app.controller;

import hexlet.code.app.dto.UserCreationDTO;
import hexlet.code.app.model.User;
import hexlet.code.app.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(value = "${base-url}" + "/users")
public final class UserController {

    @Autowired
    private UserService service;

    @GetMapping
    public List<User> getUsers() {
        return service.getUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        User user = null;
        try {
            user = service.getUser(id);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
        return user;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody UserCreationDTO dto) {
        return service.createUser(dto);
    }

    @PatchMapping("/{id}")
    public User updateUser(@PathVariable long id, @RequestBody UserCreationDTO dto) {
        User user = null;
        try {
            user = service.updateUser(id, dto);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), e);
        }
        return user;
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable long id) {
        try {
            return service.deleteUser(id);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, e.getMessage(), e);
        }
    }
}
