package com.unitutor.grupo3_unitutor.service;

import com.unitutor.grupo3_unitutor.model.TutoringSession;
import com.unitutor.grupo3_unitutor.repository.TutoringSessionRepository;
import com.unitutor.grupo3_unitutor.view.ConsoleIO;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import com.unitutor.grupo3_unitutor.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TutoringSessionService {

    private static final Logger logger = LoggerFactory.getLogger(TutoringSessionService.class);

    private final TutoringSessionRepository sessionRepository;
    private final ConsoleIO consoleIO;
    private final AuthorizationService authorizationService;

    public TutoringSessionService(TutoringSessionRepository sessionRepository, ConsoleIO consoleIO,
            AuthorizationService authorizationService) {
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
            logger.error("Database error during tutoring session creation", e);
            consoleIO.writeError("An unexpected error occurred. Contact support.");
            return Optional.empty();
        }
    }

    public static List<TutoringSession> searchSessions(String subject, LocalDateTime date, String modality) {

        if (subject != null && !subject.isBlank()) {
            return TutoringSessionRepository.findBySubjectIgnoreCase(subject);
        }

        if (date != null) {
            LocalDateTime start = date.withHour(0).withMinute(0);
            LocalDateTime end = date.withHour(23).withMinute(59);
            return TutoringSessionRepository.findByStartTimeBetween(start, end);
        }

        if (modality != null && !modality.isBlank()) {
            return TutoringSessionRepository.findByModalityIgnoreCase(modality);
        }

        return List.of();
    }
}