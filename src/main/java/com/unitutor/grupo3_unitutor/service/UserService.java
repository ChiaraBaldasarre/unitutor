package com.unitutor.grupo3_unitutor.service;

import com.unitutor.grupo3_unitutor.model.User;
import com.unitutor.grupo3_unitutor.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UsuarioRepository usuarioRepository;

    public UserService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<User> authenticateByDni(String dni) {

        if (dni == null || !dni.matches("^\\d{8}$")) {
            return Optional.empty();
        }

        return usuarioRepository.findByDni(dni);
    }
}