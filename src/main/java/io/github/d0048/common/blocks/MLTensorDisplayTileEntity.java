package io.github.d0048.common.blocks;

import java.util.Arrays;
import java.util.HashMap;

import javax.swing.text.StyleConstants.CharacterConstants;

import io.github.d0048.MCML;
import io.github.d0048.common.MLDataWrap;
import io.github.d0048.util.Util;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class MLTensorDisplayTileEntity extends TileEntity implements ITickable {
    String dataID = "";
    MLDataWrap dataWrap;
    int[] displayShape = new int[3];
    BlockPos edgeLow = edgeHigh = new BlockPos(0, 0, 0), edgeHigh = new BlockPos(0, 0, 0);
    HashMap<BlockPos, Integer> pos2IndexMap = new HashMap<BlockPos, Integer>();
    HashMap<Integer, BlockPos> index2PosMap = new HashMap<Integer, BlockPos>();
    int loop = 10, curr = 0;

    public MLTensorDisplayTileEntity() {
        super();
        info("New tile entity created");
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
        int[] bk = this.displayShape;
        this.displayShape = displayShape;
        if (isDisplayShapeValid()) return true;
        else {
            this.displayShape = bk;
            return false;
        }
    }

    public MLTensorDisplayTileEntity relocate(BlockPos edgeLow) {
        Cleanup();
        this.edgeLow = edgeLow;
        solveDataWrap();
        return this;
    }

    @Override
    public void update() {
        if (curr++ % loop == 0 && dataWrap != null) {
            writeValues();
        }
    }


    public MLTensorDisplayTileEntity reDraw() {
        Cleanup().solveDataWrap();
        return this;
    }

    public void writeValues() {
        //TODO
    }

    MLTensorDisplayTileEntity Cleanup() {
        for (int i = edgeLow.getX(); i <= edgeHigh.getX(); i++) {
            for (int j = edgeLow.getY(); j <= edgeHigh.getY(); j++) {
                for (int k = edgeLow.getZ(); k <= edgeHigh.getZ(); k++) {
                    getWorld().destroyBlock(new BlockPos(i, j, k), false);
                }
            }
        }
        pos2IndexMap.clear();
        index2PosMap.clear();
        edgeLow = edgeHigh = this.getPos().add(1, 0, 1);
        return this;
    }

    MLTensorDisplayTileEntity solveDataWrap() {
        if (dataWrap == null) return this;

        int[] shape = getDisplayShape(), data = dataWrap.getData();
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
                    //info(i + ", " + j + ", " + k);
                    BlockPos p = edgeHigh.add(-i - 1, -j - 1, -k - 1);
                    int index = (i * shape[0] + j) * shape[1] + k, val = data[index];
                    pos2IndexMap.put(p, index);
                    index2PosMap.put(index, p);
                    MLScalar.placeAt(getWorld(), p, val);
                }
            }
        //MLScalar.placeAt(getWorld(), edgeLow, 15);// debug
        //MLScalar.placeAt(getWorld(), edgeHigh, 15);// debug
        return this;
    }

    boolean isDisplayShapeValid() {
        if (dataWrap != null)
            return Util.arrCumProduct(getDisplayShape()) < dataWrap.getData().length;
        else return false;
    }

    @Override
    public String toString() {
        String ret = TextFormatting.LIGHT_PURPLE + super.toString() + ":\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - DataID: " + TextFormatting.YELLOW + getDataID()
                + TextFormatting.LIGHT_PURPLE + "\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - DataWrap: " + dataWrap + "\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - Display Shape: " + TextFormatting.YELLOW
                + (dataWrap == null ? -1 : dataWrap.getData().length) + TextFormatting.LIGHT_PURPLE + " reshaped into "
                + TextFormatting.YELLOW + Arrays.toString(displayShape) + TextFormatting.LIGHT_PURPLE + " which is " +
                TextFormatting.YELLOW + (isDisplayShapeValid() ? "valid" : "invalid") + TextFormatting.YELLOW + "\n";
        return ret;

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString("dataID", dataID);
        compound.setIntArray("edgeHigh", new int[]{edgeHigh.getX(), edgeHigh.getY(), edgeHigh.getZ()});
        compound.setIntArray("edgeLow", new int[]{edgeLow.getX(), edgeLow.getY(), edgeLow.getZ()});
        compound.setIntArray("displayShape", displayShape);
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