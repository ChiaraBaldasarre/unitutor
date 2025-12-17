package com.unitutor.grupo3_unitutor.controller;

import com.unitutor.grupo3_unitutor.model.User;
import com.unitutor.grupo3_unitutor.service.ProfessorFormService;
import com.unitutor.grupo3_unitutor.view.ConsoleIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class ProfessorSessionController {

  private static final Logger logger = LoggerFactory.getLogger(ProfessorSessionController.class);
  private final ProfessorFormService professorFormService;
  private final ConsoleIO consoleIO;
  private final ConsoleController consoleController;

  public ProfessorSessionController(ProfessorFormService professorFormService, ConsoleIO consoleIO, ConsoleController consoleController) {
    this.professorFormService = professorFormService;
    this.consoleIO = consoleIO;
    this.consoleController = consoleController;
  }

  public void createSession(User professor) {

    try {
      professorFormService.createTutoringSession(professor);

    } catch (Exception e) {
      logger.error("Error creating tutoring session", e);
      consoleIO.writeError("An unexpected error occurred. Contact support.");
    }
  }

  public void manageActiveSessions(User professor) {
    consoleController.manageProfessorSessionManagement(professor);
  }
}