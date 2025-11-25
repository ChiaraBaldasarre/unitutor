package service;
import com.unitutor.grupo3_unitutor.model.Role;
import com.unitutor.grupo3_unitutor.model.Usuario;
import com.unitutor.grupo3_unitutor.service.UsuarioService;
import com.unitutor.grupo3_unitutor.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UsuarioRepository userRepository;
    private UsuarioService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UsuarioRepository.class);
        userService = new UsuarioService(userRepository);
    }

    // ---------------------------
    // SUCCESSFUL LOGIN: STUDENT
    // ---------------------------
    @Test
    void authenticateStudent_successful() {
        Usuario student = new Usuario();
        student.setDni("12345678");

        Role role = new Role();
        role.setName("Student");
        student.setRole(role);

        when(userRepository.findByDni("12345678"))
                .thenReturn(Optional.of(student));

        Optional<Usuario> result = userService.autenticarPorDni("12345678");

        assertTrue(result.isPresent());
        assertEquals("Student", result.get().getRole().getName());
    }

    // ---------------------------
    // SUCCESSFUL LOGIN: TEACHER
    // ---------------------------
    @Test
    void authenticateTeacher_successful() {
        Usuario teacher = new Usuario();
        teacher.setDni("87654321");

        Role role = new Role();
        role.setName("Teacher");
        teacher.setRole(role);

        when(userRepository.findByDni("87654321"))
                .thenReturn(Optional.of(teacher));

        Optional<Usuario> result = userService.autenticarPorDni("87654321");

        assertTrue(result.isPresent());
        assertEquals("Teacher", result.get().getRole().getName());
    }

    // ---------------------------
    // FAILED LOGIN: INVALID DNI FORMAT
    // ---------------------------
    @Test
    void authenticate_failInvalidFormat() {
        Optional<Usuario> result = userService.autenticarPorDni("abc123");

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

        Optional<Usuario> result = userService.autenticarPorDni("12345678");

        assertTrue(result.isEmpty());
    }
}
