package io.github.d0048.databackend.datacore_mcml.mcmlisp;

import io.github.d0048.MCML;
import io.github.d0048.databackend.MLDataCore;
import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.databackend.datacore_mcml.MLDataCoreMCML;
import io.github.d0048.databackend.datacore_mcml.mcmlisp.ops.OPBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

public class Molecule {
    String fullStr;
    List<Molecule> tokens = new ArrayList<Molecule>();

    public Molecule(String fullStr) {
        fullStr = fullStr.replaceAll("( +)", " ").trim();
        this.fullStr = fullStr.startsWith("(") && fullStr.endsWith(")") ? fullStr.substring(1, fullStr.length() - 1) : fullStr;
        //info("Start parsing: " + this.fullStr);
        if (fullStr.contains(" ")) parse();
    }

    void parse() {
        Stack<Character> stack = new Stack<Character>();
        char c;
        String token = "";
        for (int i = 0; i < fullStr.length(); i++) {
            c = fullStr.charAt(i);
            token += c;
            if (c == '(')
                stack.push(c);
            else if (c == ')') {
                stack.pop();
            }
            if ((c == ' ' || i == fullStr.length() - 1) && stack.size() == 0) {
                tokens.add(new Molecule(token.trim()));
                token = "";
            }
        }
        if (!getOP().isAtom()) throw new IllegalArgumentException("First element must be an atom");
    }

    public MLDataWrap evaluate() {
        if (isAtom()) {
            return MCML.mlDataCore.getDataForID(fullStr);
        } else {
            return Evaluater.performOP(getOP().getFullStr(), getArgs(), false);
        }
    }

    @Override
    public String toString() {
        if (isAtom()) return fullStr;
        String ret = "(" + getOP() + "\n";
        for (Molecule m : getArgs()) {
            String[] mss = m.toString().split("\n");
            for (String ms : mss) {
                ret += "    " + ms + "\n";
            }
        }
        return ret + ")";
    }

    public Molecule getOP() {
        return isAtom() ? this : tokens.get(0);
    }

    public List<Molecule> getArgs() {
        return tokens.subList(1, tokens.size());
    }

    public String getFullStr() {
        return fullStr;
    }

    public List<Molecule> getTokens() {
        return tokens;
    }

    public boolean isAtom() {
        return tokens.size() == 0;
    }


    static void info(String s) {
        MLDataCoreMCML.logger.info(s);
    }
}
