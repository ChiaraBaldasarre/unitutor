package com.unitutor.grupo3_unitutor.controller;

import com.unitutor.grupo3_unitutor.model.User;
import com.unitutor.grupo3_unitutor.service.UserService;
import com.unitutor.grupo3_unitutor.view.ConsoleIO;
import com.unitutor.grupo3_unitutor.utils.DniValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class AuthenticationController {

  private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
  private final UserService userService;
  private final ConsoleIO consoleIO;

  public AuthenticationController(UserService userService, ConsoleIO consoleIO) {
    this.userService = userService;
    this.consoleIO = consoleIO;
  }

  public Optional<User> login() {
    boolean authenticated = false;
    consoleIO.write("\nAuthentication Module");
    consoleIO.write("Type 'exit' at any time to leave.\n");

    while (!authenticated) {
      try {
        String dni = consoleIO.readLine("Enter DNI (8 digits) or type 'exit' to quit: ").trim();

        if (dni.equalsIgnoreCase("exit")) {
          consoleIO.write("Exiting the system...");
          return Optional.empty();
        }

        String errorMsg = DniValidator.getValidationError(dni);

        if (errorMsg != null) {
          consoleIO.writeError("Error: " + errorMsg + " Expected format: 8 numeric digits.");
          continue;
        }

        Optional<User> optUser = userService.authenticateByDni(dni);

        if (optUser.isEmpty()) {
          consoleIO.writeError("Error: DNI not found. Please verify and try again.");
        } else {
          User user = optUser.get();
          consoleIO.write("Successful login. Welcome " + user.getFirstName() + "!");
          return Optional.of(user);
        }
      } catch (Exception e) {
        logger.error("Unexpected error during login", e);
        consoleIO.writeError("An unexpected error occurred. Contact support.");
        return Optional.empty();
      }
    }
    return Optional.empty();
  }
}
