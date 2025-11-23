package com.unitutor.grupo3_unitutor.controller;

import com.unitutor.grupo3_unitutor.model.User;
import com.unitutor.grupo3_unitutor.service.ProfessorMenuView;
import com.unitutor.grupo3_unitutor.service.StudentMenuView;
import com.unitutor.grupo3_unitutor.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Scanner;

@Component
public class ConsoleController {

    private final UserService userService;
    private final Scanner scanner;
    private User currentUser;
    private final StudentMenuView studentMenuView;
    private final ProfessorMenuView professorMenuView;

    public ConsoleController(UserService userService) {
        this.userService = userService;
        this.scanner = new Scanner(System.in);
        this.studentMenuView = new StudentMenuView();
        this.professorMenuView = new ProfessorMenuView();
    }

    public void run() {
        showLoginMenu();

        if (currentUser != null) {
            showPrincipalMenu(currentUser);
        }

        scanner.close();
    }

    private void showLoginMenu() {
        boolean authenticated = false;
        System.out.println("\nAuthentication Module");

        System.out.println("(Type 'exit' to leave)\n");

        while (!authenticated) {
            System.out.print("Insert your DNI (ej. 11223344): ");
            String dni = scanner.nextLine().trim();

            if (dni.equalsIgnoreCase("exit")) {
                System.out.println("Exiting the system...");
                return;
            }

            Optional<User> optUser = userService.authenticateByDni(dni);

            if (dni.matches("^\\d{8}$") && optUser.isEmpty()) {
                // CA-2.1: DNI no registrado
                System.err.println("ERROR: DNI not found. Please verify your credentials.");
            } else if (!dni.matches("^\\d{8}$")) {
                // CA-2.2: DNI Inválido/Formato
                System.err.println("ERROR: The DNI format is invalid. It must be numeric and contain 8 digits.");
            } else {
                this.currentUser = optUser.get();
                System.out.println("Successful login. Welcome " + currentUser.getFirstName() + "!");
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

            System.out.print("Select an option: ");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    if ("STUDENT".equals(roleName)) {
                        System.out.println("[STUDENT] Search and Book Tutoring Sessions (pending implementation).");
                    } else {
                        System.out.println("[PROFESSOR] Create New Tutoring Session (pending implementation).");
                    }
                    break;

                case "2":
                    if ("STUDENT".equals(roleName)) {
                        System.out.println("[STUDENT] Cancel Enrollment (pending implementation).");
                    } else {
                        System.out.println("[PROFESSOR] Upload Grades (pending implementation).");
                    }
                    break;

                case "3":
                    if ("STUDENT".equals(roleName)) {
                        System.out.println("[STUDENT] My Tutoring History (pending implementation).");
                    } else {
                        System.out.println("[PROFESSOR] Manage Active Tutoring Sessions (pending implementation).");
                    }
                    break;

                case "0":
                    System.out.println("Logging out...");
                    currentUser = null;
                    exit = true;
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }

            if (!exit) {
                System.out.println("\nPress ENTER to return to the menu...");
                scanner.nextLine();
            }
        }
    }
}