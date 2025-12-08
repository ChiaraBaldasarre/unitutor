
package com.unitutor.grupo3_unitutor.service;
import com.unitutor.grupo3_unitutor.model.User;
import com.unitutor.grupo3_unitutor.exception.PermissionDeniedException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    public void checkIsProfessor(User user) {

        if (user == null || !"PROFESSOR".equalsIgnoreCase(user.getRole().getName())) {
            throw new PermissionDeniedException("Access Denied: Only users with the PROFESSOR role can perform this action.");
        }
    }
}