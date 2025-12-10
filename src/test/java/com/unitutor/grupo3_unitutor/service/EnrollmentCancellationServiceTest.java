package com.unitutor.grupo3_unitutor.service;

import com.unitutor.grupo3_unitutor.model.Enrollment;
import com.unitutor.grupo3_unitutor.model.TutoringSession;
import com.unitutor.grupo3_unitutor.model.User;
import com.unitutor.grupo3_unitutor.repository.EnrollmentRepository;
import com.unitutor.grupo3_unitutor.repository.TutoringSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataAccessResourceFailureException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EnrollmentCancellationServiceTest {

  private EnrollmentRepository enrollmentRepository;
  private TutoringSessionRepository tutoringSessionRepository;
  private EnrollmentCancellationService service;
  private User student;

  @BeforeEach
  void setUp() {
    enrollmentRepository = Mockito.mock(EnrollmentRepository.class);
    tutoringSessionRepository = Mockito.mock(TutoringSessionRepository.class);
    service = new EnrollmentCancellationService(enrollmentRepository, tutoringSessionRepository);

    student = new User();
    student.setDni("12345678");
    student.setFirstName("Ana");
  }

  @Test
  void cancelEnrollment_successWhenMoreThan24Hours() {
    LocalDateTime start = LocalDateTime.now().plusHours(30);
    TutoringSession session = buildSession(1L, "Math", start);
    Enrollment enrollment = buildEnrollment(student, session, "ACTIVE");

    when(tutoringSessionRepository.findById(1L)).thenReturn(Optional.of(session));
    when(enrollmentRepository.findByStudentAndSession_Id(student, 1L)).thenReturn(Optional.of(enrollment));
    when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> invocation.getArgument(0));

    EnrollmentCancellationService.CancellationResult result = service.cancelEnrollment(student, 1L);

    assertTrue(result.isSuccess());
    assertTrue(result.getMessage().contains("Math"));
    assertEquals("CANCELLED", enrollment.getStatus());
    assertNotNull(enrollment.getCancellationDate());
    assertEquals("12345678", enrollment.getCancelledBy());
    verify(enrollmentRepository).save(enrollment);
  }

  @Test
  void cancelEnrollment_blockedWhenWithin24Hours() {
    LocalDateTime start = LocalDateTime.now().plusHours(10);
    TutoringSession session = buildSession(2L, "Physics", start);
    Enrollment enrollment = buildEnrollment(student, session, "ACTIVE");

    when(tutoringSessionRepository.findById(2L)).thenReturn(Optional.of(session));
    when(enrollmentRepository.findByStudentAndSession_Id(student, 2L)).thenReturn(Optional.of(enrollment));

    EnrollmentCancellationService.CancellationResult result = service.cancelEnrollment(student, 2L);

    assertFalse(result.isSuccess());
    assertTrue(result.getMessage().contains("Cancellation not allowed"));
    assertEquals("ACTIVE", enrollment.getStatus());
    verify(enrollmentRepository, never()).save(any());
  }

  @Test
  void cancelEnrollment_failsWhenCourseOrEnrollmentMissing() {
    when(tutoringSessionRepository.findById(3L)).thenReturn(Optional.empty());

    EnrollmentCancellationService.CancellationResult missingCourse = service.cancelEnrollment(student, 3L);
    assertFalse(missingCourse.isSuccess());
    assertTrue(missingCourse.getMessage().contains("does not exist"));
    verify(enrollmentRepository, never()).findByStudentAndSession_Id(any(), any());

    TutoringSession session = buildSession(4L, "Chemistry", LocalDateTime.now().plusHours(30));
    when(tutoringSessionRepository.findById(4L)).thenReturn(Optional.of(session));
    when(enrollmentRepository.findByStudentAndSession_Id(student, 4L)).thenReturn(Optional.empty());

    EnrollmentCancellationService.CancellationResult notEnrolled = service.cancelEnrollment(student, 4L);
    assertFalse(notEnrolled.isSuccess());
    assertTrue(notEnrolled.getMessage().contains("not enrolled"));
    verify(enrollmentRepository, never()).save(any());
  }

  @Test
  void cancelEnrollment_idempotentWhenAlreadyCancelled() {
    LocalDateTime start = LocalDateTime.now().plusHours(50);
    TutoringSession session = buildSession(5L, "Biology", start);
    Enrollment enrollment = buildEnrollment(student, session, "CANCELLED");
    enrollment.setCancellationDate(LocalDateTime.now().minusDays(1));

    when(tutoringSessionRepository.findById(5L)).thenReturn(Optional.of(session));
    when(enrollmentRepository.findByStudentAndSession_Id(student, 5L)).thenReturn(Optional.of(enrollment));

    EnrollmentCancellationService.CancellationResult result = service.cancelEnrollment(student, 5L);

    assertFalse(result.isSuccess());
    assertTrue(result.getMessage().toLowerCase().contains("already cancelled"));
    verify(enrollmentRepository, never()).save(any());
  }

  @Test
  void cancelEnrollment_handlesDatabaseFailure() {
    LocalDateTime start = LocalDateTime.now().plusHours(30);
    TutoringSession session = buildSession(6L, "History", start);
    Enrollment enrollment = buildEnrollment(student, session, "ACTIVE");

    when(tutoringSessionRepository.findById(6L)).thenReturn(Optional.of(session));
    when(enrollmentRepository.findByStudentAndSession_Id(student, 6L)).thenReturn(Optional.of(enrollment));
    when(enrollmentRepository.save(any())).thenThrow(new DataAccessResourceFailureException("DB down"));

    EnrollmentCancellationService.CancellationResult result = service.cancelEnrollment(student, 6L);

    assertFalse(result.isSuccess());
    assertTrue(result.getMessage().contains("system error"));
  }

  private TutoringSession buildSession(Long id, String subject, LocalDateTime start) {
    TutoringSession session = new TutoringSession();
    session.setId(id);
    session.setSubject(subject);
    session.setStartTime(start);
    session.setDurationMinutes(60);
    session.setMaxCapacity(10);
    session.setModality("ONLINE");
    return session;
  }

  private Enrollment buildEnrollment(User student, TutoringSession session, String status) {
    Enrollment enrollment = new Enrollment();
    enrollment.setId(100L);
    enrollment.setStudent(student);
    enrollment.setSession(session);
    enrollment.setStatus(status);
    enrollment.setEnrollmentDate(LocalDateTime.now());
    return enrollment;
  }
}
