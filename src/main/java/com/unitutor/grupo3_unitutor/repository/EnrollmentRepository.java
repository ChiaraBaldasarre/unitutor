package com.unitutor.grupo3_unitutor.repository;

import com.unitutor.grupo3_unitutor.model.Enrollment;
import com.unitutor.grupo3_unitutor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    @Query("SELECT e FROM Enrollment e WHERE e.student = :student ORDER BY e.session.startTime DESC")
    List<Enrollment> findHistory(@Param("student") User student);
    Optional<Enrollment> findByStudentAndSession_Id(User student, Long sessionId);
    List<Enrollment> findBySession_Id(Long sessionId);
}