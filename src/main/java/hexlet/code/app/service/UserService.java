package hexlet.code.app.service;

import hexlet.code.app.Mapper;
import hexlet.code.app.dto.UserCreationDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private Mapper mapper;

    public List<User> getUsers() {
        return (List<User>) repository.findAll();
    }

    public User getUser(long id) throws Exception {
        return repository.findById(id).orElseThrow(() -> new Exception("User not exist"));
    }

    public User createUser(UserCreationDTO dto) {
        User user = mapper.toUser(dto);
        return repository.save(user);
    }
    public User updateUser(long id, UserCreationDTO dto) throws Exception {
        User updatedUser = repository.findById(id).orElseThrow(() -> new Exception("User not exist"));
        updatedUser.setFirstName(dto.getFirstName());
        updatedUser.setLastName(dto.getLastName());
        updatedUser.setEmail(dto.getEmail());
        updatedUser.setPassword(dto.getPassword());
        return repository.save(updatedUser);
    }

    public String deleteUser(long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return "User deleted";
        }
        return "User not exist";
    }
}
