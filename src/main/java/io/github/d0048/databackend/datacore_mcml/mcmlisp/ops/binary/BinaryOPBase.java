package io.github.d0048.databackend.datacore_mcml.mcmlisp.ops.binary;

import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.databackend.datacore_mcml.mcmlisp.ops.OPBase;
import io.github.d0048.util.Util;
import org.apache.commons.lang3.Range;

import java.util.List;

public abstract class BinaryOPBase extends OPBase {

    public BinaryOPBase(String name) {
        setName(name);
        setNumArgs(Range.between(2, 2));
    }

    @Override
    public MLDataWrap runRaw(List<MLDataWrap> args) {
        checkArguments(args);
        MLDataWrap maxWrap = args.get(0);
        for (int i = 1; i < args.size(); i++) {
            if (Util.arrCumProduct(maxWrap.getShape()) < Util.arrCumProduct(args.get(i).getShape())) {
                maxWrap = args.get(i);
            }
        }
        maxWrap = maxWrap.clone();
        int[] maxShape = maxWrap.getShape();
        int maxLen = Util.arrCumProduct(maxShape);

        MLDataWrap ret = MLDataWrap.sameValue(maxLen, 1);
        ret.setShape(maxShape);
        for (MLDataWrap dw : args) {
            if (dw.getData().length == 1) {
                for (int i = 0; i < maxLen; i++) ret.getData()[i] = binaryOP(ret.getData()[i], dw.getData()[0]);
            } else {
                for (int i = 0; i < dw.getData().length; i++) {
                    ret.getData()[i] = binaryOP(ret.getData()[i], dw.getData()[i]);
                }
            }
        }
        return ret;
    }

    public abstract double binaryOP(double a, double b);
}
