package com.unitutor.grupo3_unitutor.model;

import java.time.LocalDateTime;

public class SessionHistory {
    private Long sessionId;
    private String subject;
    private String professorName;
    private LocalDateTime dateTime;
    private String status;

    public SessionHistory(Long sessionId, String subject, String professorName, LocalDateTime dateTime, String status) {
        this.sessionId = sessionId;
        this.subject = subject;
        this.professorName = professorName;
        this.dateTime = dateTime;
        this.status = status;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}