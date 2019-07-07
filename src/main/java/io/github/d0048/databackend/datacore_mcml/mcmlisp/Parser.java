package io.github.d0048.databackend.datacore_mcml.mcmlisp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Stack;

public class Parser {
    public static Molecule parse(String input) throws IllegalAccessException {
        if (input.contains("(")&&!isParenthesisMatch(input)) throw new IllegalAccessException("Syntax: Parenthesis mismatch");
        return new Molecule(input);
    }

    static boolean isParenthesisMatch(String str) {
        Stack<Character> stack = new Stack<Character>();
        char c;

        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (c == '(' || c == '{')
                stack.push(c);
            else if (stack.empty())
                return false;
            else if (c == ')') {
                if (stack.pop() != '(')
                    return false;
            } else if (c == '}') {
                if (stack.pop() != '{')
                    return false;
            }
        }
        return stack.empty();
    }

    public static void main(String[] args) throws Exception {
        //REPL
        InputStreamReader in = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(in);
        while (true) {
            try {
                System.out.print("MCMLisp REPL> ");
                String a = br.readLine();
                System.out.println(parse(a) + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
