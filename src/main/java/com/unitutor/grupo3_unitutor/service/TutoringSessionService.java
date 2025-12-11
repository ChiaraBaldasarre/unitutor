package com.unitutor.grupo3_unitutor.service;

import com.unitutor.grupo3_unitutor.model.TutoringSession;
import com.unitutor.grupo3_unitutor.repository.EnrollmentRepository;
import com.unitutor.grupo3_unitutor.repository.TutoringSessionRepository;
import com.unitutor.grupo3_unitutor.view.ConsoleIO;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import com.unitutor.grupo3_unitutor.model.User;
import com.unitutor.grupo3_unitutor.model.Enrollment;
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
    private static final String STATUS_CANCELLED = "CANCELLED";
    private final EnrollmentRepository enrollmentRepository;

    public TutoringSessionService(TutoringSessionRepository sessionRepository, EnrollmentRepository enrollmentRepository,
                                  ConsoleIO consoleIO, AuthorizationService authorizationService) {
        this.sessionRepository = sessionRepository;
        this.enrollmentRepository = enrollmentRepository;
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

    public List<TutoringSession> searchSessions(String subject, LocalDateTime date, String modality) {

        if (subject != null && !subject.isBlank()) {
            return sessionRepository.findBySubjectIgnoreCase(subject);
        }

        if (date != null) {
            LocalDateTime start = date.withHour(0).withMinute(0);
            LocalDateTime end = date.withHour(23).withMinute(59);
            return sessionRepository.findByStartTimeBetween(start, end);
        }

        if (modality != null && !modality.isBlank()) {
            return sessionRepository.findByModalityIgnoreCase(modality);
        }

        return List.of();
    }

    public List<TutoringSession> getProfessorActiveSessions(User professor) {
        authorizationService.checkIsProfessor(professor);
        return sessionRepository.findByProfessorAndStartTimeAfterAndStatusNot(professor, LocalDateTime.now(), STATUS_CANCELLED);

    }

    public boolean cancelSessionById(User professor, Long sessionId) {

        authorizationService.checkIsProfessor(professor);

        Optional<TutoringSession> sessionOpt = sessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            return false;
        }

        TutoringSession session = sessionOpt.get();

        if (!session.getProfessor().getDni().equals(professor.getDni())) {
            return false;
        }

        if (STATUS_CANCELLED.equals(session.getStatus())) {
            return true;
        }

        try {

            session.setStatus(STATUS_CANCELLED);
            sessionRepository.save(session);
            List<Enrollment> enrollments = enrollmentRepository.findBySession_Id(sessionId);
            LocalDateTime now = LocalDateTime.now();

            for (Enrollment enrollment : enrollments) {
                if (!STATUS_CANCELLED.equals(enrollment.getStatus())) {
                    enrollment.setStatus(STATUS_CANCELLED);
                    enrollment.setCancellationDate(now);
                    enrollment.setCancelledBy(professor.getDni());
                    enrollmentRepository.save(enrollment);
                }
            }
            return true;

        } catch (DataAccessException e) {
            logger.error("Database error during enrollment cancellation for session id={}", sessionId, e);
            return false;
        }
    }
}