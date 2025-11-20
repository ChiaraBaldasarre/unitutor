package com.unitutor.grupo3_unitutor.repository;
import com.unitutor.grupo3_unitutor.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    Optional<Usuario> findByDni(String dni);
}