package hexlet.code.app.service;

import hexlet.code.app.dto.UserCreationDTO;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static hexlet.code.app.config.SecurityConfig.DEFAULT_AUTHORITIES;

@Service
@AllArgsConstructor
public final class UserService implements UserDetailsService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    public List<User> getUsers() {
        return (List<User>) repository.findAll();
    }

    public User getUser(long id) throws Exception {
        return repository.findById(id).orElseThrow(() -> new Exception("User not found"));
    }

    public User createUser(UserCreationDTO dto) {
        dto.setPassword(encoder.encode(dto.getPassword()));
        User user = new User(dto);
        return repository.save(user);
    }
    public User updateUser(long id, UserCreationDTO dto) throws Exception {
        User updatedUser = repository.findById(id).orElseThrow(() -> new Exception("Can't update. User not found"));
        updatedUser.setFirstName(dto.getFirstName());
        updatedUser.setLastName(dto.getLastName());
        updatedUser.setEmail(dto.getEmail());
        updatedUser.setPassword(encoder.encode(dto.getPassword()));
        return repository.save(updatedUser);
    }

    public String deleteUser(long id) throws Exception {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return "User deleted";
        }
        throw new Exception("Can't delete. User not exist");
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return repository.findByEmail(username)
                .map(this:: buildSpringUser)
                .orElseThrow(() -> new UsernameNotFoundException("Not found user with 'username': " + username));
    }

    private UserDetails buildSpringUser(final User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                DEFAULT_AUTHORITIES
        );
    }
}
