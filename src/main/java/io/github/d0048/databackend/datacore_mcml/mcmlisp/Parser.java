package io.github.d0048.databackend.datacore_mcml.mcmlisp;

import io.github.d0048.MCML;
import io.github.d0048.databackend.MLDataCore;
import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.databackend.datacore_mcml.MLDataCoreMCML;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Stack;

public class Parser {
    public static Molecule parse(String input) throws Exception {
        if (input.contains("(") && !isParenthesisMatch(input)) throw new Exception("Syntax: Parenthesis mismatch");
        return new Molecule(input);
    }

    static boolean isParenthesisMatch(String str) {
        String bone = str.replaceAll("[^\\)\\(\\}\\{\\[\\]]", "");
        int lastlen = bone.length();
        while (lastlen != 0) {
            bone = bone.replace("{}", "").replace("[]", "").replace("()", "");
            if (bone.length() == lastlen) return false;
            lastlen=bone.length();
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
        MCML.mlDataCore = new MLDataCoreMCML();
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
                MLDataWrap data = MCML.mlDataCore.registerDataForID(a);
                //MLDataWrap data = m.evaluate();
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
