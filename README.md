# Calculator

This is a console-based calculator implemented in Java. It performs basic arithmetic operations and includes additional mathematical functions. The program supports both manual input and file-based input, logs results to a file, maintains a calculation history, and handles errors gracefully.

## Features

- **Basic Operations**: Addition (+), Subtraction (-), Multiplication (*), Division (/), Modulus (%).
- **Additional Functions**: 
  - `sqrt(number)` - Calculates the square root of a number.
  - `abs(number)` - Returns the absolute value of a number.
  - `round(number)` - Rounds a number to the nearest integer.
  - `roundTo(number decimalPlaces)` - Rounds a number to a specified number of decimal places.
  - `power(base exponent)` - Raises a base number to an exponent (using `^`).
- **Input Modes**: 
  - Manual - Enter operations directly in the console.
  - File - Reads operations from `input.txt`.
- **Output**: Saves results to `output.txt`.
- **History**: Stores past calculations for review.
- **Error Handling**: Manages invalid inputs, division by zero, and other exceptions.

## Input Format
- **Basic Operations:** `5 + 3`, `10 / 2`, etc.
- **Functions:** `sqrt 16`, `abs -5`, `round 3.7`, `roundTo 5.555 2`.
- **Special Commands:** `history` to wiew past calculations, `exit` to quit.

## Sample `input.txt`
```text
5 + 3
sqrt 16
roundTo 3.14159 2
history
exit
```

## Output
**Results are written to `output.txt`. Example:**
```text
5 + 3 = 8
sqrt 16 = 4.0
roundTo 3.14159 2 = 3.14
```

## Error Handling
- **Division by zero: "Error: Division by zero".**
- **Invalid number: "Error: Invalid number format".**
- **Wrong format: "Error: Invalid input format".**

## Code Explanation
**Imports**
```java
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
```
- `Arraylist` and `List`: Used to create a dynamic list for storing calculation history.
- `Scanner`: Reads input from the console or a file
- `File` and `FileWriter`: Handle file operations for reading `input.txt` and writing to `output.txt`.
- `IOException`: Catches errors related to file operations.

**Class and Variables**
```java
public class Main {
    private static List<String> history = new ArrayList<>();
```
- `Main`: The main class containing all the logic
- `history`: A static list of strings to store past calculations (e.g., "5 + 3 = 8"), accessible across all methods.

**Main Method (Entry Point)**
```java
public static void main(String[] args) {
    Scanner consoleScanner = new Scanner(System.in);
    System.out.println("Choose input mode: 'file' for input.txt or 'manual' for console input: ");
    String mode = consoleScanner.nextLine().trim().toLowerCase();

    if (mode.equals("file")) {
        runFileMode(consoleScanner);
    } else if (mode.equals("manual")) {
        runManualMode(consoleScanner);
    } else {
        System.out.println("Invalid mode. Defaulting to manual.");
        runManualMode(consoleScanner);
    }
    consoleScanner.close();
}
```
- Creates a `Scanner`object to read from the console.
- Prompts the user to choose between `file` or `manual` input mode.
- Based on the input, calls either `runFileMode` or `runManualMode`.
- If the mode is invalide, defaults to manual mode with warning.
- Closes the scanner to prevent resourse leaks.

**File Mode Method**
```java
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
```
- Opens `input.txt` for reading and `output.txt` for writing.
- Loops through each line in the file using `while (fileScanner.hasNextLine())`.
- If the line is `exit`, stops the loop.
- If the line is `history`, prints and writes the entire history to both console and file.
- For other inputs, passes the line to `processInput` for calculation.
- `writer.flush()` ensures data is written to the file immediately.
- Between lines, asks if the user wants to continue; breaks if not "yes".
- Catches `IOException` for file-related errors (e.g., file not found).

**Manual Mode Method**
```java
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
```
- Similar to `runFileMode`, but reads input from the console instead of a file.
- Runs an infinite loop until `exit` is entered.
- Prompts the user for an operation, processes it with `processInput`, and writes results to `output.txt`.
- Displays history if `history` is entered.
- Asks to continue after each operation; exits if not "yes".
- Handles file writing errors with `IOException`.

**Input Processing Method**
```java
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
```
- Splits the input string into parts using whitespace `(split("\\s+"))`.
- **Two Parts(Functions)**
  - Parses the second part as a number.
  - Checks the first part for `sqrt`, `abs`, or `round`; uses `Math` methods to compute the result.
  - Throws an exception for unknown functions.
- **Three Parts:**
  - If first part is `roundTo`:
    - Parses the number and decimal places.
    - Multiplies by 10^decimalPlaces, rounds, then divides back to get the result.
  - Otherwise:
    - Parses first and third parts as numbers, second as an operator.
    - Support `+`,`-`,`*`,`/`,`%`,`^`; checks for division/modulus by zero.
    - Throws an exception for invalid operators.
- Outputs the result to console and file, adds it to history.
- **Error Handling:**
  - `NumberFormatException`: Invalid number input.
  - `ArithmeticException`: Division or modulus by zero.
  - `IllegalArgumentException`: Wrong format or operator/function.


 
