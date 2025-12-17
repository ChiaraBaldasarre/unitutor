package com.unitutor.grupo3_unitutor.view;

import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleIO {
    private final Scanner scanner;

    public ConsoleIO() {
        this.scanner = new Scanner(System.in);
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public String readLine(String prompt, String defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine();
        return input.isEmpty() ? defaultValue : input;
    }

    public void write(String message) {
        System.out.println(message);
    }

    public void writeError(String message) {
        System.err.println(message);
    }

    public void closeScanner() {
        scanner.close();
    }
}