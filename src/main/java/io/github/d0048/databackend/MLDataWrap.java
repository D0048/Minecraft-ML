package io.github.d0048.databackend;

import java.util.Arrays;

import io.github.d0048.databackend.datacore_mcml.MLDataCoreMCML;
import io.github.d0048.util.Util;
import net.minecraft.util.text.TextFormatting;

public class MLDataWrap {
    int[] shape;
    double[] data;


    public int[] getShape() {
        return shape;
    }

    public void setShape(int[] shape) {
        if (shape.length < 1) {
            shape = Arrays.copyOf(shape, shape.length + 1);
            shape[shape.length - 1] = 0;
        }
        this.shape = shape;
    }

    public double getData(int[] index) {
        int indexInternal = 0;
        for (int i = 0; i < index.length; i++) {
            int shapesum = 1;
            for (int j = i; j < getShape().length - 1; j++) {
                shapesum *= getShape()[j];
            }
            indexInternal += index[i] * shapesum;
        }
        //System.out.println(Arrays.toString(index) + " -> " + indexInternal);
        return getData()[indexInternal];
    }

    public void setData(int[] index, double val) {
        int indexInternal = 0;
        for (int i = 0; i < index.length; i++) {
            int shapesum = 1;
            for (int j = i; j < getShape().length; j++) {
                shapesum *= getShape()[j];
            }
            indexInternal += index[i] * shapesum;
        }
        getData()[indexInternal] = val;
    }

    public double[] getData() {
        return data;
    }

    public void setData(double[] data) {
        this.data = data;
        if (Util.arrCumProduct(getShape()) < data.length) setShape(new int[]{data.length});
    }

    public MLDataWrap(int[] shape, double[] data) {
        this.setShape(shape);
        this.setData(data);
    }

    public MLDataWrap(double[] data) {
        this.setShape(new int[]{data.length});
        this.setData(data);
    }

    public static MLDataWrap sameValue(int size, double val) {
        double[] datatmp = new double[size];
        for (int i = 0; i < datatmp.length; i++) {
            datatmp[i] = val;
        }
        return new MLDataWrap(datatmp);
    }

    public static MLDataWrap whiteData(int size) {
        return sameValue(size, 0);
    }

    public static MLDataWrap fromStringValue(String str, boolean force) {
        //TODO: matrix support
        double[] buffer = Util.parseDoubleArr(str, force);
        int[] shape = Util.parseArrShape(str);
        return new MLDataWrap(shape, buffer);
    }

    public static MLDataWrap fromStringShape(String str, boolean force) {
        int[] sizeBuffer = Util.parseIntArr(str, force);
        double[] buffer = new double[Util.arrCumProduct(sizeBuffer)];
        return new MLDataWrap(sizeBuffer, buffer);
    }

    @Override
    public String toString() {
        return "Data Wrap" + " of shape " + TextFormatting.YELLOW + Arrays.toString(getShape())
                + TextFormatting.LIGHT_PURPLE;
    }

    static void info(String s) {
        MLDataCoreMCML.logger.info(s);
    }

    public MLDataWrap clone() {
        return new MLDataWrap(getShape(), getData());
    }

}