package com.unitutor.grupo3_unitutor.service;

import com.unitutor.grupo3_unitutor.model.Enrollment;
import com.unitutor.grupo3_unitutor.model.TutoringSession;
import com.unitutor.grupo3_unitutor.model.User;
import com.unitutor.grupo3_unitutor.repository.EnrollmentRepository;
import com.unitutor.grupo3_unitutor.repository.TutoringSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class EnrollmentCancellationService {

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentCancellationService.class);
    private static final String STATUS_CANCELLED = "CANCELLED";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final EnrollmentRepository enrollmentRepository;
    private final TutoringSessionRepository tutoringSessionRepository;

    public EnrollmentCancellationService(EnrollmentRepository enrollmentRepository,
            TutoringSessionRepository tutoringSessionRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.tutoringSessionRepository = tutoringSessionRepository;
    }

    public CancellationResult cancelEnrollment(User student, Long sessionId) {
        try {
            @SuppressWarnings("null")
            Optional<TutoringSession> sessionOpt = tutoringSessionRepository.findById(sessionId);
            if (sessionOpt.isEmpty()) {
                return CancellationResult.failure("Error: The requested course does not exist.");
            }

            TutoringSession session = sessionOpt.get();
            Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByStudentAndSession_Id(student, sessionId);

            if (enrollmentOpt.isEmpty()) {
                return CancellationResult.failure("Error: You are not enrolled in this course.");
            }

            Enrollment enrollment = enrollmentOpt.get();

            if (STATUS_CANCELLED.equalsIgnoreCase(enrollment.getStatus())) {
                LocalDateTime cancelledAt = enrollment.getCancellationDate();
                String when = cancelledAt != null ? cancelledAt.format(FORMATTER) : "previously";
                return CancellationResult
                        .failure("Your enrollment was already cancelled on " + when + ". No changes made.");
            }

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime cutoff = session.getStartTime().minusHours(24);

            if (!now.isBefore(cutoff)) {
                String cutoffText = cutoff.format(FORMATTER);
                String startText = session.getStartTime().format(FORMATTER);
                return CancellationResult.failure("Cancellation not allowed. Cutoff was " + cutoffText
                        + ". Session starts at " + startText + ".");
            }

            enrollment.setStatus(STATUS_CANCELLED);
            enrollment.setCancellationDate(now);
            enrollment.setCancelledBy(student.getDni());
            enrollmentRepository.save(enrollment);

            String message = "Cancellation successful for '" + session.getSubject() + "' at " + now.format(FORMATTER)
                    + ".";
            return CancellationResult.success(message);

        } catch (DataAccessException e) {
            logger.error("Database error during enrollment cancellation for sessionId={} and student={}", sessionId,
                    student.getDni(), e);
            return CancellationResult
                    .failure("Could not complete cancellation due to a system error. Please try again later.");
        } catch (Exception e) {
            logger.error("Unexpected error during enrollment cancellation", e);
            return CancellationResult.failure("An unexpected error occurred. Please contact support.");
        }
    }

    public static class CancellationResult {
        private final boolean success;
        private final String message;

        private CancellationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public static CancellationResult success(String message) {
            return new CancellationResult(true, message);
        }

        public static CancellationResult failure(String message) {
            return new CancellationResult(false, message);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}
