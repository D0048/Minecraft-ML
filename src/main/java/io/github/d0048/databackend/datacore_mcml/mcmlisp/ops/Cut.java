package io.github.d0048.databackend.datacore_mcml.mcmlisp.ops;

import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.util.Util;
import org.apache.commons.lang3.Range;

import java.util.List;

public class Cut extends OPBase {
    public Cut() {
        setName("cut");
        setNumArgs(Range.between(3, 3));
    }

    @Override
    public MLDataWrap runRaw(List<MLDataWrap> args) {
        MLDataWrap src = args.get(0);
        int start = (int) args.get(1).getData()[0], end = (int) args.get(2).getData()[0];
        if (end < 0) end = src.getData().length + end + 1;
        if (start == end) {
            end += 1;
        } else if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        start = Util.clipIntoRange(start, 0, src.getData().length - 1);
        end = Util.clipIntoRange(end, 1, src.getData().length);
        /*
        Range validRange = Range.between(0, src.getData().length), sliceRange = Range.between(start, end);
        if (!validRange.contains(start) || !validRange.contains(end))
            throw new IllegalArgumentException("Shape mismatch in slice: slice " + start + "," + end + " from " + validRange);
         */
        int[] newShape = new int[]{end - start};
        MLDataWrap buffer = new MLDataWrap(newShape, new double[newShape[0]]);
        for (int i = start; i < end; i++) {
            buffer.getData()[i - start] = src.getData()[i];
        }
        return buffer;
    }

    @Override
    public String getUsage() {
        //return "Cut data along their length: \n    (" + getName() + " [shape_from_inclusive] [shape_to_exclusive])";
        return "Cut data assuming it's reshaped into 1D: \n    (" + getName() + " [index_from_inclusive] [index_to_exclusive])";
    }

}
