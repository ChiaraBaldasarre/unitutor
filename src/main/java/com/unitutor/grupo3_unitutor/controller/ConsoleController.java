package com.unitutor.grupo3_unitutor.controller;

import com.unitutor.grupo3_unitutor.model.SessionHistory;
import com.unitutor.grupo3_unitutor.model.TutoringSession;
import com.unitutor.grupo3_unitutor.model.User;
import com.unitutor.grupo3_unitutor.service.ProfessorFormService;
import com.unitutor.grupo3_unitutor.service.EnrollmentCancellationService;
import com.unitutor.grupo3_unitutor.service.StudentProgressService;
import com.unitutor.grupo3_unitutor.service.TutoringSessionService;
import com.unitutor.grupo3_unitutor.view.ConsoleIO;
import com.unitutor.grupo3_unitutor.view.ProfessorMenuView;
import com.unitutor.grupo3_unitutor.view.StudentMenuView;
import com.unitutor.grupo3_unitutor.service.UserService;
import com.unitutor.grupo3_unitutor.utils.DniValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.List;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class ConsoleController {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleController.class);
    private final UserService userService;
    private final ProfessorFormService professorFormService;
    private final EnrollmentCancellationService enrollmentCancellationService;
    private final ConsoleIO consoleIO;
    private User currentUser;
    private final StudentMenuView studentMenuView;
    private final ProfessorMenuView professorMenuView;
    private final StudentProgressService studentProgressService;
    private final TutoringSessionService tutoringSessionService;

    public ConsoleController(UserService userService, StudentMenuView studentMenuView,
            ProfessorMenuView professorMenuView, ProfessorFormService professorFormService,
            EnrollmentCancellationService enrollmentCancellationService, ConsoleIO consoleIO,
            StudentProgressService studentProgressService, TutoringSessionService tutoringSessionService) {
        this.userService = userService;
        this.professorFormService = professorFormService;
        this.enrollmentCancellationService = enrollmentCancellationService;
        this.consoleIO = consoleIO;
        this.studentMenuView = studentMenuView;
        this.professorMenuView = professorMenuView;
        this.studentProgressService = studentProgressService;
        this.tutoringSessionService = tutoringSessionService;
    }

    public void run() {
        consoleIO.write("UNI-TUTOR");
        boolean applicationRunning = true;
        while (applicationRunning) {

            String action = showLoginMenu();

            if ("EXIT".equals(action)) {
                applicationRunning = false;
            }

            if (currentUser != null) {

                showPrincipalMenu(currentUser);
            }

        }

        consoleIO.write("Exiting the system...");
        consoleIO.closeScanner();
    }

    private String showLoginMenu() {
        boolean authenticated = false;
        consoleIO.write("\nAuthentication Module");
        consoleIO.write("Type 'exit' at any time to leave.\n");

        while (!authenticated) {
            try {
                String dni = consoleIO.readLine("Enter DNI (8 digits) or type 'exit' to quit: ").trim();

                if (dni.equalsIgnoreCase("exit")) {
                    consoleIO.write("Exiting the system...");
                    return "EXIT";
                }

                String errorMsg = DniValidator.getValidationError(dni);

                if (errorMsg != null) {
                    consoleIO.writeError("Error: " + errorMsg + " Expected format: 8 numeric digits.");
                    continue;
                }

                Optional<User> optUser = userService.authenticateByDni(dni);

                if (optUser.isEmpty()) {
                    consoleIO.writeError("Error: DNI not found. Please verify and try again.");
                } else {
                    this.currentUser = optUser.get();
                    consoleIO.write("Successful login. Welcome " + currentUser.getFirstName() + "!");
                    authenticated = true;
                    return "SUCCESS";
                }
            } catch (Exception e) {
                logger.error("Unexpected error during login", e);
                consoleIO.writeError("An unexpected error occurred. Contact support.");
                return "EXIT";
            }
            currentUser = null;
            consoleIO.write("Successfully logged out. Returning to Authentication Module.");
        }
        return "EXIT";
    }

    private void handleStudentMenu(User student) {
        boolean exit = false;

        while (!exit) {
            studentMenuView.showMenu();
            String option = consoleIO.readLine("Select an option [0-3] (0 to log out): ").trim();

            try {
                switch (option) {
                    case "1":
                        handleStudentSearch(student);
                        break;

                    case "2":
                        handleStudentHistory(student);
                        break;

                    case "3":
                        handleCancelEnrollment(student);
                        break;

                    case "0":
                        exit = true;
                        break;

                    default:
                        consoleIO.writeError("Invalid option. Choose 0, 1, 2, or 3.");
                }

                if (!exit) {
                    consoleIO.readLine("\nPress ENTER to return to the menu...");
                }

            } catch (Exception e) {
                logger.error("Unexpected error in student menu", e);
                consoleIO.writeError("An unexpected error occurred. Contact support.");
                exit = true;
            }
        }
    }

    private void handleProfessorMenu(User professor) {
        boolean exit = false;

        while (!exit) {
            professorMenuView.showMenu();
            String option = consoleIO.readLine("Select an option [0-2] (0 to log out): ").trim();

            switch (option) {
                case "1":
                    professorFormService.createTutoringSession(professor);
                    break;

                case "2":
                    manageProfessorSessionManagement(professor);
                    break;

                case "0":
                    exit = true;
                    break;

                default:
                    consoleIO.writeError("Invalid option. Choose 0, 1, or 2.");
            }

            if (!exit && !option.equals("2"))
                consoleIO.readLine("\nPress ENTER to continue...");
        }
    }

    private void showPrincipalMenu(User user) {
        String roleName = user.getRole().getName().toUpperCase();

        try {
            if ("STUDENT".equals(roleName)) {
                handleStudentMenu(user);
            } else if ("PROFESSOR".equals(roleName)) {
                handleProfessorMenu(user);
            } else {
                consoleIO.writeError("Unrecognized role. Logging out...");
            }
        } catch (Exception e) {
            logger.error("Unexpected error in principal menu", e);
            consoleIO.writeError("An unexpected error occurred. Contact support.");
        }

        currentUser = null;
        consoleIO.write("Successfully logged out.");
    }

    private boolean showResultsAndEnrollIfRequested(User student, List<TutoringSession> results) {

        if (results == null || results.isEmpty()) {
            consoleIO.write("No tutoring sessions found.");
            return false;
        }

        consoleIO.write("--- Results ---");
        results.forEach(s -> consoleIO.write(
                s.getId() + " | " + s.getSubject() + " | " + s.getStartTime() + " | " + s.getModality()
        ));

        String input = consoleIO.readLine("\nEnter Tutoring Session ID to ENROLL (0 to go back): ").trim();

        if ("0".equals(input)) {
            return false;
        }

        try {
            Long sessionId = Long.parseLong(input);
            boolean enrolled = tutoringSessionService.enrollStudent(student, sessionId);

            return enrolled;

        } catch (NumberFormatException e) {
            consoleIO.writeError("Error: Session ID must be a number.");
            return false;
        }
    }


    private void handleStudentSearch(User student) {

        boolean exit = false;

        while (!exit) {
            studentMenuView.showSearchFilters();

            String opt = consoleIO.readLine("Select filter [0-3] (0 to go back): ").trim();

            switch (opt) {

                case "1":
                    String subject = consoleIO.readLine("Enter subject to search: ").trim();
                    var bySubject = tutoringSessionService.searchSessions(subject, null, null);

                    boolean goMain1 = showResultsAndEnrollIfRequested(student, bySubject);
                    if (goMain1) exit = true;

                    break;


                case "2":
                    String dateInput = consoleIO.readLine("Enter date (YYYY-MM-DD): ").trim();
                    try {
                        LocalDateTime date = LocalDateTime.parse(dateInput + "T00:00:00");
                        var byDate = tutoringSessionService.searchSessions(null, date, null);

                        boolean goMain2 = showResultsAndEnrollIfRequested(student, byDate);
                        if (goMain2) exit = true;

                    } catch (Exception e) {
                        consoleIO.writeError("Error: Date must use format YYYY-MM-DD.");
                    }
                    break;


                case "3":
                    String mod = consoleIO.readLine("Enter modality (ONLINE or PRESENCIAL): ").toUpperCase().trim();

                    if (!"ONLINE".equals(mod) && !"PRESENCIAL".equals(mod)) {
                        consoleIO.writeError("Error: Modality must be ONLINE or PRESENCIAL.");
                        break;
                    }

                    var byMod = tutoringSessionService.searchSessions(null, null, mod);

                    boolean goMain3 = showResultsAndEnrollIfRequested(student, byMod);
                    if (goMain3) exit = true;

                    break;


                case "0":
                    exit = true;
                    break;

                default:
                    consoleIO.writeError("Invalid option. Choose 0, 1, 2, or 3.");
            }

            if (!exit)
                consoleIO.readLine("\nPress ENTER to continue...");
        }
    }

    private void handleStudentHistory(User student) {
        consoleIO.write("\nRetrieving Academic History...");

        List<SessionHistory> history = studentProgressService.getTutoringHistory(student);
        studentMenuView.displayHistory(history);
    }

    private void handleCancelEnrollment(User student) {
        boolean exit = false;

        while (!exit) {
            String input = consoleIO
                    .readLine("Enter Tutoring Session ID to cancel (0 to go back): ").trim();

            if ("0".equals(input)) {
                exit = true;
                break;
            }

            try {
                Long sessionId = Long.parseLong(input);
                EnrollmentCancellationService.CancellationResult result = enrollmentCancellationService
                        .cancelEnrollment(student, sessionId);

                if (result.isSuccess()) {
                    consoleIO.write(result.getMessage());
                    exit = true;
                } else {
                    consoleIO.writeError(result.getMessage());
                }

            } catch (NumberFormatException e) {
                consoleIO.writeError("Error: Session ID must be a number.");
            }
        }
    }

    public void manageProfessorSessionManagement(User professor) {

        List<TutoringSession> sessions = tutoringSessionService.getProfessorActiveSessions(professor);
        professorMenuView.displayProfessorSessions(sessions, consoleIO);
        boolean exitManagement = false;

        while (!exitManagement) {
            String input = consoleIO.readLine("Enter Tutoring Session ID to CANCEL (0 to go back): ").trim();

            if ("0".equals(input)) {
                exitManagement = true;
                break;
            }

            try {
                Long sessionId = Long.parseLong(input);
                boolean success = tutoringSessionService.cancelSessionById(professor, sessionId);

                if (success) {
                    consoleIO.write("SUCCESS: Tutoring Session ID " + sessionId
                            + " has been CANCELLED. All associated student enrollments have been updated.");
                    sessions = tutoringSessionService.getProfessorActiveSessions(professor);
                    professorMenuView.displayProfessorSessions(sessions, consoleIO);

                } else {
                    consoleIO.writeError("FAILURE: Could not cancel Session ID " + sessionId
                            + ". Check if it exists or if you are the creator.");
                }

            } catch (NumberFormatException e) {
                consoleIO.writeError("Error: Session ID must be a number.");
            }
        }
    }
}