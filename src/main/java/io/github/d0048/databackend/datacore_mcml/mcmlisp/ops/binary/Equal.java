package io.github.d0048.databackend.datacore_mcml.mcmlisp.ops.binary;

import io.github.d0048.databackend.datacore_mcml.mcmlisp.Evaluater;
import org.apache.commons.lang3.Range;

public class Equal extends BinaryOPBase {
    public Equal() {
        super("eq");
        setNumArgs(Range.between(2,100));
    }


    @Override
    public String getUsage() {
        return "returns a binary representation of comparision: \n    (" + getName() + " mat1 mat2)";
    }

    @Override
    public double binaryOP(double a, double b) {
        return Math.abs(a - b) <= Evaluater.compareTolerance ? 1 : 0;
    }
}
