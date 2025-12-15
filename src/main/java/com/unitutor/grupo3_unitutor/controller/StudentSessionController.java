package com.unitutor.grupo3_unitutor.controller;

import com.unitutor.grupo3_unitutor.model.SessionHistory;
import com.unitutor.grupo3_unitutor.model.TutoringSession;
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

  private boolean showResultsAndEnrollIfRequested(User student, List<TutoringSession> results) {

    if (results == null || results.isEmpty()) {
      consoleIO.write("No tutoring sessions found.");
      return false;
    }

    consoleIO.write("--- Results ---");
    for (int i = 0; i < results.size(); i++) {
      TutoringSession s = results.get(i);
      consoleIO.write(
              (i + 1) + ") " +
                      s.getSubject() + " | " +
                      s.getStartTime() + " | " +   // if your getter name differs, change it (e.g., getTimeStart())
                      s.getModality()
      );
    }

    String decision = consoleIO.readLine("\nDo you want to enroll in one of these sessions? (Y/N): ")
            .trim().toUpperCase();

    if (!"Y".equals(decision)) {
      return false;
    }

    String input = consoleIO.readLine("Select a session number (1-" + results.size() + ", 0 to cancel): ")
            .trim();

    try {
      int choice = Integer.parseInt(input);

      if (choice == 0) return false;

      if (choice < 1 || choice > results.size()) {
        consoleIO.writeError("Invalid selection.");
        return false;
      }

      Long sessionId = results.get(choice - 1).getId(); // real ID hidden from student
      return tutoringSessionService.enrollStudent(student, sessionId);

    } catch (NumberFormatException e) {
      consoleIO.writeError("Please enter a valid number.");
      return false;
    }
  }



  public String searchAndBookSessions(User student) {

    String opt;
    while (true) {

      studentMenuView.showSearchFilters();

      opt = consoleIO.readLine("Select filter [0-3] (0 to go back): ").trim();

      switch (opt) {

        case "1": {
          String subject = consoleIO.readLine("Enter subject to search: ").trim();
          List<TutoringSession> results = tutoringSessionService.searchSessions(subject, null, null);

          if (showResultsAndEnrollIfRequested(student, results)) return opt;
          break;
        }

        case "2": {
          String dateInput = consoleIO.readLine("Enter date (YYYY-MM-DD): ").trim();
          try {
            LocalDateTime date = LocalDateTime.parse(dateInput + "T00:00:00");
            List<TutoringSession> results = tutoringSessionService.searchSessions(null, date, null);

            if (showResultsAndEnrollIfRequested(student, results)) return opt;
          } catch (Exception e) {
            consoleIO.writeError("Error: Date must use format YYYY-MM-DD.");
          }
          break;
        }

        case "3": {
          String mod = consoleIO.readLine("Enter modality (ONLINE or PRESENCIAL): ").trim().toUpperCase();

          if (!"ONLINE".equals(mod) && !"PRESENCIAL".equals(mod)) {
            consoleIO.writeError("Error: Modality must be ONLINE or PRESENCIAL.");
            break;
          }

          List<TutoringSession> results = tutoringSessionService.searchSessions(null, null, mod);

          if (showResultsAndEnrollIfRequested(student, results)) return opt;
          break;
        }

        case "0":
          return opt;

        default:
          consoleIO.writeError("Invalid option. Choose 0, 1, 2, or 3.");
      }

    }
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
