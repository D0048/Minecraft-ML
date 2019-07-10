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
        while (shape.length < 3) {
            shape = Arrays.copyOf(shape, shape.length + 1);
            shape[shape.length - 1] = 1;
        }
        this.shape = shape;
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

    public static MLDataWrap fromStringValue(String str) {
        return fromStringValue(str, true);
    }

    public static MLDataWrap fromStringValue(String str, boolean force) {
        str = str.trim();
        String[] strArgs = str.substring(1, str.length() - 1).trim().split("\\s*,\\s*");
        double[] buffer = new double[strArgs.length];
        for (int i = 0; i < strArgs.length; i++) {
            buffer[i] = Double.parseDouble(force ? strArgs[i].replaceAll("[^123456789\\.]", "") : strArgs[i]);
        }
        return new MLDataWrap(buffer);
    }

    public static MLDataWrap fromStringShape(String str) {
        str = str.trim();
        String[] strArgs = str.substring(1, str.length() - 1).trim().split("\\s*,\\s*");
        int[] sizeBuffer = new int[strArgs.length];
        for (int i = 0; i < strArgs.length; i++) {
            sizeBuffer[i] = Integer.parseInt(strArgs[i].replaceAll("[^123456789\\.]", ""));
        }
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

}