package com.unitutor.grupo3_unitutor.service;

import org.springframework.stereotype.Component;

@Component
public class ProfessorMenuView implements RoleMenuView {

    @Override
    public void showMenu() {
        System.out.println("\nDASHBOARD PRINCIPAL [PROFESSOR]");
        System.out.println("1. Crear Nueva Tutoría");
        System.out.println("2. Gestionar Tutorías Activas");
        System.out.println("3. Cargar Calificaciones");
        System.out.println("0. Salir (Cerrar Sesión)");
    }
}