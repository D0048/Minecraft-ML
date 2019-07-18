package io.github.d0048.databackend.datacore_mcml.mcmlisp.ops;

import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.util.Util;
import org.apache.commons.lang3.Range;

import java.util.Arrays;
import java.util.List;

public class Squeeze extends OPBase {
    public Squeeze() {
        setName("squeeze");
        setNumArgs(Range.between(1, 1));
    }

    @Override
    public MLDataWrap runRaw(List<MLDataWrap> args) throws Exception {
        MLDataWrap src = args.get(0).clone();
        int[] shape = src.getShape();
        int numOnes = 0;
        for (int i : shape) numOnes += (i == 1) ? 1 : 0;
        int[] newShape;
        if (numOnes < shape.length) {
            newShape = new int[shape.length - numOnes];
            int j = 0;
            for (int i = 0; i < shape.length; i++) {
                if (shape[i] != 1)
                    newShape[j++] = shape[i];
            }
        }
        else{
            newShape=new int[]{1};
        }
        src.setShape(newShape);
        return src;
    }


    @Override
    public String getUsage() {
        return "Remove all ones dimensions in shape: \n    (" + getName() + " mat) e.g [[1]]->[1]";
    }

}

