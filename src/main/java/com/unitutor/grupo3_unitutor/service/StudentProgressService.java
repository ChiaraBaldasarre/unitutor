package com.unitutor.grupo3_unitutor.service;

import com.unitutor.grupo3_unitutor.model.SessionHistory;
import com.unitutor.grupo3_unitutor.model.Enrollment;
import com.unitutor.grupo3_unitutor.model.User;
import com.unitutor.grupo3_unitutor.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentProgressService {

    private final EnrollmentRepository enrollmentRepository;

    public StudentProgressService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }


    public List<SessionHistory> getTutoringHistory(User student) {

        List<Enrollment> enrollments = enrollmentRepository.findHistory(student);
        List<SessionHistory> historyDTOs = new ArrayList<>();

        for (Enrollment e : enrollments) {

            User prof = e.getSession().getProfessor();
            String tutorName = (prof != null) ? prof.getFirstName() + " " + prof.getLastName() : "Unknown tutor";
            SessionHistory dto = new SessionHistory(
                    e.getSession().getId(),
                    e.getSession().getSubject(),
                    tutorName,
                    e.getSession().getStartTime(),
                    e.getStatus()
            );

            historyDTOs.add(dto);
        }
        return historyDTOs;
    }
}