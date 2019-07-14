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
        MLDataWrap src = args.get(0), newshapewarp = args.get(1);
        int[] newshape = Util.double2IntArray(args.get(1).getData());
        if (Util.arrCumProduct(newshape) > Util.arrCumProduct(src.getShape()))
            throw new Exception("New shape is larger than old " +
                    "one: " + Arrays.toString(src.getShape()) + " -> " + Arrays.toString(newshape));
        MLDataWrap ret = src.clone();
        ret.setShape(newshape);
        return ret;
    }

    @Override
    public String getUsage() {
        return "Multiply matix: \n    (" + getName() + " [new_shape])";
    }
}
