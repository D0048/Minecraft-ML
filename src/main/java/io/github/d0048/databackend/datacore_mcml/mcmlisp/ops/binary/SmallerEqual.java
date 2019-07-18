package io.github.d0048.databackend.datacore_mcml.mcmlisp.ops.binary;

public class SmallerEqual  extends BinaryOPBase {
    public SmallerEqual() {
        super("<eq");
    }

    @Override
    public String getUsage() {
        return "returns a binary representation of comparision: \n    (" + getName() + " mat1 mat2)";
    }

    @Override
    public double binaryOP(double a, double b) {
        return a <= b ? 1 : 0;
    }
}
