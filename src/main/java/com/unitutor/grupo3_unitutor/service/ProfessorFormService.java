package com.unitutor.grupo3_unitutor.service;

import com.unitutor.grupo3_unitutor.model.TutoringSession;
import com.unitutor.grupo3_unitutor.model.User;
import com.unitutor.grupo3_unitutor.view.ConsoleIO;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProfessorFormService {

    private static final Logger logger = LoggerFactory.getLogger(ProfessorFormService.class);
    private final TutoringSessionService sessionService;
    private final ConsoleIO consoleIO;

    public ProfessorFormService(TutoringSessionService sessionService, ConsoleIO consoleIO) {
        this.sessionService = sessionService;
        this.consoleIO = consoleIO;
    }

    // UNI-011: Implements the sequential form for Professor to create a new Tutoring Session.
    public void createTutoringSession(User professor) {
        TutoringSession session = new TutoringSession();
        session.setProfessor(professor);

        int step = 1;
        boolean exitForm = false;

        consoleIO.write("\n--- Create New Tutoring Session Form ---");
        consoleIO.write("Type 'back' to go to the previous step, or 'exit' to cancel the creation.");

        while (!exitForm) {
            String input;
            boolean valid = false;

            try {
                switch (step) {
                    case 1:
                        input = consoleIO.readLine("\nSTEP 1/4: Enter Subject (Next/Exit): ").trim();

                        if (input.equalsIgnoreCase("exit")) {
                            exitForm = true;
                            break;
                        }

                        if (input.isEmpty()) {
                            consoleIO.writeError("ERROR: Subject cannot be empty.");

                        } else {
                            session.setSubject(input);
                            step++;
                            valid = true;
                        }
                        break;

                    case 2:
                        input = consoleIO.readLine("\nSTEP 2/4: Enter Date and Time (format YYYY-MM-DD HH:mm | Back/Exit): ").trim();

                        if (input.equalsIgnoreCase("exit")) {
                            exitForm = true;
                            break;

                        } else if (input.equalsIgnoreCase("back")) {
                            step--;
                            valid = true;
                            break;
                        }

                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                            LocalDateTime startTime = LocalDateTime.parse(input, formatter);

                            if (startTime.isBefore(LocalDateTime.now().plusMinutes(5))) {
                                consoleIO.writeError("ERROR: The tutoring session must be scheduled for a future date and time.");

                            } else {
                                session.setStartTime(startTime);
                                step++;
                                valid = true;
                            }

                        } catch (DateTimeParseException e) {
                            consoleIO.writeError("ERROR: Invalid date and time format. Expected format: YYYY-MM-DD HH:mm.");
                        }
                        break;

                    case 3:
                        input = consoleIO.readLine("\nSTEP 3/4: Enter Duration in Minutes (e.g., 60 | Back/Exit): ").trim();

                        if (input.equalsIgnoreCase("exit")) {
                            exitForm = true;
                            break;

                        } else if (input.equalsIgnoreCase("back")) {
                            step--;
                            valid = true;
                            break;
                        }

                        try {
                            int duration = Integer.parseInt(input);
                            if (duration <= 0 || duration > 360) {
                                consoleIO.writeError("ERROR: Duration must be a positive number of minutes, typically up to 360 (6 hours).");

                            } else {
                                session.setDurationMinutes(duration);
                                step++;
                                valid = true;
                            }

                        } catch (NumberFormatException e) {
                            consoleIO.writeError("ERROR: Invalid format. Duration must be a whole number (in minutes).");
                        }
                        break;

                    case 4:
                        input = consoleIO.readLine("\nSTEP 4/4: Enter Max Student Capacity (e.g., 10 | Back/Exit): ").trim();

                        if (input.equalsIgnoreCase("exit")) {
                            exitForm = true;
                            break;

                        } else if (input.equalsIgnoreCase("back")) {
                            step--;
                            valid = true;
                            break;
                        }

                        try {
                            int capacity = Integer.parseInt(input);
                            if (capacity <= 0 || capacity > 50) {
                                consoleIO.writeError("ERROR: Capacity must be a positive number, typically between 1 and 50.");

                            } else {
                                session.setMaxCapacity(capacity);
                                step++;
                                valid = true;
                            }

                        } catch (NumberFormatException e) {
                            consoleIO.writeError("ERROR: Invalid format. Capacity must be a whole number.");
                        }
                        break;

                    case 5:

                        consoleIO.write("\n--- Session Summary ---");
                        consoleIO.write("Subject: " + session.getSubject());
                        consoleIO.write("Start Time: " + session.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                        consoleIO.write("Duration: " + session.getDurationMinutes() + " minutes");
                        consoleIO.write("Max Capacity: " + session.getMaxCapacity());
                        input = consoleIO.readLine("Confirm creation? (Y/N | Back/Exit): ").trim();

                        if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("n")) {
                            consoleIO.write("Tutoring session creation cancelled.");
                            exitForm = true;
                            break;

                        } else if (input.equalsIgnoreCase("back")) {
                            step--;
                            valid = true;
                            break;

                        } else if (input.equalsIgnoreCase("y")) {
                            Optional<TutoringSession> result = sessionService.createSession(professor, session);

                            if (result.isPresent()) {
                                consoleIO.write("\nSUCCESS: Tutoring Session for '" + session.getSubject() + "' created successfully!");
                                exitForm = true;

                            } else {
                                consoleIO.writeError("\nFAILURE: Could not create the tutoring session due to a system error. Please try again later.");
                                exitForm = true;
                            }
                            valid = true;

                        } else {
                            consoleIO.writeError("Invalid option. Please enter Y, N, Back, or Exit.");
                        }
                        break;

                    default:
                        consoleIO.writeError("System Error: Unknown form step. Exiting form.");
                        exitForm = true;
                        break;
                }

            } catch(Exception e){
                logger.error("An unexpected error occurred during form processing:", e);
                consoleIO.writeError("An unexpected error occurred during form processing: " + e.getMessage());
                exitForm = true;
            }
        }
        consoleIO.write("--- End of Tutoring Session Form ---");
    }
}