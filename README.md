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
34+(76-45)*2 - abs(-5)
roundTo 3.14159 2
history
exit
```

## Output
**Results are written to `output.txt`. Example:**
```text
34+(76-45)*2 - abs(-5) = 91.0
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
import java.util.Stack;
```
- `Arraylist` and `List`: Used to create a dynamic list for storing calculation history.
- `Scanner`: Reads input from the console or a file
- `File` and `FileWriter`: Handle file operations for reading `input.txt` and writing to `output.txt`.
- `IOException`: Catches errors related to file operations.
- `Stack`: Used for parsing expressions with numbers and operators.

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
- Calls `runFileMode` for "file", `runManualMode` for "manual", or defaults to manual mode if the input is invalid.
- Closes the scanner.

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
            System.out.println("Enter expression (e.g., 34+(76-45)*2-abs(-5), history, exit): ");
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
        double result = evaluate(input);
        System.out.println("Result: " + result);
        writer.write(input + " = " + result + "\n");
        history.add(input + " = " + result);
    } catch (Exception e) {
        String error = "Error: " + e.getMessage();
        System.out.println(error);
        writer.write(error + "\n");
    }
}          
```
- Takes an expressio and a `FileWriter`.
- Calls `evaluate` to compute the result.
- Prints the result to console and writes it to `output.txt`(e.g., "2 + 3 = 5").
- Adds the expression and result to `history`.
- Catches exceptions (e.g., invalid input) and logs errors to both console and file.

**Expression Evaluation Methods**
**evaluate**
```java
private static double evaluate(String expr) {
    expr = expr.replaceAll("\\s+", ""); // Remove whitespace
    return evaluateExpression(expr, 0, expr.length() - 1);
}
```
- Removes all whitespaces from the expression (e.g. "2 + 3" to "2+3").
-  Calls `evaluateExpression` to process the full string.
**evaluateExpression**
```java
private static double evaluateExpression(String expr, int start, int end) {
    Stack<Double> numbers = new Stack<>();
    Stack<Character> operators = new Stack<>();
    StringBuilder num = new StringBuilder();

    for (int i = start; i <= end; i++) {
        char c = expr.charAt(i);
        if (Character.isDigit(c) || c == '.') {
            num.append(c);
        } else if (c == '(') {
            int closing = findClosingBracket(expr, i);
            double innerResult = evaluateExpression(expr, i + 1, closing - 1);
            i = closing;
            numbers.push(innerResult);
        } else if (c == '-' && (i == start || expr.charAt(i - 1) == '(')) {
            num.append(c);
        } else if (isOperator(c)) {
            if (num.length() > 0) {
                numbers.push(Double.parseDouble(num.toString()));
                num.setLength(0);
            }
            while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                performOperation(numbers, operators.pop());
            }
            operators.push(c);
        } else if (Character.isLetter(c)) {
            String func = extractFunction(expr, i);
            i += func.length() - 1;
            int closing = findClosingBracket(expr, i + 1);
            double arg = evaluateExpression(expr, i + 2, closing - 1);
            i = closing;
            numbers.push(applyFunction(func, arg));
        }
    }
    if (num.length() > 0) {
        numbers.push(Double.parseDouble(num.toString()));
    }
    while (!operators.isEmpty()) {
        performOperation(numbers, operators.pop());
    }
    return numbers.pop();
}
```
- Core recursive function for parsing and evaluating expressions.
- Uses `numbers` (for operands) and `operators` (for operators) stacks.
- Processes each character:
  - Digits/Decimals: Builds a number string.
  - `(`: Recursively evaluates the inner expression.
  - `-`:Treats as negative if at start or after `(`.
  - Operators: Applies higher-precedence operators first, then pushes the current one.
  - Letters: Handles functions like `abs`, evaluates their argument, and applies them.
- Finalizes by applying remaining operators and returns the result.

**Helper Methods**
**findClosingBracket**
```java
private static String extractFunction(String expr, int start) {
    StringBuilder func = new StringBuilder();
    while (start < expr.length() && Character.isLetter(expr.charAt(start))) {
        func.append(expr.charAt(start));
        start++;
    }
    return func.toString().toLowerCase();
}
```
- Finds matching `)` for an opening `(` by tracking nested parentheses.
- Throws an error if no match if found.
**extractFunction**
```java
private static String extractFunction(String expr, int start) {
    StringBuilder func = new StringBuilder();
    while (start < expr.length() && Character.isLetter(expr.charAt(start))) {
        func.append(expr.charAt(start));
        start++;
    }
    return func.toString().toLowerCase();
}
```
- Extracts a function name (e.g., "abs") from letters starting at a position.
**applyFunction**
```java
private static double applyFunction(String func, double arg) {
    switch (func) {
        case "abs": return Math.abs(arg);
        case "sqrt": return Math.sqrt(arg);
        case "round": return Math.round(arg);
        default: throw new IllegalArgumentException("Unknown function: " + func);
    }
}
```
- Maps function names to `Math` methods.
- Throws an error for unrecognized functions.
**isOperator**
```java
private static boolean isOperator(char c) {
    return c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^';
}
```
- Checks if a character is a valid operator.
**precedence**
```java
private static int precedence(char op) {
    switch (op) {
        case '+': case '-': return 1;
        case '*': case '/': case '%': return 2;
        case '^': return 3;
        default: return 0;
    }
}
```
- Assings precedence levels to operators (e.g., `*`>`+`).
**performOperation**
```java
private static void performOperation(Stack<Double> numbers, char op) {
    double b = numbers.pop();
    double a = numbers.pop();
    switch (op) {
        case '+': numbers.push(a + b); break;
        case '-': numbers.push(a - b); break;
        case '*': numbers.push(a * b); break;
        case '/':
            if (b == 0) throw new ArithmeticException("Division by zero");
            numbers.push(a / b);
            break;
        case '%':
            if (b == 0) throw new ArithmeticException("Modulus by zero");
            numbers.push(a % b);
            break;
        case '^': numbers.push(Math.pow(a, b)); break;
    }
}
```
- Pops two numbers and an operator, computes the result, and pushes it back.
- Checks for division/modulus by zero and throws exceptions.






