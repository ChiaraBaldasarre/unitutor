package com.unitutor.grupo3_unitutor.service;

import com.unitutor.grupo3_unitutor.model.Usuario;
import com.unitutor.grupo3_unitutor.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> autenticarPorDni(String dni) {

        if (dni == null || !dni.matches("^\\d{8}$")) {
            return Optional.empty();
        }

        return usuarioRepository.findByDni(dni);
    }
}