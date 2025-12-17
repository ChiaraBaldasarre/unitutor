package com.unitutor.grupo3_unitutor.controller;

import com.unitutor.grupo3_unitutor.model.User;
import com.unitutor.grupo3_unitutor.service.EnrollmentCancellationService;
import com.unitutor.grupo3_unitutor.view.ConsoleIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class EnrollmentCancellationController {

  private static final Logger logger = LoggerFactory.getLogger(EnrollmentCancellationController.class);
  private final EnrollmentCancellationService enrollmentCancellationService;
  private final ConsoleIO consoleIO;

  public EnrollmentCancellationController(EnrollmentCancellationService enrollmentCancellationService,
      ConsoleIO consoleIO) {
    this.enrollmentCancellationService = enrollmentCancellationService;
    this.consoleIO = consoleIO;
  }

  public void cancelEnrollment(User student) {
    boolean exit = false;

    while (!exit) {
      try {
        String input = consoleIO
            .readLine("Enter Tutoring Session ID to cancel (0 to go back): ").trim();

        if ("0".equals(input)) {
          exit = true;
          break;
        }

        Long sessionId = Long.parseLong(input);
        EnrollmentCancellationService.CancellationResult result = enrollmentCancellationService
            .cancelEnrollment(student, sessionId);

        if (result.isSuccess()) {
          consoleIO.write(result.getMessage());
          exit = true;
        } else {
          consoleIO.writeError(result.getMessage());
        }

      } catch (NumberFormatException e) {
        consoleIO.writeError("Error: Session ID must be a number.");
      } catch (Exception e) {
        logger.error("Unexpected error during enrollment cancellation", e);
        consoleIO.writeError("An unexpected error occurred. Contact support.");
        exit = true;
      }
    }
  }
}
