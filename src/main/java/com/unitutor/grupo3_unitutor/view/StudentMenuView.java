package com.unitutor.grupo3_unitutor.view;

import com.unitutor.grupo3_unitutor.model.SessionHistory;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class StudentMenuView implements RoleMenuView {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final ConsoleIO consoleIO;

    public StudentMenuView(ConsoleIO consoleIO) {
        this.consoleIO = consoleIO;
        }

        @Override
        public void showMenu () {
            System.out.println("\nMAIN DASHBOARD [STUDENT]");
            System.out.println("1. Search and Book Tutoring Sessions");
            System.out.println("2. My Tutoring History");
            System.out.println("3. Cancel Enrollment");
            System.out.println("0. Exit (Log Out)");
        }

        public void showSearchFilters () {
            System.out.println("\n--- Search Tutoring Sessions ---");
            System.out.println("1. Search by Subject");
            System.out.println("2. Search by Date");
            System.out.println("3. Search by Modality");
            System.out.println("0. Back");
        }


        public void displayHistory (List < SessionHistory > history) {

            consoleIO.write("\n=== TUTORING HISTORY ===");

            if (history.isEmpty()) {
                consoleIO.write("You do not have any tutoring sessions yet.");

            } else {
                String headerFormat = "%-6s | %-20s | %-25s | %-16s | %-12s";
                String rowFormat = "%-6d | %-20s | %-25s | %-16s | %-12s";
                String separator = "-------------------------------------------------------------------------------------";

                consoleIO.write(String.format(headerFormat, "ID", "SUBJECT", "TUTOR", "DATE/TIME", "STATUS"));
                consoleIO.write(separator);

                for (SessionHistory dto : history) {

                    String statusDisplay = "CANCELLED".equalsIgnoreCase(dto.getStatus()) ? "CANCELLED" : dto.getStatus();

                    String row = String.format(rowFormat,
                            dto.getSessionId(),
                            truncate(dto.getSubject(), 20),
                            truncate(dto.getProfessorName(), 25),
                            dto.getDateTime().format(DATE_TIME_FORMATTER),
                            statusDisplay
                    );
                    consoleIO.write(row);
                }
                consoleIO.write(separator);
            }
        }

        private String truncate (String input, int width){
            //Asegura que la tabla no se desalinee en la consola.
            if (input == null) return "";
            if (input.length() > width) {
                return input.substring(0, width - 3) + "...";
            }
            return input;
        }
}