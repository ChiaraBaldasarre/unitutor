package com.unitutor.grupo3_unitutor.utils;

public class DniValidator {

  public static String getValidationError(String dni) {

    if (dni == null || dni.trim().isEmpty()) {
      return "DNI is required.";
    }
    if (!dni.matches("[0-9]+")) {
      return "The DNI can only contain numbers.";
    }
    if (dni.length() < 7 || dni.length() > 8) {
      return "The DNI must have between 7 and 8 digits.";
    }
    return null;
  }
}