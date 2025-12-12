package com.unitutor.grupo3_unitutor.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tutoring_sessions")
public class TutoringSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "max_capacity", nullable = false)
    private int maxCapacity;

    @ManyToOne
    @JoinColumn(name = "professor_dni", nullable = false)
    private User professor;

    @Column(name = "modality", nullable = false)
    private String modality;

    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";

    public Long getId() { return id; }
    public void setId(Long id) {
        this.id = id;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public int getDurationMinutes() {
        return durationMinutes;
    }
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    public int getMaxCapacity() {
        return maxCapacity;
    }
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
    public User getProfessor() {
        return professor;
    }
    public void setProfessor(User professor) {
        this.professor = professor;
    }
    public String getModality() { return modality; }
    public void setModality(String modality) { this.modality = modality; }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
