package com.example.demo.students;

import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.InvalidCredentialsException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final StudentMapper studentMapper;

    public StudentService(StudentRepository studentRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.studentMapper = studentMapper;
    }

    @Transactional
    public StudentResponse register(StudentRegisterRequest request) {
        if (studentRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Ja existe uma conta cadastrada com este e-mail.");
        }
        Student student = Student.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        Student saved = studentRepository.save(student);
        return studentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Student student = studentRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("E-mail ou senha invalidos."));

        if (!passwordEncoder.matches(request.password(), student.getPassword())) {
            throw new InvalidCredentialsException("E-mail ou senha invalidos.");
        }

        String token = jwtService.generateToken(student.getId(), student.getEmail());
        return LoginResponse.of(token, jwtService.expirationSeconds(), studentMapper.toResponse(student));
    }

    @Transactional(readOnly = true)
    public StudentResponse getById(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno nao encontrado."));
        return studentMapper.toResponse(student);
    }
}
