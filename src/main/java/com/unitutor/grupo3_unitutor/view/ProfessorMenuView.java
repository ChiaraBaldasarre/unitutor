package com.unitutor.grupo3_unitutor.view;

import org.springframework.stereotype.Component;

@Component
public class ProfessorMenuView implements RoleMenuView {

    @Override
    public void showMenu() {
        System.out.println("\nMAIN DASHBOARD [PROFESSOR]");
        System.out.println("1. Create New Tutoring Session");
       // System.out.println("2. Manage Active Tutoring Sessions");
       // System.out.println("3. Upload Grades");
        System.out.println("0. Log Out (Exit)");
    }
}