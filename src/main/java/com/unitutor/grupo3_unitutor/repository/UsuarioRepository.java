package com.unitutor.grupo3_unitutor.repository;
import com.unitutor.grupo3_unitutor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<User, String> {
    Optional<User> findByDni(String dni);
}