package io.github.d0048.databackend;

import java.awt.*;
import java.awt.image.BufferedImage;
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
        return getData()[extIndex2InternalIndex(index)];
    }

    public void setData(int[] index, double val) {
        getData()[extIndex2InternalIndex(index)] = val;
    }

    public int extIndex2InternalIndex(int[] index) {
        int indexInternal = 0;
        for (int i = 0; i < index.length; i++) {
            int shapesum = 1;
            for (int j = i + 1; j < getShape().length; j++) {
                shapesum *= getShape()[j];
            }
            indexInternal += index[i] * shapesum;
        }
        //System.out.println(Arrays.toString(index) + " -> " + indexInternal);
        return indexInternal;
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

    public MLDataWrap(int[] shape) {
        this.setShape(shape);
        this.setData(new double[Util.arrCumProduct(shape)]);
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

    public static MLDataWrap fromBufferedImage(BufferedImage img) {
        int[] size = new int[]{img.getWidth(), 3, img.getHeight()};
        double[] buffer = new double[Util.arrCumProduct(size)];
        MLDataWrap ret = new MLDataWrap(size, buffer);
        for (int i = 0; i < size[0]; i++)
            for (int j = 0; j < size[2]; j++) {
                Color color = new Color(img.getRGB(i, j));
                ret.setData(new int[]{i, 0, j}, color.getRed());
                ret.setData(new int[]{i, 1, j}, color.getGreen());
                ret.setData(new int[]{i, 2, j}, color.getBlue());
            }

        return ret;
    }

    @Override
    public String toString() {
        return "Data Wrap " + TextFormatting.YELLOW +
                (getData().length < 9 ? Arrays.toString(getData()) :
                        "[" + getData()[0] + "..." + getData()[getData().length - 1] + "]") + TextFormatting.LIGHT_PURPLE
                + " of shape " + TextFormatting.YELLOW + Arrays.toString(getShape())
                + TextFormatting.LIGHT_PURPLE;
    }

    static void info(String s) {
        MLDataCoreMCML.logger.info(s);
    }

    public MLDataWrap clone() {
        return new MLDataWrap(getShape().clone(), getData().clone());
    }

}