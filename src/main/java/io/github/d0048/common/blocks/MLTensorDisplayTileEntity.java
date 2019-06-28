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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MLTensorDisplayTileEntity extends TileEntity implements ITickable {
	String dataID = "";
	MLDataWrap dataWrap;
	BlockPos edgeLow = new BlockPos(0, 0, 0), edgeHigh = new BlockPos(0, 0, 0);
	HashMap<BlockPos, Integer> pos2IndexMap = new HashMap<BlockPos, Integer>();
	HashMap<Integer, BlockPos> index2PosMap = new HashMap<Integer, BlockPos>();

	public MLTensorDisplayTileEntity() {
		super();
		info("New tile entity created");
	}

	public void setDataID(String dataID) {
		this.dataID = dataID;
		dataWrap = MCML.mlDataCore.registerDataForID(dataID);
		Cleanup();
		solveDataWrap();
	}

	int loop = 10, curr = 0;

	@Override
	public void update() {
		if (curr++ % loop == 0 && dataWrap != null) {
			// info("Tensor updated at " + this.getPos() + " with state "
			// +getBlockMetadata());
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
		if (dataWrap.getShape().length > 3)
			MCML.logger.warn("Displaying a tensor larger than 3D, only first 3 used");
		edgeLow = this.getPos().add(1, 0, 1);
		edgeHigh = this.getPos().add(1, 0, 1);
		int[] shape = dataWrap.getShape(), data = dataWrap.getData();
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
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
	}

	@Override
	public void onLoad() {
		info("Display loaded at " + this.getPos());
	}

	public void toggleWritable() {
		getWorld().setBlockState(getPos(), MLTensorDisplay.mlTensorDisplay.getStateFromMeta(// flip writable state
				MLTensorDisplay.mlTensorDisplay.getMetaFromState(getWorld().getBlockState(getPos())) == 0 ? 1 : 0));
	}

	public boolean isWritable() {
		return this.getBlockMetadata() == 1;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return false;
	}

	public String getDataID() {
		return dataID;
	}

	static void info(String s) {
		MCML.logger.info(s);
	}

}