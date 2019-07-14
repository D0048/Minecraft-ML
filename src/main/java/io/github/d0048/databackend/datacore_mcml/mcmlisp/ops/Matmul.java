package io.github.d0048.databackend.datacore_mcml.mcmlisp.ops;

import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.util.Util;
import org.apache.commons.lang3.Range;

import java.util.Arrays;
import java.util.List;

public class Matmul extends OPBase {
    public Matmul() {
        setName("matmul");
        setNumArgs(Range.between(2, 2));
    }

    @Override
    public MLDataWrap runRaw(List<MLDataWrap> args) throws Exception {
        MLDataWrap mat1 = args.get(0), mat2 = args.get(1);
        int[] newshape;
        try {
            assert mat1.getShape()[1] == mat2.getShape()[0];
            newshape = new int[]{mat1.getShape()[0], mat2.getShape()[1]};
        } catch (Exception e) {
            throw new Exception("Matrix shape is incompatible for multiplication " + Arrays.toString(mat1.getShape()) + " -> " +
                    Arrays.toString(mat2.getShape()));
        }
        MLDataWrap buffer = new MLDataWrap(newshape, new double[Util.arrCumProduct(newshape)]);
        for (int i = 0; i < mat1.getShape()[0]; i++) {
            for (int j = 0; j < mat1.getShape()[1]; j++) {
                for (int k = 0; k < mat2.getShape()[1]; k++) {
                    //buffer.getData()[i * newshape[0] + k] += mat1.getData(new int[]{i, j}) * mat2.getData(new int[]{j, k});
                    double a = mat1.getData(new int[]{i, j});
                    double b = mat2.getData(new int[]{j, k});
                    buffer.getData()[i * newshape[0] + k] += a * b;
                }
            }
        }
        return buffer;
    }

    @Override
    public String getUsage() {
        return "Multiply matix: \n    (" + getName() + "mat1 mat2)";
    }
}
