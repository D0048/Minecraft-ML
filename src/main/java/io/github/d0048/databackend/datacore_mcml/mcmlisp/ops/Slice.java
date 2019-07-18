package io.github.d0048.databackend.datacore_mcml.mcmlisp.ops;

import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.util.Util;
import org.apache.commons.lang3.Range;

import java.util.Arrays;
import java.util.List;

public class Slice extends OPBase {
    public Slice() {
        setName("slice");
        setNumArgs(Range.between(3, 3));
    }

    @Override
    public MLDataWrap runRaw(List<MLDataWrap> args) throws Exception {
        MLDataWrap src = args.get(0);
        int[] start = Util.double2IntArray(args.get(1).getData()), end = Util.double2IntArray(args.get(2).getData());
        if (start.length != end.length || start.length != src.getShape().length)
            throw new Exception("slice start and end mismatch: " + Arrays.toString(start) + " -> " + Arrays.toString(end));
        for (int i = 0; i < start.length; i++) {
            if (end[i] > src.getShape()[i] || start[i] > end[i])
                throw new Exception("Malformed slice boundaries: " + Arrays.toString(start) + " -> " + Arrays.toString(end));
        }
        int[] newShape = Util.arrCumDiff(end, start);
        MLDataWrap newData = new MLDataWrap(newShape);
        traverseNDDataWrap(new int[]{}, src, newData, start, end);
        //System.out.println("slice: " + Arrays.toString(src.getShape()) + " -> " + Arrays.toString(newData.getShape()));
        return newData;
    }

    static void traverseNDDataWrap(int[] previousD, MLDataWrap wrap, MLDataWrap newData, int[] start, int[] end) {
        int[] shape = wrap.getShape();
        if (previousD.length + 1 == wrap.getShape().length) {
            for (int i = 0; i < shape[shape.length - 1]; i++) {
                int[] index = Arrays.copyOf(previousD, previousD.length + 1);
                index[index.length - 1] = i;
                //Operate on final index
                boolean isInRange = true;
                for (int j = 0; j < start.length; j++)
                    if (!(index[j] >= start[j] && index[j] < end[j])) {
                        isInRange = false;
                        break;
                    }
                if (isInRange) {
                    //System.out.println(Arrays.toString(index));
                    //System.out.println(Arrays.toString(start) + "->" + Arrays.toString(end));
                    newData.setData(Util.arrCumDiff(index, start), wrap.getData(index));
                }
            }
        } else {
            for (int i = 0; i < shape[previousD.length]; i++) {
                int[] index = Arrays.copyOf(previousD, previousD.length + 1);
                index[index.length - 1] = i;
                traverseNDDataWrap(index, wrap, newData, start, end);
            }
        }
    }

    @Override
    public String getUsage() {
        return "Slice data between given axes: \n    (" + getName() + " [index,from,inclusive] [index,to,exclusive])";
    }

}

