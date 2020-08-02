package calculator;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    private static final Map<String, BigInteger> variablesMap = new HashMap<>();

    public static void main(String[] args) {
        while (true) {
            String line = scanner.nextLine().trim();
            if (line.equals("/exit")) {
                break;
            }
            if (line.equals("/help")) {
                System.out.println("The program supports: * + - / and brackets ()\n" +
                        "it calculates expressions like: 3 + 8 * ((4 + 3) * 2 + 1) - 6 / (2 + 1)\n" +
                        "you can also use variables: var = 5");
                continue;
            }
            if (line.startsWith("/")) {
                System.out.println("Unknown command");
                continue;
            }
            if (line.equals("")) {
                continue;
            }
            String[] array = line.split("=");
            if (array.length > 2) {
                System.out.println("Invalid assignment");
                continue;
            }
            if (array.length == 1) {//probable expression
                try {
                    String expression = extractVariables(line);
                    handleExpression(expression);
                } catch (IllegalArgumentException e) {
                    System.out.println("Unknown variable");
                }
                continue;
            }
            String name = array[0].trim();
            String value = array[1].trim();
            Matcher matcherOnlyLetters = Pattern.compile("[a-zA-Z]+").matcher(name);
            if (!matcherOnlyLetters.matches()) {
                System.out.println("Invalid identifier");
                continue;
            }
            if (variablesMap.containsKey(value)) {
                variablesMap.put(name, variablesMap.get(value));
            } else {
                Matcher matcherInvalidInput = Pattern.compile("(\\d[a-zA-Z]|[a-zA-Z]\\d)").matcher(value);
                if (matcherInvalidInput.find()) {
                    System.out.println("Invalid input");
                    continue;
                }
                try {
                    variablesMap.put(name, new BigInteger(value));
                } catch (NumberFormatException e) {
                    System.out.println("Unknown variable");
                    continue;
                }
            }
        }
        System.out.println("Bye!");
    }

    private static String extractVariables(String expression) { //changes variables names to values
        Matcher matcher = Pattern.compile("[a-zA-Z]+").matcher(expression);
        while (matcher.find()) {
            String name = matcher.group();
            if (variablesMap.containsKey(name)) {
                expression = expression.replaceAll(name, variablesMap.get(name).toString());
            } else {
                throw new IllegalArgumentException();
            }
        }
        return expression;
    }

    private static void handleExpression(String line) {
        if (line.endsWith("+") || line.endsWith("-")) {
            System.out.println("Invalid expression");
            return;
        }
        Matcher matcherNotAllowedChar = Pattern.compile("[a-zA-Z]").matcher(line);
        Matcher matcherNoSignBetweenNumbers = Pattern.compile("\\d\\s+\\d").matcher(line);
        if (matcherNoSignBetweenNumbers.find() || matcherNotAllowedChar.find()) {
            System.out.println("Invalid expression");
            return;
        }
        line = line.replaceAll("\\s+", "");
        Matcher matcherCompressingSigns = Pattern.compile("[+-][+-]").matcher(line);
        while (matcherCompressingSigns.find()) {
            line = line.replaceAll("(\\+\\+|--)", "+").replaceAll("(\\+-|-\\+)", "-");
        }
        try {
            List<String> postfix = PostfixNotationUtils.infixToPostfix(line);
            System.out.println(PostfixNotationUtils.calculatePostfixBigInt(postfix));
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("Invalid expression");
        }
    }
}
