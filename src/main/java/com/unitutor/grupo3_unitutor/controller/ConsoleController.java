package com.unitutor.grupo3_unitutor.controller;

import com.unitutor.grupo3_unitutor.model.SessionHistory;
import com.unitutor.grupo3_unitutor.model.User;
import com.unitutor.grupo3_unitutor.service.ProfessorFormService;
import com.unitutor.grupo3_unitutor.service.StudentProgressService;
import com.unitutor.grupo3_unitutor.service.TutoringSessionService;
import com.unitutor.grupo3_unitutor.view.ConsoleIO;
import com.unitutor.grupo3_unitutor.view.ProfessorMenuView;
import com.unitutor.grupo3_unitutor.view.StudentMenuView;
import com.unitutor.grupo3_unitutor.service.UserService;
import com.unitutor.grupo3_unitutor.utils.DniValidator;

import org.springframework.stereotype.Component;
import java.util.List;

import java.time.LocalDateTime;
import java.util.Optional;


@Component
public class ConsoleController {
    private final UserService userService;
    private final ProfessorFormService professorFormService;
    private final ConsoleIO consoleIO;
    private User currentUser;
    private final StudentMenuView studentMenuView;
    private final ProfessorMenuView professorMenuView;
    private final StudentProgressService studentProgressService;

    public ConsoleController(UserService userService, StudentMenuView studentMenuView,
            ProfessorMenuView professorMenuView, ProfessorFormService professorFormService, ConsoleIO consoleIO, StudentProgressService studentProgressService) {
        this.userService = userService;
        this.professorFormService = professorFormService;
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
        consoleIO.write("(Type 'exit' to leave)\n");

        while (!authenticated) {
            String dni = consoleIO.readLine("Insert your DNI (ej. 11223344): ").trim();

            if (dni.equalsIgnoreCase("exit")) {
                consoleIO.write("Exiting the system...");
                return;
            }

            String errorMsg = DniValidator.getValidationError(dni); // It returns null

            if (errorMsg != null) {
                consoleIO.writeError("ERROR: " + errorMsg);
                continue;
            }

            // PASO 2: Intentar Autenticar (Solo si el formato es válido)
            Optional<User> optUser = userService.authenticateByDni(dni);

            if (optUser.isEmpty()) {
                // CA-2.1: DNI válido en formato, pero no existe en la BD
                consoleIO.writeError("ERROR: DNI not found. Please verify your credentials.");
            } else {
                // Login Exitoso
                this.currentUser = optUser.get();
                consoleIO.write("Successful login. Welcome " + currentUser.getFirstName() + "!");
                authenticated = true;
            }
        }
    }

    private void showPrincipalMenu(User user) {
        String roleName = user.getRole().getName().toUpperCase();
        boolean exit = false;
        while (!exit) {

            if ("STUDENT".equals(roleName)) {
                studentMenuView.showMenu();
            } else if ("PROFESSOR".equals(roleName)) {
                professorMenuView.showMenu();
            } else {
                System.err.println("Unrecognized role. Logging out...");
                currentUser = null;
                return;
            }

            String option = consoleIO.readLine("Select an option: ").trim();

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
                        consoleIO.write("[STUDENT] Cancel Enrollment (pending implementation).");
                    } else {
                        consoleIO.write("[PROFESSOR] Upload Grades (pending implementation).");
                    }
                    break;

                case "0":
                    consoleIO.write("\n" + "Signing out...");
                    currentUser = null;
                    exit = true;
                    break;

                default:
                    consoleIO.write("Invalid option. Please try again.");
            }

            if (!exit) {
                consoleIO.readLine("\nPress ENTER to return to the menu...");
            }
        }
    }

    private void handleStudentSearch(User student) {

        boolean exit = false;

        while (!exit) {
            studentMenuView.showSearchFilters();

            String opt = consoleIO.readLine("Choose filter: ").trim();

            switch (opt) {

                case "1":
                    String subject = consoleIO.readLine("Enter subject: ");
                    var bySubject = TutoringSessionService.searchSessions(subject, null, null);

                    if (bySubject.isEmpty()) {
                        consoleIO.write("Subject not found.");
                    } else {
                        consoleIO.write("--- Results ---");
                        bySubject.forEach(s ->
                                consoleIO.write(s.getId() + " | " + s.getSubject() + " | " + s.getStartTime() + " | " + s.getModality())
                        );
                    }
                    break;

                case "2":
                    String dateInput = consoleIO.readLine("Enter date (YYYY-MM-DD): ");
                    try {
                        LocalDateTime date = LocalDateTime.parse(dateInput + "T00:00:00");
                        var byDate = TutoringSessionService.searchSessions(null, date, null);

                        if (byDate.isEmpty()) {
                            consoleIO.write("No tutoring sessions found.");
                        } else {
                            consoleIO.write("--- Results ---");
                            byDate.forEach(s ->
                                    consoleIO.write(s.getId() + " | " + s.getSubject() + " | " + s.getStartTime() + " | " + s.getModality())
                            );
                        }

                    } catch (Exception e) {
                        consoleIO.writeError("Invalid date format.");
                    }
                    break;

                case "3":
                    String mod = consoleIO.readLine("Enter modality (ONLINE / PRESENCIAL): ").toUpperCase();
                    var byMod = TutoringSessionService.searchSessions(null, null, mod);

                    if (byMod.isEmpty()) {
                        consoleIO.write("No tutoring sessions found.");
                    } else {
                        consoleIO.write("--- Results ---");
                        byMod.forEach(s ->
                                consoleIO.write(s.getId() + " | " + s.getSubject() + " | " + s.getStartTime() + " | " + s.getModality())
                        );
                    }
                    break;

                case "0":
                    exit = true;
                    break;

                default:
                    consoleIO.write("Invalid option.");
            }

            if (!exit) consoleIO.readLine("\nPress ENTER to continue...");
        }
    }
    private void handleStudentHistory(User student) {
        consoleIO.write("\nRetrieving Academic History...");

        List<SessionHistory> history = studentProgressService.getTutoringHistory(student);
        studentMenuView.displayHistory(history);
    }
}
