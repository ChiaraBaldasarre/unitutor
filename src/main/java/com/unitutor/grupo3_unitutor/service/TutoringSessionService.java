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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TutoringSessionService {

    private static final Logger logger = LoggerFactory.getLogger(TutoringSessionService.class);

    private final TutoringSessionRepository sessionRepository;
    private final ConsoleIO consoleIO;
    private final AuthorizationService authorizationService;
    private final EnrollmentRepository enrollmentRepository;
    private static final String STATUS_CANCELLED = "CANCELLED";
    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_CANCELLED_BY_PROFESSOR = "CANCELLED_BY_PROFESSOR";

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

    @Transactional
    public boolean cancelSessionById(User professor, Long sessionId) {

        authorizationService.checkIsProfessor(professor);

        Optional<TutoringSession> sessionOpt = sessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            consoleIO.writeError("Error: Tutoring Session ID " + sessionId + " not found.");
            return false;
        }

        TutoringSession session = sessionOpt.get();

        if (!session.getProfessor().getDni().equals(professor.getDni())) {
            consoleIO.writeError("Error: You can only cancel sessions you have created.");
            return false;
        }

        if (STATUS_CANCELLED.equals(session.getStatus())) {
            consoleIO.writeError("Error: Tutoring Session ID " + sessionId + " is already cancelled.");
            return false;
        }

        if (session.getStartTime().isBefore(LocalDateTime.now())) {
            consoleIO.writeError("Error: Cannot cancel a session that has already started or passed.");
            return false;
        }

        try {

            session.setStatus(STATUS_CANCELLED);
            sessionRepository.save(session);
            List<Enrollment> enrollments = enrollmentRepository.findBySession_Id(sessionId);

            for (Enrollment enrollment : enrollments) {
                if (!STATUS_CANCELLED.equals(enrollment.getStatus())) {
                    enrollment.setStatus(STATUS_CANCELLED);
                    enrollment.setCancellationDate(LocalDateTime.now());
                    enrollment.setCancelledBy(professor.getFirstName() + " " + professor.getLastName());
                    enrollmentRepository.save(enrollment);
                }
            }
            return true;

        } catch (DataAccessException e) {
            logger.error("Database error during enrollment cancellation for session id={}", sessionId, e);
            consoleIO.writeError("Database error during session cancellation. Please try again.");
            throw new RuntimeException("Database error.", e);

        } catch (Exception e) {
            logger.error("Unexpected error during session cancellation", e);
            consoleIO.writeError("An unexpected error occurred. Please contact support.");
            throw new RuntimeException("Unexpected error.", e);
        }
    }

    public boolean enrollStudent(User student, Long sessionId) {

        if (student == null) {
            consoleIO.writeError("Error: Student not found.");
            return false;
        }

        Optional<TutoringSession> sessionOpt = sessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            consoleIO.writeError("Error: Tutoring session not found.");
            return false;
        }

        TutoringSession session = sessionOpt.get();

        if (STATUS_CANCELLED.equalsIgnoreCase(session.getStatus())) {
            consoleIO.writeError("Error: This tutoring session is CANCELLED.");
            return false;
        }

        if (session.getStartTime().isBefore(LocalDateTime.now())) {
            consoleIO.writeError("Error: You cannot enroll in a past session.");
            return false;
        }

        Optional<Enrollment> existingOpt = enrollmentRepository.findByStudentAndSession_Id(student, sessionId);

        if (existingOpt.isPresent()) {
            Enrollment existing = existingOpt.get();

            if (!STATUS_CANCELLED.equalsIgnoreCase(existing.getStatus())) {
                consoleIO.writeError("Error: You are already enrolled in this session.");
                return false;
            }

            existing.setStatus(STATUS_ACTIVE);
            existing.setEnrollmentDate(LocalDateTime.now());
            existing.setCancellationDate(null);
            existing.setCancelledBy(null);
            enrollmentRepository.save(existing);

            consoleIO.write("Enrollment reactivated successfully.");
            return true;
        }

        List<Enrollment> all = enrollmentRepository.findBySession_Id(sessionId);
        long activeCount = all.stream()
                .filter(e -> e.getStatus() == null || !STATUS_CANCELLED.equalsIgnoreCase(e.getStatus()))
                .count();

        if (activeCount >= session.getMaxCapacity()) {
            consoleIO.writeError("Error: This tutoring session is already full.");
            return false;
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setSession(session);
        enrollment.setStatus(STATUS_ACTIVE);
        enrollment.setEnrollmentDate(LocalDateTime.now());

        enrollmentRepository.save(enrollment);

        consoleIO.write("Enrollment successful!");
        return true;
    }
}