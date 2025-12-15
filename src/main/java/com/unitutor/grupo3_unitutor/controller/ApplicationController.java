package com.unitutor.grupo3_unitutor.controller;

import com.unitutor.grupo3_unitutor.model.User;
import com.unitutor.grupo3_unitutor.view.ConsoleIO;
import com.unitutor.grupo3_unitutor.view.ProfessorMenuView;
import com.unitutor.grupo3_unitutor.view.StudentMenuView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class ApplicationController {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);
  private final AuthenticationController authenticationController;
  private final StudentSessionController studentSessionController;
  private final ProfessorSessionController professorSessionController;
  private final EnrollmentCancellationController enrollmentCancellationController;
  private final ConsoleIO consoleIO;
  private final StudentMenuView studentMenuView;
  private final ProfessorMenuView professorMenuView;

  public ApplicationController(AuthenticationController authenticationController,
      StudentSessionController studentSessionController,
      ProfessorSessionController professorSessionController,
      EnrollmentCancellationController enrollmentCancellationController,
      ConsoleIO consoleIO,
      StudentMenuView studentMenuView,
      ProfessorMenuView professorMenuView) {
    this.authenticationController = authenticationController;
    this.studentSessionController = studentSessionController;
    this.professorSessionController = professorSessionController;
    this.enrollmentCancellationController = enrollmentCancellationController;
    this.consoleIO = consoleIO;
    this.studentMenuView = studentMenuView;
    this.professorMenuView = professorMenuView;
  }

  public void run() {
    Optional<User> loggedInUser = authenticationController.login();

    if (loggedInUser.isPresent()) {
      showMainMenu(loggedInUser.get());
    }

    consoleIO.closeScanner();
  }

  private void showMainMenu(User user) {
    String roleName = user.getRole().getName().toUpperCase();
    boolean exit = false;
    String opt1 = "";
    while (!exit) {
      try {
        if ("STUDENT".equals(roleName)) {
          studentMenuView.showMenu();
        } else if ("PROFESSOR".equals(roleName)) {
          professorMenuView.showMenu();
        } else {
          consoleIO.writeError("Unrecognized role. Logging out...");
          return;
        }

        String option = consoleIO.readLine("Select an option [0-3] (0 to log out): ").trim();

        switch (option) {
          case "1":
            if ("STUDENT".equals(roleName)) {
              opt1 = studentSessionController.searchAndBookSessions(user);
            } else if ("PROFESSOR".equals(roleName)) {
              professorSessionController.createSession(user);
            }
            break;

          case "2":
            if ("STUDENT".equals(roleName)) {
              studentSessionController.viewTutoringHistory(user);
            } else if ("PROFESSOR".equals(roleName)) {
              professorSessionController.manageActiveSessions(user);
            }
            break;

          case "3":
            if ("STUDENT".equals(roleName)) {
              enrollmentCancellationController.cancelEnrollment(user);
            } else if ("PROFESSOR".equals(roleName)) {
              professorSessionController.uploadGrades(user);
            }
            break;

          case "0":
            consoleIO.write("\nSigning out...");
            exit = true;
            break;

          default:
            consoleIO.writeError("Invalid option. Choose 0, 1, 2, or 3.");
        }

        if (!exit) {
          if (opt1.equals("0") == false) {
            consoleIO.readLine("\nPress ENTER to return to the menu...");
          }
        } else {
          run();
        }
      } catch (Exception e) {
        logger.error("Unexpected error in main menu", e);
        consoleIO.writeError("An unexpected error occurred. Contact support.");
        return;
      }
    }
  }
}
