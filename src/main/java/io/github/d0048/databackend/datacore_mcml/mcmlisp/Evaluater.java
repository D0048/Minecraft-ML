package io.github.d0048.databackend.datacore_mcml.mcmlisp;

import io.github.d0048.MCML;
import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.databackend.datacore_mcml.MLDataCoreMCML;
import io.github.d0048.databackend.datacore_mcml.mcmlisp.ops.Add;
import io.github.d0048.databackend.datacore_mcml.mcmlisp.ops.Multiply;
import io.github.d0048.databackend.datacore_mcml.mcmlisp.ops.OPBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Evaluater {
    public static HashMap<String, OPBase> opMap = new HashMap();

    public static void init() {
        registerOP(new Add());
        registerOP(new Multiply());
    }

    static void registerOP(OPBase op) {
        opMap.put(op.getName(), op);
        info("Registered OP: " + op);
    }

    public static OPBase getOP(String name) {
        OPBase op;
        if ((op = opMap.get(name)) == null) {
            throw new IllegalArgumentException("Operation \"" + name + "\" is undefined!");
        }
        return opMap.get(name);
    }

    public static MLDataWrap performOP(String op, List<Molecule> args, boolean useCache) {
        MLDataWrap ret = getOP(op).run(args);
        //TODO Implement caching
        return ret;
    }

    public static List<MLDataWrap> evaluateAll(List<Molecule> args) {
        List<MLDataWrap> datas = new ArrayList<MLDataWrap>();
        for (Molecule m : args) {
            datas.add(m.evaluate());
        }
        return datas;
    }

    static void info(String s) {
        MLDataCoreMCML.logger.info(s);
    }
}