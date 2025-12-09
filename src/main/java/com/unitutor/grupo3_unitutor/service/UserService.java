package com.unitutor.grupo3_unitutor.service;

import com.unitutor.grupo3_unitutor.model.User;
import com.unitutor.grupo3_unitutor.repository.UserRepository;
import org.springframework.stereotype.Service;
//import com.unitutor.grupo3_unitutor.utils.DniValidator;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository usuarioRepository;

    public UserService(UserRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<User> authenticateByDni(String dni) {

        //DniValidator dniValidator = new DniValidator();
        //String dniValidated = dniValidator.getValidationError(dni);

        if (dni == null || !dni.matches("^\\d{8}$")) {
            return Optional.empty();
        }

        return usuarioRepository.findByDni(dni);
    }
}