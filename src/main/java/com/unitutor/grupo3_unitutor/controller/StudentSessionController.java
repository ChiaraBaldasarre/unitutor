package com.unitutor.grupo3_unitutor.controller;

import com.unitutor.grupo3_unitutor.model.SessionHistory;
import com.unitutor.grupo3_unitutor.model.User;
import com.unitutor.grupo3_unitutor.service.StudentProgressService;
import com.unitutor.grupo3_unitutor.service.TutoringSessionService;
import com.unitutor.grupo3_unitutor.view.ConsoleIO;
import com.unitutor.grupo3_unitutor.view.StudentMenuView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class StudentSessionController {

  private static final Logger logger = LoggerFactory.getLogger(StudentSessionController.class);
  private final ConsoleIO consoleIO;
  private final StudentMenuView studentMenuView;
  private final StudentProgressService studentProgressService;
  private final TutoringSessionService tutoringSessionService;

  public StudentSessionController(ConsoleIO consoleIO, StudentMenuView studentMenuView,
      StudentProgressService studentProgressService, TutoringSessionService tutoringSessionService) {
    this.consoleIO = consoleIO;
    this.studentMenuView = studentMenuView;
    this.studentProgressService = studentProgressService;
    this.tutoringSessionService = tutoringSessionService;
  }

  public String searchAndBookSessions(User student) {
    boolean exit = false;
    String opt = "";
    while (!exit) {
      studentMenuView.showSearchFilters();

      opt = consoleIO.readLine("Select filter [0-3] (0 to go back): ").trim();

      switch (opt) {
        case "1":
          searchBySubject();
          break;

        case "2":
          searchByDate();
          break;

        case "3":
          searchByModality();
          break;

        case "0":
          exit = true;
          break;

        default:
          consoleIO.writeError("Invalid option. Choose 0, 1, 2, or 3.");
      }
      System.out.println(opt);
      if (!exit) {
        consoleIO.readLine("\nPress ENTER to continue...");
      }
    }
    return opt;
  }

  public void viewTutoringHistory(User student) {
    try {
      consoleIO.write("\nRetrieving Academic History...");
      List<SessionHistory> history = studentProgressService.getTutoringHistory(student);
      studentMenuView.displayHistory(history);
    } catch (Exception e) {
      logger.error("Error retrieving tutoring history", e);
      consoleIO.writeError("An unexpected error occurred. Contact support.");
    }
  }

  private void searchBySubject() {
    try {
      String subject = consoleIO.readLine("Enter subject to search: ").trim();
      var bySubject = tutoringSessionService.searchSessions(subject, null, null);

      if (bySubject.isEmpty()) {
        consoleIO.write("No tutoring sessions found for that subject.");
      } else {
        consoleIO.write("--- Results ---");
        bySubject.forEach(s -> consoleIO.write(s.getId() + " | " + s.getSubject() + " | "
            + s.getStartTime() + " | " + s.getModality()));
      }
    } catch (Exception e) {
      logger.error("Error searching by subject", e);
      consoleIO.writeError("An unexpected error occurred. Contact support.");
    }
  }

  private void searchByDate() {
    try {
      String dateInput = consoleIO.readLine("Enter date (YYYY-MM-DD): ").trim();
      LocalDateTime date = LocalDateTime.parse(dateInput + "T00:00:00");
      var byDate = tutoringSessionService.searchSessions(null, date, null);

      if (byDate.isEmpty()) {
        consoleIO.write("No tutoring sessions found.");
      } else {
        consoleIO.write("--- Results ---");
        byDate.forEach(s -> consoleIO.write(s.getId() + " | " + s.getSubject() + " | "
            + s.getStartTime() + " | " + s.getModality()));
      }
    } catch (Exception e) {
      logger.error("Error searching by date", e);
      consoleIO.writeError("Error: Date must use format YYYY-MM-DD.");
    }
  }

  private void searchByModality() {
    try {
      String mod = consoleIO.readLine("Enter modality (ONLINE or PRESENCIAL): ").toUpperCase().trim();

      if (!"ONLINE".equals(mod) && !"PRESENCIAL".equals(mod)) {
        consoleIO.writeError("Error: Modality must be ONLINE or PRESENCIAL.");
        return;
      }

      var byMod = tutoringSessionService.searchSessions(null, null, mod);

      if (byMod.isEmpty()) {
        consoleIO.write("No tutoring sessions found.");
      } else {
        consoleIO.write("--- Results ---");
        byMod.forEach(s -> consoleIO.write(s.getId() + " | " + s.getSubject() + " | " + s.getStartTime()
            + " | " + s.getModality()));
      }
    } catch (Exception e) {
      logger.error("Error searching by modality", e);
      consoleIO.writeError("An unexpected error occurred. Contact support.");
    }
  }
}
