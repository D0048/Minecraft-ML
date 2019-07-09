package io.github.d0048.databackend.datacore_mcml.mcmlisp.ops;

import io.github.d0048.databackend.MLDataWrap;
import org.apache.commons.lang3.Range;

import java.util.List;

public class Add extends OPBase {
    public Add() {
        this.name = "+";
        this.numArgs = Range.between(2, 100);
    }

    @Override
    public MLDataWrap runRaw(List<MLDataWrap> args) {
        checkArguments(args);
        MLDataWrap sum = args.get(0);
        for (int i = 1; i < args.size(); i++) {
            sum = add(args.get(i), sum);
        }
        return sum;
    }

    private MLDataWrap add(MLDataWrap a1, MLDataWrap dst) {
        MLDataWrap tmp = a1;
        if (a1.getData().length > dst.getData().length) {
            a1 = dst;
            dst = tmp;
        }
        int retLen = Math.max(a1.getData().length, dst.getData().length);
        double[] dstData = dst.getData();
        if (dstData.length < retLen) {
            double[] buffer = new double[retLen];
            for (int i = 0; i < dstData.length; i++) buffer[i] = dstData[i];
            dst.setData(dstData = buffer);
        }
        for (int i = 0; i < retLen; i++) {
            dstData[i] += a1.getData()[Math.min(a1.getData().length - 1, i)];
        }
        return dst;
    }
}
