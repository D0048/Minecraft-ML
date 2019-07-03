package io.github.d0048.common.blocks;

import java.util.*;

import javax.swing.text.StyleConstants.CharacterConstants;

import io.github.d0048.MCML;
import io.github.d0048.common.MLDataWrap;
import io.github.d0048.util.Util;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Range;

public class MLTensorDisplayTileEntity extends TileEntity implements ITickable {
    String dataID = "";
    MLDataWrap dataWrap;
    int[] displayShape = new int[3];
    BlockPos edgeLow = edgeHigh = new BlockPos(0, 0, 0), edgeHigh = new BlockPos(0, 0, 0);
    HashMap<BlockPos, Integer> pos2IndexMap = new HashMap<BlockPos, Integer>();
    HashMap<Integer, BlockPos> index2PosMap = new HashMap<Integer, BlockPos>();
    Range<Double> normalizationRange = Range.between(-1D, 1D);

    public MLTensorDisplayTileEntity() {
        super();
    }


    public boolean setDataID(String dataID) {
        markDirty();
        this.dataID = dataID;
        if ((dataWrap = MCML.mlDataCore.registerDataForID(dataID)) != null) {
            displayShape = dataWrap.getShape().clone();
            return true;
        } else {
            info("DataID not found");
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
        if (dataWrap != null) {
            setNormalizationRange(Util.arrRange(dataWrap.getData()));
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

    int loop = 10, curr = 0;

    @Override
    public void update() {
        if (curr++ >= loop && dataWrap != null) {
            if (isWritable())
                readValues();
            else
                writeValues();
            Util.spawnLine(getWorld(), EnumParticleTypes.REDSTONE, getPos(), edgeLow,
                    (int) (Math.sqrt(getPos().distanceSq(edgeLow)) * 2),
                    0, 0, 1);
            Util.spawnLine(getWorld(), EnumParticleTypes.REDSTONE, getPos(), edgeHigh,
                    (int) (Math.sqrt(getPos().distanceSq(edgeHigh)) * 2),
                    0, 0, 1);
            curr = 0;
        }
    }

    public MLTensorDisplayTileEntity hint() {
        Util.surroundArea(getWorld(), EnumParticleTypes.ENCHANTMENT_TABLE, edgeHigh, edgeLow, 55);
        return this;
    }

    public MLTensorDisplayTileEntity reDraw() {
        Cleanup().normalize().solveDataWrap();
        return this;
    }

    public void writeValues() {
        if (getWorld() == null || dataWrap == null) return;
        Set<Integer> indexs = index2PosMap.keySet();
        double[] values = dataWrap.getData();
        double min = getNormalizationRange().getMinimum(), max = getNormalizationRange().getMaximum();

        for (int i : indexs) {
            double value = values[i];
            int normedValue = (int) ((Math.min(max, Math.max(min, value)) - min) / (max - min) * MCML.scalarResolution);
            MLScalar.placeAt(getWorld(), index2PosMap.get(i), normedValue);
        }
    }

    public void readValues() {
        if (getWorld() == null || dataWrap == null) return;
        Set<BlockPos> indexs = pos2IndexMap.keySet();
        double[] values = dataWrap.getData();
        double min = getNormalizationRange().getMinimum(), max = getNormalizationRange().getMaximum();

        for (BlockPos p : indexs) {
            double value = MLScalar.valueAt(getWorld(), p);
            double deNormedValue = value / MCML.scalarResolution * (max - min) + min;
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
        if (isDisplayShapeValid())
            edgeHigh = edgeLow.add(getDisplayShape()[0], getDisplayShape()[1], getDisplayShape()[2]);
        else {
            edgeLow = edgeHigh = this.getPos().add(1, 0, 1);
        }
        return this;
    }

    MLTensorDisplayTileEntity solveDataWrap() {
        if (dataWrap == null || getWorld() == null) return this;

        int[] shape = getDisplayShape();
        double[] data = dataWrap.getData();
        if (!isDisplayShapeValid()) {
            MCML.logger.warn("Display shape too large, use original instead!");
            shape = dataWrap.getShape();
        }
        if (shape.length > 3)
            MCML.logger.warn("Displaying a tensor larger than 3D, only first 3 used!");

        edgeHigh = edgeLow.add(shape[0], shape[1], shape[2]);
        info("Solving Data: " + Arrays.toString(data) + " with shape " + Arrays.toString(shape));

        for (int i = 0; i < shape[0] || i == 0; i++)
            for (int j = 0; j < shape[1] || j == 0; j++) {
                for (int k = 0; k < shape[2] || k == 0; k++) {
                    BlockPos p = edgeHigh.add(-i - 1, -j - 1, -k - 1);
                    int index = i * shape[1] * shape[2] + shape[2] * j + k;// val = data[index];
                    pos2IndexMap.put(p, index);
                    index2PosMap.put(index, p);
                    //MLScalar.placeAt(getWorld(), p, val);
                }
            }
        writeValues();
        //MLScalar.placeAt(getWorld(), edgeLow, 15);// debug
        //MLScalar.placeAt(getWorld(), edgeHigh, 15);// debug
        return this;
    }

    boolean isDisplayShapeValid() {
        if (dataWrap != null)
            return Util.arrCumProduct(getDisplayShape()) <= dataWrap.getData().length;
        else return false;
    }

    @Override
    public String toString() {
        String ret = TextFormatting.LIGHT_PURPLE + super.toString() + ":\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - Mode: " + TextFormatting.YELLOW + (isWritable() ? "Read/Write" : "Read Only")
                + TextFormatting.LIGHT_PURPLE + "\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - DataID: " + TextFormatting.YELLOW + getDataID()
                + TextFormatting.LIGHT_PURPLE + "\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - DataWrap: " + dataWrap + "\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - Display Shape: " + TextFormatting.YELLOW
                + (dataWrap == null ? -1 : dataWrap.getData().length) + TextFormatting.LIGHT_PURPLE + " reshaped into "
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
        if (compound.hasKey("dataID")) {
            setDataID(compound.getString("dataID"));
        }
        if (compound.hasKey("displayShape")) {
            setDisplayShape(compound.getIntArray("displayShape"));
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

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return false;
    }

    public Range<Double> getNormalizationRange() {
        return normalizationRange;
    }

    @Override
    public void onLoad() {
        info("Display loaded at " + this.getPos());
    }

    public int[] getDisplayShape() {
        return displayShape;
    }

    static void info(String s) {
        MCML.logger.info(s);
    }


    public String getDataID() {
        return dataID;
    }
}