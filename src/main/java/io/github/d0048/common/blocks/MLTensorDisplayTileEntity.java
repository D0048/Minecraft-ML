package io.github.d0048.common.blocks;

import java.util.Arrays;
import java.util.HashMap;

import javax.swing.text.StyleConstants.CharacterConstants;

import io.github.d0048.MCML;
import io.github.d0048.common.MLDataWrap;
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
    BlockPos edgeLow = new BlockPos(0, 0, 0), edgeHigh = new BlockPos(0, 0, 0);
    HashMap<BlockPos, Integer> pos2IndexMap = new HashMap<BlockPos, Integer>();
    HashMap<Integer, BlockPos> index2PosMap = new HashMap<Integer, BlockPos>();
    int loop = 10, curr = 0;

    public MLTensorDisplayTileEntity() {
        super();
        info("New tile entity created");
    }

    static void info(String s) {
        MCML.logger.info(s);
    }

    public boolean setDataID(String dataID) {
        this.dataID = dataID;
        if ((dataWrap = MCML.mlDataCore.registerDataForID(dataID)) != null) {
            edgeLow = this.getPos().add(1, 0, 1);
            edgeHigh = this.getPos().add(1, 0, 1);
            displayShape = dataWrap.getShape().clone();
            Cleanup();
            solveDataWrap();
            return true;
        } else {
            info("DataID not found");
            return false;
        }
    }

    @Override
    public void update() {
        if (curr++ % loop == 0 && dataWrap != null) {
        }
    }

    void Cleanup() {
        for (int i = edgeLow.getX(); i <= edgeHigh.getX(); i++) {
            for (int j = edgeLow.getY(); j <= edgeHigh.getY(); j++) {
                for (int k = edgeLow.getZ(); k <= edgeHigh.getZ(); k++) {
                    getWorld().destroyBlock(new BlockPos(i, j, k), false);
                }
            }
        }
        pos2IndexMap.clear();
        index2PosMap.clear();
        edgeLow = new BlockPos(0, 0, 0);
        edgeHigh = new BlockPos(0, 0, 0);
    }


    void solveDataWrap() {
        if (dataWrap == null)
            return;
        if (getDisplayShape().length > 3)
            MCML.logger.warn("Displaying a tensor larger than 3D, only first 3 used");
        int[] shape = getDisplayShape(), data = dataWrap.getData();
        edgeHigh = edgeHigh.add(shape[0] + 1, shape[1] + 1, shape[2] + 1);
        info("Solving Data: " + Arrays.toString(data) + " with shape " + Arrays.toString(shape));

        for (int i = 0; i < shape[0] || i == 0; i++)
            for (int j = 0; j < shape[1] || j == 0; j++) {
                for (int k = 0; k < shape[2] || k == 0; k++) {
                    info(i + ", " + j + ", " + k);
                    BlockPos p = edgeHigh.add(-i - 1, -j - 1, -k - 1);
                    int index = (i * shape[0] + j) * shape[1] + k, val = data[index];
                    pos2IndexMap.put(p, index);
                    index2PosMap.put(index, p);
                    MLScalar.placeAt(getWorld(), p, val);
                }
            }
        MLScalar.placeAt(getWorld(), edgeLow, 15);// debug
        MLScalar.placeAt(getWorld(), edgeHigh, 15);// debug
        markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString("dataID", dataID);
        compound.setIntArray("edgeHigh", new int[]{edgeHigh.getX(), edgeHigh.getY(), edgeHigh.getZ()});
        compound.setIntArray("edgeLow", new int[]{edgeLow.getX(), edgeLow.getY(), edgeLow.getZ()});
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
            setDataID(compound.getString("edgeLow"));
        }

    }

    @Override
    public String toString() {
        String ret = TextFormatting.LIGHT_PURPLE + super.toString() + ":\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - DataID: " + TextFormatting.YELLOW + getDataID()
                + TextFormatting.LIGHT_PURPLE + "\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - DataWrap: " + dataWrap + "\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - Display Shape: " + TextFormatting.YELLOW
                + (dataWrap == null ? -1 : dataWrap.getData().length) + TextFormatting.LIGHT_PURPLE + " reshaped into "
                + TextFormatting.YELLOW + Arrays.toString(displayShape) + "\n";
        return ret;

    }

    @Override
    public void onLoad() {
        info("Display loaded at " + this.getPos());
    }

    public void toggleWritable() {// flip writable state
        getWorld().setBlockState(getPos(),
                MLTensorDisplay.mlTensorDisplay.getStateFromMeta(
                        MLTensorDisplay.mlTensorDisplay.getMetaFromState(getWorld().getBlockState(getPos())) == 0 ? 1 : 0));
    }

    public boolean isWritable() {
        return this.getBlockMetadata() == 1;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return false;
    }

    public int[] getDisplayShape() {
        return displayShape;
    }

    public void setDisplayShape(int[] displayShape) {
        this.displayShape = displayShape;
    }

    public String getDataID() {
        return dataID;
    }

}