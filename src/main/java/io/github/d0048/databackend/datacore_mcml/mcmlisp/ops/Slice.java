package io.github.d0048.databackend.datacore_mcml.mcmlisp.ops;

import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.util.Util;
import org.apache.commons.lang3.Range;

import java.util.Arrays;
import java.util.List;

public class Slice extends OPBase {
    public Slice() {
        this.name = "slice";
        this.numArgs = Range.between(3, 3);
    }

    @Override
    public MLDataWrap runRaw(List<MLDataWrap> args) {/*
        MLDataWrap start = args.get(0), end = args.get(1), src = args.get(2);
        if (start.getData().length != end.getData().length || end.getData().length != src.getShape().length) {
            throw new IllegalArgumentException("Shape mismatch in slice");
        }
        int[] newShape = new int[end.getData().length];
        for (int i = 0; i < src.getShape().length; i++) {
            newShape[i] = (int) (end.getShape()[i] - start.getData()[i]) - 1;
        }
        MLDataWrap buffer = new MLDataWrap(newShape, new double[Util.arrCumProduct(newShape)]);
        for(int i=0;i<newShape.length;i++){
        }*/
        MLDataWrap src = args.get(0);
        int start = (int) args.get(1).getData()[0], end = (int) args.get(2).getData()[0];
        if (end < 0) end = src.getData().length;
        if (start == end) end += 1;
        else if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        /*
        Range validRange = Range.between(0, src.getData().length), sliceRange = Range.between(start, end);
        if (!validRange.contains(start) || !validRange.contains(end))
            throw new IllegalArgumentException("Shape mismatch in slice: slice " + start + "," + end + " from " + validRange);
         */
        int[] newShape = new int[end - start];
        MLDataWrap buffer = new MLDataWrap(newShape, new double[newShape[0]]);
        for (int i = start; i < end; i++) {
            buffer.getData()[i - start] = src.getData()[start];
        }
        System.out.println(buffer.toString());
        return buffer;
    }

    @Override
    public String getUsage() {
        //return "Slice data along their length: \n    (" + getName() + " [shape_from_inclusive] [shape_to_exclusive])";
        return "Slice data: \n    (" + getName() + " [index_from_inclusive] [index_to_exclusive])";
    }
}
