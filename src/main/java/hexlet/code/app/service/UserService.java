package hexlet.code.app.service;

import hexlet.code.app.dto.UserCreationDTO;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final UserRepository repository;

    private final PasswordEncoder encoder;

    public List<User> getUsers() {
        return (List<User>) repository.findAll();
    }

    public User getUser(final long id) {
        return repository.findById(id).get();
    }

    public User createUser(final UserCreationDTO dto) {
        final User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        return repository.save(user);
    }

    public User updateUser(final long id, final UserCreationDTO dto) {
        final User updatedUser = repository.findById(id).get();
        updatedUser.setFirstName(dto.getFirstName());
        updatedUser.setLastName(dto.getLastName());
        updatedUser.setEmail(dto.getEmail());
        updatedUser.setPassword(encoder.encode(dto.getPassword()));
        return repository.save(updatedUser);
    }

    public void deleteUser(final long id) {
        repository.deleteById(id);
    }

    public String getCurrentUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public User getCurrentUser() {
        return repository.findByEmail(getCurrentUserName()).get();
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return repository.findByEmail(username)
                .map(this::buildSpringUser)
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
