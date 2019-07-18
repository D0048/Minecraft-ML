package io.github.d0048.databackend.datacore_mcml.mcmlisp.ops;

import io.github.d0048.databackend.MLDataWrap;
import org.apache.commons.lang3.Range;

import java.util.List;

public class Add extends OPBase {
    public Add() {
        setName("+");
        setNumArgs(Range.between(2, 100));
    }

    @Override
    public MLDataWrap runRaw(List<MLDataWrap> args) {
        checkArguments(args);
        int maxLen = args.get(0).getData().length;
        int[] maxShape = args.get(0).getShape();
        for (int i = 1; i < args.size(); i++) {
            if (maxLen < args.get(i).getData().length) {
                maxLen = Math.max(maxLen, args.get(i).getData().length);
                maxShape = args.get(i).getShape();
            }
        }
        MLDataWrap ret = new MLDataWrap(maxShape);
        for (MLDataWrap dw : args) {
            if (dw.getData().length == 1) {
                for (int i = 0; i < maxLen; i++) ret.getData()[i] += dw.getData()[0];
            } else {
                for (int i = 0; i < dw.getData().length; i++) {
                    ret.getData()[i] += dw.getData()[i];
                }
            }
        }
        return ret;
    }


    @Override
    public String getUsage() {
        return "Adding data along their size: \n    (" + getName() + " data_1 data_2 ... data_n)";
    }

}
