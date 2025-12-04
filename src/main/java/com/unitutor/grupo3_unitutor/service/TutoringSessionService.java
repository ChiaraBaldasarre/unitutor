package com.unitutor.grupo3_unitutor.service;

import com.unitutor.grupo3_unitutor.model.TutoringSession;
import com.unitutor.grupo3_unitutor.repository.TutoringSessionRepository;
import com.unitutor.grupo3_unitutor.view.ConsoleIO;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TutoringSessionService {

    private final TutoringSessionRepository sessionRepository;
    private final ConsoleIO consoleIO;

    public TutoringSessionService(TutoringSessionRepository sessionRepository, ConsoleIO consoleIO) {
        this.sessionRepository = sessionRepository;
        this.consoleIO = consoleIO;
    }

    public Optional<TutoringSession> createSession(TutoringSession session) {
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