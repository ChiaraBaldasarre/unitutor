package com.unitutor.grupo3_unitutor.service;

import com.unitutor.grupo3_unitutor.model.TutoringSession;
import com.unitutor.grupo3_unitutor.repository.EnrollmentRepository;
import com.unitutor.grupo3_unitutor.repository.TutoringSessionRepository;
import com.unitutor.grupo3_unitutor.view.ConsoleIO;
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
    private static final String STATUS_ACTIVE = "ACTIVE";
    private final EnrollmentRepository enrollmentRepository;

    public TutoringSessionService(TutoringSessionRepository sessionRepository,
                                  EnrollmentRepository enrollmentRepository,
                                  ConsoleIO consoleIO, AuthorizationService authorizationService) {
        this.sessionRepository = sessionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.consoleIO = consoleIO;
        this.authorizationService = authorizationService;
    }

    public Optional<TutoringSession> createSession(User professor, TutoringSession session) {
        try {
            authorizationService.checkIsProfessor(professor);

            session.setStatus(STATUS_ACTIVE);
            TutoringSession savedSession = sessionRepository.save(session);
            return Optional.of(savedSession);
        } catch (Exception e) {
            logger.error("Database error creating session for professor {}", professor.getDni(), e);
            return Optional.empty();
        }
    }

    public List<TutoringSession> getProfessorActiveSessions(User professor) {
        authorizationService.checkIsProfessor(professor);
        return sessionRepository.findByProfessorAndStartTimeAfterAndStatusNot(
                professor, LocalDateTime.now(), STATUS_CANCELLED);
    }

    public boolean cancelSessionById(User professor, Long sessionId) {
        authorizationService.checkIsProfessor(professor);

        Optional<TutoringSession> sessionOpt = sessionRepository.findById(sessionId);

        if (sessionOpt.isEmpty()) {
            return false;
        }

        TutoringSession session = sessionOpt.get();

        if (!session.getProfessor().getDni().equals(professor.getDni())) {
            consoleIO.writeError("Access Denied: You can only cancel sessions you created.");
            return false;
        }

        if (STATUS_CANCELLED.equalsIgnoreCase(session.getStatus())) {
            consoleIO.write("Info: The session is already cancelled.");
            return true;
        }

        session.setStatus(STATUS_CANCELLED);
        sessionRepository.save(session);

        List<Enrollment> enrollments = enrollmentRepository.findBySession_Id(sessionId);
        for (Enrollment e : enrollments) {
            if (!STATUS_CANCELLED.equalsIgnoreCase(e.getStatus())) {
                e.setStatus(STATUS_CANCELLED);
                e.setCancellationDate(LocalDateTime.now());
                e.setCancelledBy("PROFESSOR_SESSION_CANCELLED");
                enrollmentRepository.save(e);
            }
        }

        return true;
    }

    public List<TutoringSession> searchSessions(String subject, LocalDateTime date, String modality) {

        if (subject != null) {
            return sessionRepository.findBySubjectIgnoreCase(subject);

        } else if (date != null) {
            if (date.getHour() == 0 && date.getMinute() == 0 && date.getSecond() == 0 && date.getNano() == 0) {
                LocalDateTime startOfDay = date;
                LocalDateTime endOfDay = date.withHour(23).withMinute(59).withSecond(59);
                return sessionRepository.findByStartTimeBetween(startOfDay, endOfDay);
            } else {
                return sessionRepository.findByStartTimeAfter(date);
            }

        } else if (modality != null) {
            return sessionRepository.findByModalityIgnoreCase(modality);
        }
        return sessionRepository.findAll();
    }

    public Optional<TutoringSession> findSessionById(Long id) {
        return sessionRepository.findById(id);
    }

    public boolean enrollStudent(User student, Long sessionId) {
        Optional<TutoringSession> sessionOpt = sessionRepository.findById(sessionId);

        if (sessionOpt.isEmpty()) {
            consoleIO.writeError("Error: Tutoring Session ID " + sessionId + " not found.");
            return false;
        }

        TutoringSession session = sessionOpt.get();
        LocalDateTime now = LocalDateTime.now();

        if (session.getStartTime().isBefore(now)) {
            consoleIO.writeError("Error: Cannot enroll in a session that has already started or passed.");
            return false;
        }

        Optional<Enrollment> existingEnrollment = enrollmentRepository.findByStudentAndSession_Id(student, sessionId);

        if (existingEnrollment.isPresent()) {
            Enrollment existing = existingEnrollment.get();

            if (STATUS_ACTIVE.equalsIgnoreCase(existing.getStatus())) {
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