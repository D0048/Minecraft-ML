package io.github.d0048.common.blocks;

import io.github.d0048.MCML;
import io.github.d0048.MLConfig;
import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.util.Util;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.Range;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MLTensorDisplayTileEntity extends MLTileEntityBase {
    String dataID = "";
    int[] displayShape = new int[]{1,1,1};
    BlockPos edgeLow = new BlockPos(0, 0, 0), edgeHigh = edgeLow;
    ConcurrentHashMap<BlockPos, Integer> pos2IndexMap = new ConcurrentHashMap<BlockPos, Integer>();
    ConcurrentHashMap<Integer, BlockPos> index2PosMap = new ConcurrentHashMap<Integer, BlockPos>();
    Range<Double> normalizationRange = Range.between(-1D, 1D);

    public MLTensorDisplayTileEntity() {
        super();
    }


    int loop = (int) (Math.random() * MLConfig.tensorDisplayRefreshInterval);

    @Override
    public void update() {
        if (loop-- < 0 && getDataWrap() != null) {
            Util.spawnLine(getWorld(), EnumParticleTypes.REDSTONE, getPos(), edgeLow,
                    (int) (Math.sqrt(getPos().distanceSq(edgeLow)) * 2),
                    0, 0, 1);
            Util.spawnLine(getWorld(), EnumParticleTypes.REDSTONE, getPos(), edgeHigh,
                    (int) (Math.sqrt(getPos().distanceSq(edgeHigh)) * 2),
                    0, 0, 1);
            loop = MLConfig.tensorDisplayRefreshInterval;
        }
        try {
            if (loop % 2 == 1) {
                if (isWritable())
                    readValues();
                else
                    writeValues();
            }
        } catch (Exception e) {
            MCML.logger.error("Failed to update value");
            MCML.logger.error(e);
        }
    }

    public MLDataWrap getDataWrap() {
        return MCML.mlDataCore.getDataForID(dataID);
    }

    public boolean setDataID(String dataID) {
        try {
            markDirty();
            Cleanup().unregisterID();
            this.dataID = dataID.trim();
            if ((MCML.mlDataCore.registerDataForID(dataID)) != null) {
                if (Util.arrCumProduct(displayShape) != Util.arrCumProduct(getDataWrap().getShape()))
                    setDisplayShape(getDataWrap().getShape().clone());
                solveDataWrap();
                return true;
            } else {
                throw new IllegalArgumentException("Data ID not found");
            }
        } catch (Exception e) {
            info("Error setting dataID of \"" + dataID + "\" due to " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean setDisplayShape(int[] displayShape) {
        int[] backup = this.displayShape;
        try {
            this.displayShape = displayShape;
            if (isDisplayShapeValid()) {
                reDraw();
                markDirty();
                return true;
            } else {
                throw new Exception("Invalid Display Shape");
            }
        } catch (Exception e) {
            this.displayShape = backup;
            e.printStackTrace();
            reDraw();
            return false;
        }
    }

    public MLTensorDisplayTileEntity normalize() {
        if (getDataWrap() != null) {
            Range r = Util.arrRange(getDataWrap().getData());
            if (!r.getMaximum().equals(r.getMinimum()))
                setNormalizationRange(r);
            else
                setNormalizationRange(Range.between(-1D, 1D));
        }
        return this;
    }

    public void setNormalizationRange(Range<Double> newRange) {
        info("Normalizing from " + this.normalizationRange + " into " + newRange);
        this.normalizationRange = newRange;
        writeValues();
    }

    public boolean reroot(BlockPos edgeLow) {
        BlockPos backup = this.edgeLow;
        try {
            Cleanup();
            this.edgeLow = edgeLow;
            solveDataWrap();
            markDirty();
            return true;
        } catch (Exception e) {
            this.edgeLow = backup;
            e.printStackTrace();
            return false;
        }
    }

    public MLTensorDisplayTileEntity hint() {
        Util.surroundArea(getWorld(), EnumParticleTypes.ENCHANTMENT_TABLE, edgeHigh, edgeLow, 55);
        return this;
    }

    public MLTensorDisplayTileEntity reDraw() {
        Cleanup().solveDataWrap();
        return this;
    }

    public void writeValues() {
        if (getWorld() == null || getDataWrap() == null) return;
        Set<Integer> indexs = index2PosMap.keySet();
        double[] values = getDataWrap().getData();
        double min = getNormalizationRange().getMinimum(), max = getNormalizationRange().getMaximum();

        for (int i : indexs) {
            double value = values[i];
            int normedValue = (int) ((Math.min(max, Math.max(min, value)) - min) / (max - min) * MLConfig.scalarResolution);
            MLScalar.placeAt(getWorld(), index2PosMap.get(i), normedValue);
        }
        MCML.mlDataCore.writeDataForID(getDataID());
    }

    public void readValues() {
        if (getWorld() == null || getDataWrap() == null) return;
        Set<BlockPos> indexs = pos2IndexMap.keySet();
        double[] values = getDataWrap().getData();
        double min = getNormalizationRange().getMinimum(), max = getNormalizationRange().getMaximum();

        for (BlockPos p : indexs) {
            double value = MLScalar.getValueAt(getWorld(), p);
            double deNormedValue = value / MLConfig.scalarResolution * (max - min) + min;
            values[pos2IndexMap.get(p)] = deNormedValue;
        }
    }

    MLTensorDisplayTileEntity Cleanup() {
        if (getWorld() != null)
            for (int i = edgeLow.getX(); i <= edgeHigh.getX() - 1; i++) {
                for (int j = edgeLow.getY(); j <= edgeHigh.getY() - 1; j++) {
                    for (int k = edgeLow.getZ(); k <= edgeHigh.getZ() - 1; k++) {
                        getWorld().destroyBlock(new BlockPos(i, j, k), false);
                    }
                }
            }
        pos2IndexMap.clear();
        index2PosMap.clear();
        //if (isDisplayShapeValid())
        edgeHigh = edgeLow.add(getDisplayShape()[0], getDisplayShape()[1], getDisplayShape()[2]);
        //else {
        //    edgeLow = edgeHigh = this.getPos().add(1, 0, 1);
        //}
        return this;
    }

    MLTensorDisplayTileEntity unregisterID() {
        MCML.mlDataCore.unregisterID(getDataID());
        return this;
    }

    MLTensorDisplayTileEntity solveDataWrap() {
        if (getDataWrap() == null || getWorld() == null) return this;

        int[] shape = getDisplayShape();
        double[] data = getDataWrap().getData();
        if (!isDisplayShapeValid()) {
            MCML.logger.warn("Display shape too large, use original instead!");
            shape = getDataWrap().getShape();
            while (shape.length < 3) { //pad to 3D
                shape = Arrays.copyOf(shape, shape.length + 1);
                shape[shape.length - 1] = 1;
            }
        }
        if (shape.length > 3)
            MCML.logger.warn("Displaying a tensor larger than 3D, only first 3 used!");

        edgeHigh = edgeLow.add(shape[0], shape[1], shape[2]);
        //info("Solving Data: " + Arrays.toString(data) + " with shape " + Arrays.toString(shape));
        info("Solving Data: [" + getDataID() + "] with shape " + Arrays.toString(shape));
        final int[] shapeFinal = shape.clone();
        new Thread(() -> {
            info("Staring a new thread to solve datawrap");
            for (int i = 0; i < shapeFinal[0] || i == 0; i++)
                for (int j = 0; j < shapeFinal[1] || j == 0; j++) {
                    for (int k = 0; k < shapeFinal[2] || k == 0; k++) {
                        BlockPos p = edgeHigh.add(-i - 1, -j - 1, -k - 1);
                        int index = getDataWrap().extIndex2InternalIndex(new int[]{i, j, k});
                        pos2IndexMap.put(p, index);
                        index2PosMap.put(index, p);
                        //MLScalar.placeAt(getWorld(), p, val);
                    }
                }
            info("Datawrap sovled.");
        }).start();
        writeValues();
        return this;
    }

    boolean isDisplayShapeValid() {
        if (getDataWrap() != null)
            return Util.arrCumProduct(getDisplayShape()) <= Util.arrCumProduct(getDataWrap().getShape());
        else return false;
    }

    @Override
    public String toString() {
        String ret = TextFormatting.LIGHT_PURPLE + super.toString() + ":\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - Mode: " + TextFormatting.YELLOW + (isWritable() ? "Read/Write" : "Read Only")
                + TextFormatting.LIGHT_PURPLE + "\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - DataID: " + TextFormatting.YELLOW + getDataID()
                + TextFormatting.LIGHT_PURPLE + "\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - DataWrap: " + getDataWrap() + "\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - Display Shape: " + TextFormatting.YELLOW
                + (getDataWrap() == null ? -1 : getDataWrap().getData().length) + TextFormatting.LIGHT_PURPLE + " reshaped into "
                + TextFormatting.YELLOW + Arrays.toString(displayShape) + " | " + getNormalizationRange() +
                TextFormatting.LIGHT_PURPLE + " which is " + TextFormatting.YELLOW + (isDisplayShapeValid() ? "valid" : "invalid") +
                TextFormatting.YELLOW + "\n";
        return ret;

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString("dataID", dataID);
        compound.setIntArray("edgeHigh", new int[]{edgeHigh.getX(), edgeHigh.getY(), edgeHigh.getZ()});
        compound.setIntArray("edgeLow", new int[]{edgeLow.getX(), edgeLow.getY(), edgeLow.getZ()});
        compound.setIntArray("displayShape", displayShape);
        compound.setDouble("rangeMax", getNormalizationRange().getMaximum());
        compound.setDouble("rangeMin", getNormalizationRange().getMinimum());
        markDirty();
        return compound;

    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("edgeHigh"))
            edgeHigh = new BlockPos(compound.getIntArray("edgeHigh")[0], compound.getIntArray("edgeHigh")[1],
                    compound.getIntArray("edgeHigh")[2]);
        if (compound.hasKey("edgeLow"))
            edgeLow = new BlockPos(compound.getIntArray("edgeLow")[0], compound.getIntArray("edgeLow")[1],
                    compound.getIntArray("edgeLow")[2]);
        if (compound.hasKey("displayShape")) {
            setDisplayShape(compound.getIntArray("displayShape"));
        }
        if (compound.hasKey("dataID")) {
            try {
                setDataID(compound.getString("dataID"));
            } catch (Exception e) {
                info("Malformed DataID: " + compound.getString("dataID"));
                MCML.logger.error(e);
            }
        }
        double max = 1, min = -1;
        if (compound.hasKey("rangeMax")) {
            max = (compound.getDouble("rangeMax"));
        }
        if (compound.hasKey("rangeMin")) {
            min = (compound.getDouble("rangeMin"));
        }
        setNormalizationRange(Range.between(min, max));
    }

    public MLTensorDisplayTileEntity setWritable(boolean b) {
        if (isWritable() != b) toggleWritable();
        return this;
    }

    public MLTensorDisplayTileEntity toggleWritable() {// flip writable state
        getWorld().setBlockState(getPos(),
                MLTensorDisplay.mlTensorDisplay.getStateFromMeta(
                        MLTensorDisplay.mlTensorDisplay.getMetaFromState(getWorld().getBlockState(getPos())) == 0 ? 1 : 0));
        markDirty();
        return this;
    }

    public boolean isWritable() {
        return this.getBlockMetadata() == 1;
    }

    public Range<Double> getNormalizationRange() {
        return normalizationRange;
    }

    public int[] getDisplayShape() {
        int[] shape = Util.arrCumProduct(displayShape) != 0 ? displayShape : getDataWrap().getShape();
        while (shape.length < 3) { //pad to 3D
            shape = Arrays.copyOf(shape, shape.length + 1);
            shape[shape.length - 1] = 1;
        }
        return shape;
    }

    public String getDataID() {
        return dataID;
    }
}