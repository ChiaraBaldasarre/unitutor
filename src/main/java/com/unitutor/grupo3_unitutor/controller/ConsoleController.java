package com.unitutor.grupo3_unitutor.controller;

import com.unitutor.grupo3_unitutor.model.User;
import com.unitutor.grupo3_unitutor.service.ProfessorFormService;
import com.unitutor.grupo3_unitutor.view.ConsoleIO;
import com.unitutor.grupo3_unitutor.view.ProfessorMenuView;
import com.unitutor.grupo3_unitutor.view.StudentMenuView;
import com.unitutor.grupo3_unitutor.service.UserService;
import com.unitutor.grupo3_unitutor.utils.DniValidator;

import org.springframework.stereotype.Component;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ConsoleController {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleController.class);
    private final UserService userService;
    private final ProfessorFormService professorFormService;
    private final ConsoleIO consoleIO;
    private User currentUser;
    private final StudentMenuView studentMenuView;
    private final ProfessorMenuView professorMenuView;

    public ConsoleController(UserService userService, StudentMenuView studentMenuView,
            ProfessorMenuView professorMenuView, ProfessorFormService professorFormService, ConsoleIO consoleIO) {
        this.userService = userService;
        this.professorFormService = professorFormService;
        this.consoleIO = consoleIO;
        this.studentMenuView = new StudentMenuView();
        this.professorMenuView = new ProfessorMenuView();
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
                    } else if ("PROFESSOR".equals(roleName)) {
                        professorFormService.createTutoringSession(user);
                    }
                    break;

                case "2":
                    if ("STUDENT".equals(roleName)) {
                        consoleIO.write("[STUDENT] My Tutoring History (pending implementation).");
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
}
