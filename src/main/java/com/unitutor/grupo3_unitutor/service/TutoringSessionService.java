package com.unitutor.grupo3_unitutor.service;

import com.unitutor.grupo3_unitutor.model.TutoringSession;
import com.unitutor.grupo3_unitutor.repository.TutoringSessionRepository;
import com.unitutor.grupo3_unitutor.view.ConsoleIO;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import com.unitutor.grupo3_unitutor.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TutoringSessionService {

    private final TutoringSessionRepository sessionRepository;
    private final ConsoleIO consoleIO;
    private final AuthorizationService authorizationService;

    public TutoringSessionService(TutoringSessionRepository sessionRepository, ConsoleIO consoleIO, AuthorizationService authorizationService) {
        this.sessionRepository = sessionRepository;
        this.consoleIO = consoleIO;
        this.authorizationService = authorizationService;
    }

    public Optional<TutoringSession> createSession(User professor, TutoringSession session) {
        authorizationService.checkIsProfessor(professor);
        if (session.getStartTime().isBefore(LocalDateTime.now())) {
            return Optional.empty();
        }

        try {
            TutoringSession savedSession = sessionRepository.save(session);
            return Optional.of(savedSession);

        } catch (DataAccessException e) {
            consoleIO.writeError("CRITICAL DB ERROR during session creation: " + e.getMessage());
            return Optional.empty();
        }
    }
}