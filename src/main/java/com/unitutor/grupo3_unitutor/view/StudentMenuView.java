package com.unitutor.grupo3_unitutor.view;

import org.springframework.stereotype.Component;

@Component
public class StudentMenuView implements RoleMenuView {

    @Override
    public void showMenu() {
        System.out.println("\nMAIN DASHBOARD [STUDENT]");
        System.out.println("1. Search and Book Tutoring Sessions");
        System.out.println("2. My Tutoring History");
        System.out.println("3. Cancel Enrollment");
        System.out.println("0. Exit (Log Out)");
    }

    public void showSearchFilters() {
        System.out.println("\n--- Search Tutoring Sessions ---");
        System.out.println("1. Search by Subject");
        System.out.println("2. Search by Date");
        System.out.println("3. Search by Modality");
        System.out.println("0. Back");
    }
}