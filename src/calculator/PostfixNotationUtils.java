package calculator;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostfixNotationUtils {
    private static final Map<String, Integer> operatorsPriority = Map.of("+", 1, "-", 1,
            "*", 2, "/", 2, "^", 3);

    public static List<String> infixToPostfix(String expression) {
        Matcher matcherOperators = Pattern.compile("[/*\\-+]{2}").matcher(expression);
        if (matcherOperators.find()) {
            throw new IllegalArgumentException();
        }
        List<String> postfix = new ArrayList<>();
        if (expression.startsWith("-")) {
            postfix.add("0");
        }
        Deque<String> operatorsStack = new ArrayDeque<>();
        Matcher matcher = Pattern.compile("([a-zA-Z]+|\\d+|[+\\-*/)(])").matcher(expression);
        int openBrackets = 0;
        while (matcher.find()) {
            String group = matcher.group();
            if (group.matches("([a-zA-Z]+|\\d+)")) { //variable or number
                postfix.add(group);
            } else if (group.matches("[\\-+/*]")) { //operator
                if (operatorsStack.isEmpty() || operatorsStack.peekLast().equals("(")
                        || operatorsPriority.get(group) > operatorsPriority.get(operatorsStack.peekLast())) {
                    operatorsStack.offerLast(group);
                } else if(operatorsPriority.get(group) <= operatorsPriority.get(operatorsStack.peekLast())){
                    while (true) {
                        postfix.add(operatorsStack.pollLast());
                        if (operatorsStack.isEmpty() || operatorsStack.peekLast().equals("(")
                                || operatorsPriority.get(group) > operatorsPriority.get(operatorsStack.peekLast())) {
                            break;
                        }
                    }
                    operatorsStack.add(group);
                }
            } else if (group.equals("(")) { // open bracket
                operatorsStack.offerLast(group);
                openBrackets++;
            } else if (group.equals(")")) { // close bracket
                if (openBrackets <= 0) {
                    throw new IllegalArgumentException();
                }
                while (!operatorsStack.peekLast().equals("(")) {
                    postfix.add(operatorsStack.pollLast());
                }
                operatorsStack.pollLast();
                openBrackets--;
            } else {
                System.out.println("Something is wrong");
            }
        }
        if (openBrackets != 0) {
            throw new IllegalArgumentException();
        }
        while (!operatorsStack.isEmpty()) {
            postfix.add(operatorsStack.pollLast());
        }
        return postfix;
    }

    public static int calculatePostfix(List<String> postfix) {
        Deque<Integer> numbersStack = new ArrayDeque<>();
        for (String e : postfix) {
            if (e.matches("\\d+")) {
                numbersStack.offerLast(Integer.parseInt(e));
            } else if (e.equals("+")) {
                int b = numbersStack.pollLast();
                numbersStack.offerLast(numbersStack.pollLast() + b);
            } else if (e.equals("-")) {
                int b = numbersStack.pollLast();
                numbersStack.offerLast(numbersStack.pollLast() - b);
            } else if (e.equals("*")) {
                int b = numbersStack.pollLast();
                numbersStack.offerLast(numbersStack.pollLast() * b);
            } else if (e.equals("/")) {
                int b = numbersStack.pollLast();
                numbersStack.offerLast(numbersStack.pollLast() / b);
            }
        }
        return numbersStack.peekLast();
    }

    public static BigInteger calculatePostfixBigInt(List<String> postfix) {
        Deque<BigInteger> numbersStack = new ArrayDeque<>();
        for (String e : postfix) {
            if (e.matches("\\d+")) {
                numbersStack.offerLast(new BigInteger(e));
            } else if (e.equals("+")) {
                BigInteger b = numbersStack.pollLast();
                numbersStack.offerLast(numbersStack.pollLast().add(b));
            } else if (e.equals("-")) {
                BigInteger b = numbersStack.pollLast();
                numbersStack.offerLast(numbersStack.pollLast().subtract(b));
            } else if (e.equals("*")) {
                BigInteger b = numbersStack.pollLast();
                numbersStack.offerLast(numbersStack.pollLast().multiply(b));
            } else if (e.equals("/")) {
                BigInteger b = numbersStack.pollLast();
                numbersStack.offerLast(numbersStack.pollLast().divide(b));
            }
        }
        return numbersStack.peekLast();
    }
}
