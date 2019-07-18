package io.github.d0048.databackend.datacore_mcml.mcmlisp.ops.binary;

import io.github.d0048.databackend.datacore_mcml.mcmlisp.Evaluater;

public class Xor extends BinaryOPBase {
    public Xor() {
        super("xor");
    }

    @Override
    public String getUsage() {
        return "returns a binary representation of comparision: \n    (" + getName() + " mat1 mat2)";
    }

    @Override
    public double binaryOP(double a, double b) {
        return ((Math.abs(a - 1) <= Evaluater.compareTolerance)
                !=
                (Math.abs(b - 1) <= Evaluater.compareTolerance)) ? 1 : 0;
    }
}
