package service;
import com.unitutor.grupo3_unitutor.model.Role;
import com.unitutor.grupo3_unitutor.model.User;
import com.unitutor.grupo3_unitutor.service.UserService;
import com.unitutor.grupo3_unitutor.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UsuarioRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UsuarioRepository.class);
        userService = new UserService(userRepository);
    }

    // ---------------------------
    // SUCCESSFUL LOGIN: STUDENT
    // ---------------------------
    @Test
    void authenticateStudent_successful() {
        User student = new User();
        student.setDni("12345678");

        Role role = new Role();
        role.setName("Student");
        student.setRole(role);

        when(userRepository.findByDni("12345678"))
                .thenReturn(Optional.of(student));

        Optional<User> result = userService.authenticateByDni("12345678");

        assertTrue(result.isPresent());
        assertEquals("Student", result.get().getRole().getName());
    }

    // ---------------------------
    // SUCCESSFUL LOGIN: TEACHER
    // ---------------------------
    @Test
    void authenticateTeacher_successful() {
        User teacher = new User();
        teacher.setDni("87654321");

        Role role = new Role();
        role.setName("Teacher");
        teacher.setRole(role);

        when(userRepository.findByDni("87654321"))
                .thenReturn(Optional.of(teacher));

        Optional<User> result = userService.authenticateByDni("87654321");

        assertTrue(result.isPresent());
        assertEquals("Teacher", result.get().getRole().getName());
    }

    // ---------------------------
    // FAILED LOGIN: INVALID DNI FORMAT
    // ---------------------------
    @Test
    void authenticate_failInvalidFormat() {
        Optional<User> result = userService.authenticateByDni("abc123");

        assertTrue(result.isEmpty());
        verify(userRepository, never()).findByDni(any());
    }

    // ---------------------------
    // FAILED LOGIN: USER NOT FOUND
    // ---------------------------
    @Test
    void authenticate_failUserNotFound() {
        when(userRepository.findByDni("12345678"))
                .thenReturn(Optional.empty());

        Optional<User> result = userService.authenticateByDni("12345678");

        assertTrue(result.isEmpty());
    }
}
