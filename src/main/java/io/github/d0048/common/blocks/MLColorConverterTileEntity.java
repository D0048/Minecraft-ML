package io.github.d0048.common.blocks;

import io.github.d0048.MLConfig;
import io.github.d0048.util.ColorUtil;
import io.github.d0048.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.List;

public class MLColorConverterTileEntity extends MLTileEntityBase {
    BlockPos edgeLow = new BlockPos(0, 0, 0), edgeHigh = edgeLow;


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
        BlockPos pos = getPos();
        World world = getWorld();
        try {
            int r = MLScalar.mlScalar.getMetaFromState(getWorld().getBlockState(pos.add(0, -1, 0)));
            int g = MLScalar.mlScalar.getMetaFromState(getWorld().getBlockState(pos.add(0, -2, 0)));
            int b = MLScalar.mlScalar.getMetaFromState(getWorld().getBlockState(pos.add(0, -3, 0)));
            String color=Util.rgb2Hex(new int[]{r, g, b});
            info(color);
            ItemStack stack = ColorUtil.getBlockFromColor(color).first();
            ItemBlock ib = (ItemBlock) stack.getItem();
            IBlockState state =
                    ib.getBlock().getStateForPlacement(world, pos, EnumFacing.DOWN, .5f, .5f, .5f, stack.getMetadata(), null);
            world.setBlockState(pos.add(0, 1, 0), state);
            info(state+"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
