package io.github.d0048.databackend.datacore_mcml.mcmlisp.ops;

import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.util.Util;
import org.apache.commons.lang3.Range;

import java.util.Arrays;
import java.util.List;

public class Transpose extends OPBase {
    public Transpose() {
        setName("transpose");
        setNumArgs(Range.between(2, 2));
    }

    @Override
    public MLDataWrap runRaw(List<MLDataWrap> args) throws Exception {
        MLDataWrap data = args.get(0);
        int[] newIndex = Util.double2IntArray(args.get(1).getData()), oldIndex = new int[newIndex.length];
        for (int i = 0; i < oldIndex.length; i++)
            oldIndex[i] = i;
        if (newIndex.length != data.getShape().length || Util.arrCumSum(newIndex) != Util.arrCumSum(oldIndex))
            throw new Exception(
                    "New index is not compatible: " + Arrays.toString(data.getShape()) + " -> " + Arrays.toString(newIndex));

        MLDataWrap newData =
                new MLDataWrap(arrSwap(oldIndex, newIndex, data.getShape()), new double[Util.arrCumProduct(data.getShape())]);

        traverseNDDataWrap(new int[]{}, data,oldIndex,newIndex,newData);
        //System.out.println("Transpose: " + Arrays.toString(data.getShape()) + " -> " + Arrays.toString(newData.getShape()));
        return newData;
    }

    static void traverseNDDataWrap(int[] previousD, MLDataWrap wrap, int[] oldIndex, int[] newIndex, MLDataWrap newData) {
        int[] shape = wrap.getShape();
        if (previousD.length + 1 == wrap.getShape().length) {
            for (int i = 0; i < shape[shape.length - 1]; i++) {
                int[] index = Arrays.copyOf(previousD, previousD.length + 1);
                index[index.length - 1] = i;
                //System.out.println(Arrays.toString(index));
                int[] ptr2NewData = arrSwap(oldIndex, newIndex, index);
                newData.setData(ptr2NewData, wrap.getData(index));
                //Operate on final index
            }
        } else {
            for (int i = 0; i < shape[previousD.length]; i++) {
                int[] index = Arrays.copyOf(previousD, previousD.length + 1);
                index[index.length - 1] = i;
                traverseNDDataWrap(index, wrap, oldIndex, newIndex, newData);
            }
        }
    }

    static int[] arrSwap(int[] pre, int[] post, int[] src) {
        int[] dst = src.clone();
        for (int i = 0; i < pre.length; i++) {
            swap(pre[i], post[i], src, dst);
        }
        return dst;
    }

    static void swap(int a, int b, int[] src, int[] dst) {
        dst[b] = src[a];
    }

    @Override
    public String getUsage() {
        return "Transpose matrix using new index: \n    (" + getName() + "mat [new index]) e.g (transpose mat [2,1,0])";
    }
}
