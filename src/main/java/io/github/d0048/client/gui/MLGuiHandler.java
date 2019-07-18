package io.github.d0048.client.gui;

import io.github.d0048.common.blocks.MLTensorDisplayTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class MLGuiHandler implements IGuiHandler {
    public static MLGuiHandler INSTANCE = new MLGuiHandler();

    public MLGuiHandler() {
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case 0:
                return new MLTensorDisplayGui(new BlockPos(x, y, z));
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case 0:
                return new MLTensorDisplayGui(new BlockPos(x, y, z));
            default:
                return null;
        }
    }

}
