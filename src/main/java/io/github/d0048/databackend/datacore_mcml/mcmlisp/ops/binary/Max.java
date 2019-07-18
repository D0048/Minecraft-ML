package io.github.d0048.databackend.datacore_mcml.mcmlisp.ops.binary;

import org.apache.commons.lang3.Range;

public class Max extends BinaryOPBase {
    public Max() {
        super("max");
        setNumArgs(Range.between(2,100));
    }

    @Override
    public String getUsage() {
        return "returns a binary representation of comparision: \n    (" + getName() + " mat1 mat2)";
    }

    @Override
    public double binaryOP(double a, double b) {
        return Math.max(a, b);
    }
}
