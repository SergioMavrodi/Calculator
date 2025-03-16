import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    private static List<String> history = new ArrayList<>();

    public static void main(String[] args) {
        Scanner consoleScanner = new Scanner(System.in);
        System.out.println("Choose input mode: 'file' for input.txt or 'manual' for console input: ");
        String mode = consoleScanner.nextLine().trim().toLowerCase();

        if (mode.equals("file")) {
            runFileMode(consoleScanner);
        } else if (mode.equals("manual")) {
            runManualMode(consoleScanner);
        } else {
            System.out.println("Invalid mode, you dumb fuck. Defaulting to manual.");
            runManualMode(consoleScanner);
        }
        consoleScanner.close();
    }

    private static void runFileMode(Scanner consoleScanner) {
        try {
            Scanner fileScanner = new Scanner(new File("input.txt"));
            FileWriter writer = new FileWriter("output.txt");
            while (fileScanner.hasNextLine()) {
                String input = fileScanner.nextLine().trim();
                if (input.equalsIgnoreCase("exit")) break;
                if (input.equalsIgnoreCase("history")) {
                    for (String record : history) {
                        System.out.println(record);
                        writer.write(record + "\n");
                    }
                    continue;
                }
                processInput(input, writer);
                writer.flush();
                if (fileScanner.hasNextLine()) {
                    System.out.println("Continue? (yes/no)");
                    if (!consoleScanner.nextLine().trim().equalsIgnoreCase("yes")) break;
                }
            }
            fileScanner.close();
            writer.close();
        } catch (IOException e) {
            System.out.println("Error: File operation failed - " + e.getMessage());
        }
    }

    private static void runManualMode(Scanner scanner) {
        try {
            FileWriter writer = new FileWriter("output.txt");
            while (true) {
                System.out.println("Enter operation (e.g., 5 + 3, sqrt 16, roundTo 5.555 2, history, exit): ");
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("exit")) break;
                if (input.equalsIgnoreCase("history")) {
                    for (String record : history) {
                        System.out.println(record);
                        writer.write(record + "\n");
                    }
                    continue;
                }
                processInput(input, writer);
                writer.flush();
                System.out.println("Continue? (yes/no)");
                if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) break;
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error: File operation failed - " + e.getMessage());
        }
    }

    private static void processInput(String input, FileWriter writer) throws IOException {
        try {
            String[] parts = input.split("\\s+");
            if (parts.length == 2) {
                double num = Double.parseDouble(parts[1]);
                double result = 0;
                switch (parts[0].toLowerCase()) {
                    case "sqrt": result = Math.sqrt(num); break;
                    case "abs": result = Math.abs(num); break;
                    case "round": result = Math.round(num); break;
                    default: throw new IllegalArgumentException("Invalid function");
                }
                System.out.println("Result: " + result);
                writer.write(input + " = " + result + "\n");
                history.add(input + " = " + result);
            } else if (parts.length == 3) {
                if (parts[0].toLowerCase().equals("roundto")) {
                    double num = Double.parseDouble(parts[1]);
                    int decimalPlaces = Integer.parseInt(parts[2]);
                    double result = Math.round(num * Math.pow(10, decimalPlaces)) / Math.pow(10, decimalPlaces);
                    System.out.println("Result: " + result);
                    writer.write(input + " = " + result + "\n");
                    history.add(input + " = " + result);
                } else {
                    double num1 = Double.parseDouble(parts[0]);
                    String operator = parts[1];
                    double num2 = Double.parseDouble(parts[2]);
                    double result = 0;
                    switch (operator) {
                        case "+": result = num1 + num2; break;
                        case "-": result = num1 - num2; break;
                        case "*": result = num1 * num2; break;
                        case "/":
                            if (num2 == 0) throw new ArithmeticException("Division by zero");
                            result = num1 / num2;
                            break;
                        case "%":
                            if (num2 == 0) throw new ArithmeticException("Modulus by zero");
                            result = num1 % num2;
                            break;
                        case "^": result = Math.pow(num1, num2); break;
                        default: throw new IllegalArgumentException("Invalid operator");
                    }
                    System.out.println("Result: " + result);
                    writer.write(input + " = " + result + "\n");
                    history.add(input + " = " + result);
                }
            } else {
                throw new IllegalArgumentException("Invalid input format");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number format");
            writer.write("Error: Invalid number format\n");
        } catch (ArithmeticException e) {
            System.out.println("Error: " + e.getMessage());
            writer.write("Error: " + e.getMessage() + "\n");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            writer.write("Error: " + e.getMessage() + "\n");
        }
    }
}