package io.github.d0048.common.blocks;

import io.github.d0048.MLConfig;
import io.github.d0048.util.ColorUtil;
import io.github.d0048.util.Util;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.awt.*;

public class MLColorConverterTileEntity extends MLTileEntityBase {
    BlockPos edgeLow = new BlockPos(0, 0, 0), edgeHigh = edgeLow;

    public static enum ColorMode {
        GLASS, ANY//,WOOL
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

    int loop = MLConfig.colorConverterRefershInterval > 0 ? (int) (Math.random() * MLConfig.colorConverterRefershInterval) :
            MLConfig.colorConverterRefershInterval;

    @Override
    public void update() {
        if (loop < 0) return;
        if (loop-- < 0) {
            try {
                refresh();
            } catch (Exception e) {
            }
            loop = MLConfig.colorConverterRefershInterval;
        }
    }

    public void refresh() throws Exception {
        //info("Converter refreshed");
        BlockPos pos = getPos();
        World world = getWorld();
        int r =
                (int) (MLScalar.getValueAt(getWorld(), pos.add(0, -1, 0)) * (256D / MLConfig.scalarResolution));
        int g =
                (int) (MLScalar.getValueAt(getWorld(), pos.add(0, -2, 0)) * (256D / MLConfig.scalarResolution));
        int b =
                (int) (MLScalar.getValueAt(getWorld(), pos.add(0, -3, 0)) * (256D / MLConfig.scalarResolution));
        IBlockState state;
        if (MLConfig.ConverterColorMode == ColorMode.ANY) {
            String color = Util.rgb2Hex(new int[]{r, g, b});
            ItemStack stack = ColorUtil.getBlockFromColor(color).first();
            ItemBlock ib = (ItemBlock) stack.getItem();
            state = ib.getBlock().getStateForPlacement(world, pos, EnumFacing.DOWN, .5f, .5f, .5f, stack.getMetadata(), null);
        } else {
            state = ColorUtil.getGlassFromColor(new Color(r, g, b));
        }
        world.setBlockState(pos.add(0, 1, 0), Blocks.WOOL.getStateFromMeta(15));// Black
        world.setBlockState(pos.add(0, 2, 0), state);
    }

    @Override
    public String toString() {
        return TextFormatting.LIGHT_PURPLE + "Color Converter updating " + TextFormatting.YELLOW + "[On Demand]" +
                TextFormatting.LIGHT_PURPLE + ", using " + TextFormatting.YELLOW + MLConfig.ConverterColorMode;
    }
}
