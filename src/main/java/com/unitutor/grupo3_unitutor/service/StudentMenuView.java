package com.unitutor.grupo3_unitutor.service;

import org.springframework.stereotype.Component;

@Component
public class StudentMenuView implements RoleMenuView {

    @Override
    public void showMenu() {
        System.out.println("\nDASHBOARD PRINCIPAL [STUDENT]");
        System.out.println("1. Buscar y Reservar Tutorías");
        System.out.println("2. Mi Historial de Tutorías");
        System.out.println("3. Cancelar Inscripción");
        System.out.println("0. Salir (Cerrar Sesión)");
    }
}