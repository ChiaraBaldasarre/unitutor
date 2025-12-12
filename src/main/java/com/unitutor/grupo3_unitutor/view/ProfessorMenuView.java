package com.unitutor.grupo3_unitutor.view;

import com.unitutor.grupo3_unitutor.model.TutoringSession;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ProfessorMenuView implements RoleMenuView {

    @Override
    public void showMenu() {
        System.out.println("\nMAIN DASHBOARD [PROFESSOR]");
        System.out.println("1. Create New Tutoring Session");
        System.out.println("2. Manage Active Tutoring Sessions");
        System.out.println("0. Log Out (Exit)");
    }

    public void displayProfessorSessions(List<TutoringSession> sessions, ConsoleIO consoleIO) {
        consoleIO.write("\n=== YOUR ACTIVE TUTORING SESSIONS ===");

        if (sessions.isEmpty()) {
            consoleIO.write("You do not have any active tutoring sessions.");
        } else {
            String headerFormat = "%-6s | %-20s | %-16s | %-10s | %-12s";
            String rowFormat = "%-6d | %-20s | %-16s | %-10d | %-12s";
            String separator = "------------------------------------------------------------------";

            consoleIO.write(String.format(headerFormat, "ID", "SUBJECT", "DATE/TIME", "CAPACITY", "MODALITY"));
            consoleIO.write(separator);

            for (TutoringSession session : sessions) {
                String row = String.format(rowFormat,
                        session.getId(),
                        session.getSubject(),
                        session.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        session.getMaxCapacity(),
                        session.getModality());
                consoleIO.write(row);
            }
            consoleIO.write(separator);
        }
    }
}