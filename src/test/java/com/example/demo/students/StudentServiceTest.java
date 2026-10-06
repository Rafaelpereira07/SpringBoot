package com.example.demo.students;

import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.InvalidCredentialsException;
import com.example.demo.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the core business rules of student registration and login:
 * password hashing, duplicate e-mail rejection, and credential validation.
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentService = new StudentService(studentRepository, passwordEncoder, jwtService, new StudentMapper());
    }

    @Test
    void registerHashesThePasswordBeforePersisting() {
        StudentRegisterRequest request = new StudentRegisterRequest("Ana Souza", "ana@example.com", "senhaSegura123");
        when(studentRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("hashed-value");
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> {
            Student toSave = invocation.getArgument(0);
            toSave.setId(1L);
            return toSave;
        });

        StudentResponse response = studentService.register(request);

        assertThat(response.email()).isEqualTo("ana@example.com");
        verify(passwordEncoder).encode("senhaSegura123");
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void registerRejectsDuplicateEmail() {
        StudentRegisterRequest request = new StudentRegisterRequest("Ana Souza", "ana@example.com", "senhaSegura123");
        when(studentRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> studentService.register(request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void loginFailsWithInvalidCredentials() {
        LoginRequest request = new LoginRequest("ana@example.com", "wrong-password");
        Student student = Student.builder().id(1L).name("Ana").email("ana@example.com").password("hashed").build();

        when(studentRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(student));
        when(passwordEncoder.matches("wrong-password", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> studentService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void loginFailsWhenEmailDoesNotExist() {
        LoginRequest request = new LoginRequest("missing@example.com", "any-password");
        when(studentRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void loginSucceedsAndReturnsAToken() {
        LoginRequest request = new LoginRequest("ana@example.com", "correct-password");
        Student student = Student.builder().id(1L).name("Ana").email("ana@example.com").password("hashed").build();

        when(studentRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(student));
        when(passwordEncoder.matches("correct-password", "hashed")).thenReturn(true);
        when(jwtService.generateToken(1L, "ana@example.com")).thenReturn("jwt-token");
        when(jwtService.expirationSeconds()).thenReturn(7200L);

        LoginResponse response = studentService.login(request);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.student().email()).isEqualTo("ana@example.com");
    }
}
