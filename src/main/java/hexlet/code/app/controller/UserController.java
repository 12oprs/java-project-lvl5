package hexlet.code.app.controller;

import hexlet.code.app.dto.UserCreationDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.model.User;
import hexlet.code.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(path = "${base-url}" + "/users")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping
    public List<User> getUsers () {
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
    public User createUser(@RequestBody UserCreationDTO user) {
        return service.createUser(user);
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
        return service.deleteUser(id);
    }
}
