import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

public class Calculator {
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
            System.out.println("Invalid mode. Defaulting to manual.");
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

    private static double evaluate(String expr) {
        expr = expr.replaceAll("\\s+", ""); // Remove whitespace
        return evaluateExpression(expr, 0, expr.length() - 1);
    }

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

    private static int findClosingBracket(String expr, int openPos) {
        int count = 1;
        for (int i = openPos + 1; i < expr.length(); i++) {
            if (expr.charAt(i) == '(') count++;
            if (expr.charAt(i) == ')') count--;
            if (count == 0) return i;
        }
        throw new IllegalArgumentException("Mismatched parentheses");
    }

    private static String extractFunction(String expr, int start) {
        StringBuilder func = new StringBuilder();
        while (start < expr.length() && Character.isLetter(expr.charAt(start))) {
            func.append(expr.charAt(start));
            start++;
        }
        return func.toString().toLowerCase();
    }

    private static double applyFunction(String func, double arg) {
        switch (func) {
            case "abs": return Math.abs(arg);
            case "sqrt": return Math.sqrt(arg);
            case "round": return Math.round(arg);
            default: throw new IllegalArgumentException("Unknown function: " + func);
        }
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^';
    }

    private static int precedence(char op) {
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
            case '%':
                return 2;
            case '^':
                return 3;
            default:
                return 0;
        }
    }

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
}
