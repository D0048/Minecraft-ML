package io.github.d0048.databackend.datacore_mcml.mcmlisp;

import io.github.d0048.MCML;
import io.github.d0048.databackend.MLDataCore;
import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.databackend.datacore_mcml.MLDataCoreMCML;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Stack;

public class Parser {
    public static Molecule parse(String input) throws Exception {
        if (input.contains("(") && !isParenthesisMatch(input)) throw new Exception("Syntax: Parenthesis mismatch");
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
        MCML.mlDataCore=new MLDataCoreMCML();
        Evaluater.init();
        //REPL
        InputStreamReader in = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(in);
        while (true) {
            try {
                System.out.print("MCMLisp REPL> ");
                String a = br.readLine();
                Molecule m = parse(a);
                System.out.println(m + "");
                MLDataWrap data=m.evaluate();
                System.out.println(data);
                System.out.println(Arrays.toString(data.getData()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void info(String s) {
        MLDataCoreMCML.logger.info(s);
    }
}
