package bg.softuni.app.user.service;


import bg.softuni.app.security.AuthenticationDetails;
import bg.softuni.app.user.model.User;
import bg.softuni.app.user.model.UserRole;
import bg.softuni.app.user.repository.UserRepository;
import bg.softuni.app.web.dto.RegisterRequest;
import bg.softuni.app.web.dto.UserEditRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public List<User> getAllUsers() {

        return userRepository.findAll();
    }


    public User registerNewUser(RegisterRequest registerRequest) {

        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(registerRequest.getUsername(), registerRequest.getEmail());

        if (optionalUser.isPresent()) {
            throw new IllegalStateException("User with username " + registerRequest.getUsername() + " already exists");
        }

        User user = initUser(registerRequest);
        userRepository.save(user);

        return user;
    }

    public User initUser(RegisterRequest registerRequest) {

        return User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .country(registerRequest.getCountry())
                .isActive(true)
                .role(UserRole.USER)
                .createdOn(LocalDateTime.now())
                .build();
    }


    public void editUserProfile (UUID id, UserEditRequest userEditRequest) {

        User user = getById(id);
        user.setFirstName(userEditRequest.getFirstName());
        user.setLastName(userEditRequest.getLastName());
        user.setProfilePicture(userEditRequest.getProfilePicture());

        userRepository.save(user);

    }

    public User getById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalStateException("User with id " + id + " does not exist"));
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalStateException("User with username [%s] not found.".formatted(username)));
        return new AuthenticationDetails(user.getId(), user.getUsername(), user.getPassword(), user.getRole(), user.isActive());


    }


    public void switchUserRole(UUID id) {

        User user = userRepository.getById(id);



        if(user.getRole().equals(UserRole.USER)){
            user.setRole(UserRole.ADMIN);
        }else {
            user.setRole(UserRole.USER);
        }
        userRepository.save(user);
    }

    public void deleteUserById(UUID id) {

        userRepository.deleteById(id);
    }
}
