package io.github.d0048.common.blocks;

import io.github.d0048.MLConfig;
import io.github.d0048.util.SerializableBlockPos;
import io.github.d0048.util.Util;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class MLGraphAxisTileEntity extends MLTileEntityBase {
    ConcurrentHashMap<SerializableBlockPos, Double> pointsMap = new ConcurrentHashMap<>();

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        try {
            info(pointsMap.toString());
            info("Write Length: " + Util.serialize(pointsMap).length);
            compound.setByteArray("serialize_pointsMap", Util.serialize(pointsMap));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        try {
            info("Read Length: " + compound.getByteArray("serialize_pointsMap").length);
            pointsMap = (ConcurrentHashMap<SerializableBlockPos, Double>)
                    Util.deserialize(compound.getByteArray("serialize_pointsMap"));
            info(pointsMap.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int loop = 100;
    Random rand = new Random();

    @Override
    public void update() {
        if (loop-- < 0) {
            cleanUp();
            plotFunction((double t) -> new double[]{Math.sin(t / 5) * 20, Math.cos(t / 5) * 20, t}, 0, 100, 0.2);
            info("plot");
            loop = 100;
        }
    }

    public void cleanUp() {
        for (SerializableBlockPos p : pointsMap.keySet()) {
            world.destroyBlock(p.getPos(), false);
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }

    //------------------Plotting utilities
    public void plotFunction(Func3to1 f) {

    }

    public void plotFunction(Func1to3 f, double from, double to, double step_width) {
        BlockPos p0 = Util.arr2BlockPos(f.f(from));
        if (from > to) {
            double tmp = from;
            from = to;
            to = tmp;
        }
        do {
            BlockPos p1 = (from + step_width <= to) ?
                    Util.arr2BlockPos(f.f(from + step_width)) :
                    Util.arr2BlockPos(f.f(to));
            plotLineRelative(p0, p1, from % MLConfig.scalarResolution);
            p0 = p1;
        } while ((from += step_width) < to);
    }

    public void plotLineRelative(BlockPos x0, BlockPos x1, double value) {
        BlockPos[] xs = Util.sortEdges(x0, x1);
        plotLineRelative(x0.getX(), x0.getY(), x0.getZ(), x1.getX(), x1.getY(), x1.getZ(), value);
    }

    public void plotLineRelative(double x0, double y0, double z0, double x1, double y1, double z1, double value) {
        double[] rates = new double[]{Math.abs(x1 - x0), Math.abs(y1 - y0), Math.abs(z1 - z0)};
        double[] absDiff = rates.clone();
        double stepRatio = Util.arrMax(rates);
        for (int i = 0; i < rates.length; i++) {
            rates[i] = stepRatio == 0 ? 0 : rates[i] / stepRatio;
        }
        int maxLength = 500;
        double x = x0, y = y0, z = z0;
        double lastD = Integer.MAX_VALUE, d = lastD;
        int numStep = (int) Util.arrMax(Util.arrDivide(absDiff, rates));
        while (numStep-- > 0) {
            lastD = d;
            x += rates[0];
            y += rates[1];
            z += rates[2];
            plotPoint(x, y, z, value);
            if (maxLength-- < 0) break;
        }
    }

    public void plotPoint(double x, double y, double z, double value) {
        plotPoint(new double[]{x, y, z}, value);
    }

    public void plotPoint(double[] vector, double value) {
        double[][] basis = getIJK();
        BlockPos point = getCenter();
        for (double[] b : basis) {
            double[] vec = new double[3];
            System.arraycopy(vector, 0, vec, 0, Math.min(vector.length, vec.length));
            vec = Util.arrProduct(vec, b);
            point = point.add(vec[0], vec[1], vec[2]);
        }
        if (!point.equals(getCenter())) {
            MLScalar.placeAt(getWorld(), point, (int) value);
            pointsMap.put(new SerializableBlockPos(point), value);
            markDirty();
        }
    }

    public BlockPos getCenter() {
        return getPos() == null ? new BlockPos(0, 0, 0) : getPos();
    }

    public double[][] getIJK() {
        double[][] absoluteWorld = new double[][]{{-1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
        switch (EnumFacing.getFront(getBlockMetadata())) {
            case SOUTH:
                return new double[][]{{-1, 0, 0}, {0, 0, 1}, {0, 1, 0}};
            case WEST:
                return new double[][]{{0, 0, -1}, {-1, 0, 0}, {0, 1, 0}};
            case NORTH:
                return new double[][]{{1, 0, 0}, {0, 0, -1}, {0, 1, 0}};
            case EAST:
                return new double[][]{{0, 0, 1}, {1, 0, 0}, {0, 1, 0}};
        }
        return absoluteWorld;
    }

    interface Func3to1 {
        public double f(double x, double y, double z);
    }

    interface Func1to3 {
        public double[] f(double t);
    }
}

