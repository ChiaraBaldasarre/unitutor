package com.unitutor.grupo3_unitutor.controller;

import com.unitutor.grupo3_unitutor.model.Usuario;
import com.unitutor.grupo3_unitutor.service.UsuarioService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Scanner;

@Component
public class ConsoleController {

    private final UsuarioService usuarioService;
    private final Scanner scanner;
    private Usuario usuarioActual;

    public ConsoleController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
        this.scanner = new Scanner(System.in);
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
        System.out.println("\nMódulo de Autenticación");

        while (!autenticado) {
            System.out.print("Ingrese su DNI (ej. 11223344): ");
            String dni = scanner.nextLine().trim();

            if (dni.equalsIgnoreCase("exit")) {
                System.out.println("Saliendo del sistema...");
                return;
            }

            Optional<Usuario> usuarioOpt = usuarioService.autenticarPorDni(dni);

            if (dni.matches("^\\d{8}$") && usuarioOpt.isEmpty()) {
                // CA-2.1: DNI no registrado
                System.err.println("ERROR: DNI no encontrado. Verifique sus credenciales.");
            } else if (!dni.matches("^\\d{8}$")) {
                // CA-2.2: DNI Inválido/Formato
                System.err.println("ERROR: El formato del DNI es inválido. Debe ser numérico y tener 8 dígitos.");
            } else {
                this.usuarioActual = usuarioOpt.get();
                System.out.println("Ingreso exitoso. ¡Bienvenido(a) " + usuarioActual.getFirstName() + "!");
                autenticado = true;
            }
        }
    }

    // Función ejemplo para mostrar opciones del menú.
    private void mostrarMenuPrincipal(Usuario usuario) {
        boolean active = true;

        while (active) {
            String rolName = usuario.getRole().getName().toUpperCase();
            System.out.println("\nDASHBOARD PRINCIPAL [" + rolName + "]");

            if ("STUDENT".equals(rolName)) {
                System.out.println("1. Buscar y Reservar Tutorías");
                System.out.println("2. Mi Historial de Tutorías");
                System.out.println("3. Cancelar Inscripción");

            } else if ("PROFESSOR".equals(rolName)) {
                System.out.println("1. Crear Nueva Tutoría");
                System.out.println("2. Gestionar Tutorías Activas");
                System.out.println("3. Cargar Calificaciones");
            }

            System.out.println("0. Salir (Cerrar Sesión)");
            System.out.print("Seleccione una opción: ");
            // Implementar bucle while para manejar las selecciones del menú.

            String option = scanner.nextLine().trim();

            switch (option) {
                case "0":
                    System.out.println("Cerrando sesión...");
                    usuarioActual = null;   // ← destruye la sesión
                    active = false;         // ← sale del menú
                    break;

                default:
                    System.err.println("Opción inválida.");
            }
        }


    System.out.println("Sesión finalizada. Saliendo del sistema...");
    }
}