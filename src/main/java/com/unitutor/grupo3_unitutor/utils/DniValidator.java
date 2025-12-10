package com.unitutor.grupo3_unitutor.utils;

public class DniValidator {

  public static String getValidationError(String dni) {

    if (dni == null || dni.trim().isEmpty()) {
      return "DNI is required.";
    }
    if (!dni.matches("[0-9]+")) {
      return "DNI must be numeric and have exactly 8 digits.";
    }
    if (dni.length() != 8) {
      return "DNI must contain exactly 8 digits.";
    }
    return null;
  }
}