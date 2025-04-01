package bg.softuni.app.user;


import bg.softuni.app.exception.UserAlreadyExistException;
import bg.softuni.app.security.AuthenticationDetails;
import bg.softuni.app.user.model.Country;
import bg.softuni.app.user.model.User;
import bg.softuni.app.user.model.UserRole;
import bg.softuni.app.user.repository.UserRepository;
import bg.softuni.app.user.service.UserService;
import bg.softuni.app.web.dto.RegisterRequest;
import bg.softuni.app.web.dto.UserEditRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private User testUser;
    private RegisterRequest registerRequest;
    private UserEditRequest userEditRequest;
    private String username;
    private String email;
    private String rawPassword;
    private String encodedPassword;
    private Country userCountry;


     @BeforeEach
    void setUp() {
        this.userId = UUID.randomUUID();
        this.username = "testUsername";
        this.email = "test@email.com";
        this.rawPassword = "testPassword";
        this.encodedPassword = "testEncodedPassword";
        this.userCountry = Country.BULGARIA;

        this.testUser = User.builder()
                .id(userId)
                .username(username)
                .email(email)
                .password(encodedPassword)
                .country(userCountry)
                .isActive(true)
                .role(UserRole.USER)
                .createdOn(LocalDateTime.now().minusDays(1))
                .firstName("Test")
                .lastName("Testov")
                .build();


        this.registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setEmail(email);
        registerRequest.setPassword(rawPassword);
        registerRequest.setCountry(userCountry);

        this.userEditRequest = new UserEditRequest();
        userEditRequest.setFirstName("EditFirst");
        userEditRequest.setLastName("EditLast");
        userEditRequest.setProfilePicture("editProfilePicture.jpg");
    }

    @Test
    void editUserProfile_whenUserDoesNotExist_thenThrowException() {
        // Arrange

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NullPointerException.class, () -> userService.editUserProfile(userId, userEditRequest));

         verify(userRepository,never()).save(any(User.class));
    }

    @Test
    void editUserProfile_whenUserExists_thenUpdateUser() {


        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        userService.editUserProfile(userId, userEditRequest);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository,times(1)).save(userCaptor.capture());

        User sevedUser = userCaptor.getValue();



        assertThat(sevedUser).isNotNull();
        assertThat(sevedUser.getId()).isEqualTo(userId);
        assertThat(sevedUser.getFirstName()).isEqualTo(userEditRequest.getFirstName());
        assertThat(sevedUser.getLastName()).isEqualTo(userEditRequest.getLastName());
        assertThat(sevedUser.getProfilePicture()).isEqualTo(userEditRequest.getProfilePicture());

        assertThat(sevedUser.getUsername()).isEqualTo(username);
        assertThat(sevedUser.getEmail()).isEqualTo(email);


    }

    @Test
    void getById_whenUserExists_thenReturnUser() {


        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        User foundUser = userService.getById(userId);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(userId);
        assertThat(foundUser.getUsername()).isEqualTo(username);
        assertThat(foundUser.getEmail()).isEqualTo(email);
        assertThat(foundUser.getCountry()).isEqualTo(userCountry);

        verify(userRepository, times(1)).findById(userId);

    }

    @Test
    void getById_whenUserDoesNotExist_thenThrowException(){


        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> userService.getById(userId));

    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {

        User user2 = User.builder()
                .id(UUID.randomUUID())
                .username("user2")
                .build();

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

        List<User> users = userService.getAllUsers();

        assertThat(users).hasSize(2);
        assertThat(users).isNotNull();

        verify(userRepository, times(1)).findAll();


    }

    @Test
    void registerNewUser_whenUserAlreadyExists_thenThrowException() {
        // Arrange

        when(userRepository.findByUsernameOrEmail(username, email)).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(UserAlreadyExistException.class, () -> userService.registerNewUser(registerRequest));

        verify(userRepository, times(1)).findByUsernameOrEmail(username, email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerNewUser_whenUserDoesNotExist_thenSaveUser() {
        // Arrange

        when(userRepository.findByUsernameOrEmail(username, email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);


        User savedUser = userService.registerNewUser(registerRequest);


        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getPassword()).isEqualTo(encodedPassword);
        assertThat(savedUser.getUsername()).isEqualTo(username);
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getCountry()).isEqualTo(userCountry);

        verify(userRepository, times(1)).findByUsernameOrEmail(username, email);
        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void loadUserByUsername_whenUserExists_thenReturnAuthenticationDetails() {


        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userService.loadUserByUsername(username);
        assertThat(userDetails).isNotNull();
        assertThat(userDetails).isInstanceOf(AuthenticationDetails.class);
        assertThat(userDetails.getUsername()).isEqualTo(username);
        assertThat(userDetails.getPassword()).isEqualTo(encodedPassword);
        assertThat(userDetails.isEnabled()).isEqualTo(testUser.isActive());


        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void  loadUserByUsername_whenUserDoesNotExists_thenThrowException() {


        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void switchUserRole_whenUserRoleIsUser_thenChangeToAdminAndSave() {

        testUser.setRole(UserRole.USER);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        userService.switchUserRole(userId);


        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getRole()).isEqualTo(UserRole.ADMIN);

    }

    @Test
    void switchUserRole_whenUserRoleIsAdmin_thenChangeToUserAndSave() {

        testUser.setRole(UserRole.ADMIN);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        userService.switchUserRole(userId);


        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getRole()).isEqualTo(UserRole.USER);

    }

    @Test
    void switchUserRole_whenUserDoesNotExist_thenThrowException() {

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> userService.switchUserRole(userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUserById_whenUserExists_thenDeleteUser() {

       doNothing().when(userRepository).deleteById(userId);

        userService.deleteUserById(userId);


        verify(userRepository, times(1)).deleteById(userId);
    }


}
