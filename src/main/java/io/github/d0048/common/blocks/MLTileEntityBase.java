package io.github.d0048.common.blocks;

import io.github.d0048.MCML;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class MLTileEntityBase extends TileEntity implements ITickable {
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return false;
    }

    @Override
    public void onLoad() {

    }

    static void info(String s) {
        MCML.logger.info(s);
    }
}
