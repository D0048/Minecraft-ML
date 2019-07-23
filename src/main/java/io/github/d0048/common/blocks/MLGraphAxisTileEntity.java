package io.github.d0048.common.blocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class MLGraphAxisTileEntity extends MLTileEntityBase {

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

    @Override
    public void update() {
    }

    @Override
    public String toString() {
        return super.toString();
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
    }
}

