package com.unitutor.grupo3_unitutor.controller;

import com.unitutor.grupo3_unitutor.model.SessionHistory;
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

    public ConsoleController(UserService userService, StudentMenuView studentMenuView,
            ProfessorMenuView professorMenuView, ProfessorFormService professorFormService, EnrollmentCancellationService enrollmentCancellationService, ConsoleIO consoleIO,
            StudentProgressService studentProgressService) {
        this.userService = userService;
        this.professorFormService = professorFormService;
        this.enrollmentCancellationService = enrollmentCancellationService;
        this.consoleIO = consoleIO;
        this.studentMenuView = studentMenuView;
        this.professorMenuView = professorMenuView;
        this.studentProgressService = studentProgressService;
    }

    public void run() {
        showLoginMenu();

        if (currentUser != null) {
            showPrincipalMenu(currentUser);
        }
        consoleIO.closeScanner();
    }

    private void showLoginMenu() {
        boolean authenticated = false;
        consoleIO.write("\nAuthentication Module");
        consoleIO.write("Type 'exit' at any time to leave.\n");

        while (!authenticated) {
            try {
                String dni = consoleIO.readLine("Enter DNI (8 digits) or type 'exit' to quit: ").trim();

                if (dni.equalsIgnoreCase("exit")) {
                    consoleIO.write("Exiting the system...");
                    return;
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
                }
            } catch (Exception e) {
                logger.error("Unexpected error during login", e);
                consoleIO.writeError("An unexpected error occurred. Contact support.");
                return;
            }
        }
    }

    private void showPrincipalMenu(User user) {
        String roleName = user.getRole().getName().toUpperCase();
        boolean exit = false;
        while (!exit) {
            try {

                if ("STUDENT".equals(roleName)) {
                    studentMenuView.showMenu();
                } else if ("PROFESSOR".equals(roleName)) {
                    professorMenuView.showMenu();
                } else {
                    consoleIO.writeError("Unrecognized role. Logging out...");
                    currentUser = null;
                    return;
                }

                String option = consoleIO.readLine("Select an option [0-3] (0 to log out): ").trim();

                switch (option) {
                    case "1":
                        if ("STUDENT".equals(roleName)) {
                            consoleIO.write("[STUDENT] Search and Book Tutoring Sessions (pending implementation).");
                            handleStudentSearch(user);
                        } else if ("PROFESSOR".equals(roleName)) {
                            professorFormService.createTutoringSession(user);
                        }
                        break;

                    case "2":
                        if ("STUDENT".equals(roleName)) {
                            handleStudentHistory(user);
                        } else {
                            consoleIO.write("[PROFESSOR] Manage Active Tutoring Sessions (pending implementation).");
                        }
                        break;

                    case "3":
                        if ("STUDENT".equals(roleName)) {
                            handleCancelEnrollment(user);
                        } else {
                            consoleIO.write("[PROFESSOR] Upload Grades (pending implementation).");
                        }
                        break;

                    case "0":
                        consoleIO.write("\nSigning out...");
                        currentUser = null;
                        exit = true;
                        break;

                    default:
                        consoleIO.writeError("Invalid option. Choose 0, 1, 2, or 3.");
                }

                if (!exit) {
                    consoleIO.readLine("\nPress ENTER to return to the menu...");
                }
            } catch (Exception e) {
                logger.error("Unexpected error in principal menu", e);
                consoleIO.writeError("An unexpected error occurred. Contact support.");
                currentUser = null;
                return;
            }
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
                    var bySubject = TutoringSessionService.searchSessions(subject, null, null);

                    if (bySubject.isEmpty()) {
                        consoleIO.write("No tutoring sessions found for that subject.");
                    } else {
                        consoleIO.write("--- Results ---");
                        bySubject.forEach(s -> consoleIO.write(s.getId() + " | " + s.getSubject() + " | "
                                + s.getStartTime() + " | " + s.getModality()));
                    }
                    break;

                case "2":
                    String dateInput = consoleIO.readLine("Enter date (YYYY-MM-DD): ").trim();
                    try {
                        LocalDateTime date = LocalDateTime.parse(dateInput + "T00:00:00");
                        var byDate = TutoringSessionService.searchSessions(null, date, null);

                        if (byDate.isEmpty()) {
                            consoleIO.write("No tutoring sessions found.");
                        } else {
                            consoleIO.write("--- Results ---");
                            byDate.forEach(s -> consoleIO.write(s.getId() + " | " + s.getSubject() + " | "
                                    + s.getStartTime() + " | " + s.getModality()));
                        }

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

                    var byMod = TutoringSessionService.searchSessions(null, null, mod);

                    if (byMod.isEmpty()) {
                        consoleIO.write("No tutoring sessions found.");
                    } else {
                        consoleIO.write("--- Results ---");
                        byMod.forEach(s -> consoleIO.write(s.getId() + " | " + s.getSubject() + " | " + s.getStartTime()
                                + " | " + s.getModality()));
                    }
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
}
