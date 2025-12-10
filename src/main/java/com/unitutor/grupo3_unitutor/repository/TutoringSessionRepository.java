package com.unitutor.grupo3_unitutor.repository;

import com.unitutor.grupo3_unitutor.model.TutoringSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TutoringSessionRepository extends JpaRepository<TutoringSession, Long> {
    List<TutoringSession> findBySubjectIgnoreCase(String subject);
    List<TutoringSession> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    List<TutoringSession> findByModalityIgnoreCase(String modality);
}