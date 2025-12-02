package com.unitutor.grupo3_unitutor.service;

import com.unitutor.grupo3_unitutor.model.TutoringSession;
import com.unitutor.grupo3_unitutor.repository.TutoringSessionRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TutoringSessionService {

    private final TutoringSessionRepository sessionRepository;

    public TutoringSessionService(TutoringSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public Optional<TutoringSession> createSession(TutoringSession session) {
        if (session.getStartTime().isBefore(LocalDateTime.now())) {
            return Optional.empty();
        }

        try {
            TutoringSession savedSession = sessionRepository.save(session);
            return Optional.of(savedSession);
        } catch (DataAccessException e) {
            System.err.println("CRITICAL DB ERROR during session creation: " + e.getMessage());
            return Optional.empty();
        }
    }
}