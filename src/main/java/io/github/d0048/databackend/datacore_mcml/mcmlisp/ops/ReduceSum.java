package io.github.d0048.databackend.datacore_mcml.mcmlisp.ops;

import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.util.Util;
import org.apache.commons.lang3.Range;

import java.util.Arrays;
import java.util.List;

public class ReduceSum extends OPBase {
    public ReduceSum() {
        setName("reduce_sum");
        setNumArgs(Range.between(2, 2));
    }

    @Override
    public MLDataWrap runRaw(List<MLDataWrap> args) throws Exception {
        MLDataWrap data = args.get(0);
        int[] axes4Reduce = Util.double2IntArray(args.get(1).getData());
        for (int i : axes4Reduce) {
            if (i > data.getShape().length-1)
                throw new Exception("Axis for reduction \"" + i + "\" is out of bound for shape: " + Arrays.toString(data.getShape()));
        }
        MLDataWrap newData = data;
        for (int i = 0; i < axes4Reduce.length; i++) newData = sum1Axis(newData, i);
        System.out.println("reduce_sum: " + Arrays.toString(data.getShape()) + " -> " + Arrays.toString(newData.getShape()));
        return newData;
    }

    static MLDataWrap sum1Axis(MLDataWrap data, int axis2Sum) {
        int[] newShape = data.getShape().clone();
        newShape[axis2Sum] = 1;
        MLDataWrap newData = new MLDataWrap(newShape);
        traverseNDDataWrap(new int[]{}, data, newData, axis2Sum);
        return newData;
    }

    static void traverseNDDataWrap(int[] previousD, MLDataWrap wrap, MLDataWrap newData, int axis2Sum) {
        int[] shape = wrap.getShape();
        if (previousD.length + 1 == wrap.getShape().length) {
            for (int i = 0; i < shape[shape.length - 1]; i++) {
                int[] index = Arrays.copyOf(previousD, previousD.length + 1);
                index[index.length - 1] = i;
                System.out.println(Arrays.toString(index));
                //Operate on final index
                int[] indexNew = index.clone();
                indexNew[axis2Sum] = 0;
                newData.setData(indexNew, newData.getData(indexNew) + wrap.getData(index));
            }
        } else {
            for (int i = 0; i < shape[previousD.length]; i++) {
                int[] index = Arrays.copyOf(previousD, previousD.length + 1);
                index[index.length - 1] = i;
                traverseNDDataWrap(index, wrap, newData, axis2Sum);
            }
        }
    }

    @Override
    public String getUsage() {
        return "Sum matrix along given axes: \n    (" + getName() + "mat [indexes,to,sum]) e.g (reduce_sum mat [1,0])";
    }
}
