package com.unitutor.grupo3_unitutor.controller;

import com.unitutor.grupo3_unitutor.model.Usuario;
import com.unitutor.grupo3_unitutor.service.ProfessorMenuView;
import com.unitutor.grupo3_unitutor.service.StudentMenuView;
import com.unitutor.grupo3_unitutor.service.UsuarioService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Scanner;

@Component
public class ConsoleController {

    private final UsuarioService usuarioService;
    private final Scanner scanner;
    private Usuario usuarioActual;
    private final StudentMenuView studentMenuView;
    private final ProfessorMenuView professorMenuView;

    public ConsoleController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
        this.scanner = new Scanner(System.in);
        this.studentMenuView = new StudentMenuView();
        this.professorMenuView = new ProfessorMenuView();
    }

    public void run() {
        mostrarMenuLogin();

        if (usuarioActual != null) {
            mostrarMenuPrincipal(usuarioActual);
        }

        scanner.close();
    }

    private void mostrarMenuLogin() {
        boolean autenticado = false;
        System.out.println("\nAuthentication Module");

        System.out.println("(Type 'exit' to leave)\n");

        while (!autenticado) {
            System.out.print("Insert your DNI (ej. 11223344): ");
            String dni = scanner.nextLine().trim();

            if (dni.equalsIgnoreCase("exit")) {
                System.out.println("Exiting the system...");
                return;
            }

            Optional<Usuario> usuarioOpt = usuarioService.autenticarPorDni(dni);

            if (dni.matches("^\\d{8}$") && usuarioOpt.isEmpty()) {
                // CA-2.1: DNI no registrado
                System.err.println("ERROR: DNI not found. Please verify your credentials.");
            } else if (!dni.matches("^\\d{8}$")) {
                // CA-2.2: DNI Inválido/Formato
                System.err.println("ERROR: The DNI format is invalid. It must be numeric and contain 8 digits.");
            } else {
                this.usuarioActual = usuarioOpt.get();
                System.out.println("Successful login. Welcome " + usuarioActual.getFirstName() + "!");
                autenticado = true;
            }
        }
    }

    private void mostrarMenuPrincipal(Usuario usuario) {
        String rolName = usuario.getRole().getName().toUpperCase();
        boolean salir = false;
        while (!salir) {

            if ("STUDENT".equals(rolName)) {
                studentMenuView.showMenu();
            } else if ("PROFESSOR".equals(rolName)) {
                professorMenuView.showMenu();
            } else {
                System.err.println("Unrecognized role. Logging out...");
                usuarioActual = null;
                return;
            }

            System.out.print("Select an option: ");
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    if ("STUDENT".equals(rolName)) {
                        System.out.println("[STUDENT] Search and Book Tutoring Sessions (pending implementation).");
                    } else {
                        System.out.println("[PROFESSOR] Create New Tutoring Session (pending implementation).");
                    }
                    break;

                case "2":
                    if ("STUDENT".equals(rolName)) {
                        System.out.println("[STUDENT] Cancel Enrollment (pending implementation).");
                    } else {
                        System.out.println("[PROFESSOR] Upload Grades (pending implementation).");
                    }
                    break;

                case "3":
                    if ("STUDENT".equals(rolName)) {
                        System.out.println("[STUDENT] My Tutoring History (pending implementation).");
                    } else {
                        System.out.println("[PROFESSOR] Manage Active Tutoring Sessions (pending implementation).");
                    }
                    break;

                case "0":
                    System.out.println("Logging out...");
                    usuarioActual = null;
                    salir = true;
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }

            if (!salir) {
                System.out.println("\nPress ENTER to return to the menu...");
                scanner.nextLine();
            }
        }
    }
}