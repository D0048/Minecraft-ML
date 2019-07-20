package io.github.d0048.common.networking;

import io.github.d0048.MCML;
import io.github.d0048.client.gui.MLTensorDisplayGui;
import io.github.d0048.common.blocks.MLTensorDisplayTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MLTensorDisplaySyncMessage implements IMessage {
    public NBTTagCompound nbt;

    public MLTensorDisplaySyncMessage() {
    }

    public MLTensorDisplaySyncMessage(NBTTagCompound nbt) {
        this.nbt = nbt;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        nbt = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, nbt);
    }

    public static class Handler implements IMessageHandler<MLTensorDisplaySyncMessage, IMessage> {

        @Override
        public IMessage onMessage(MLTensorDisplaySyncMessage message, MessageContext ctx) {
            System.out.println("nbt = " + message.nbt);
            if (ctx.side == Side.SERVER) {
                System.out.println("server got message");
                EntityPlayerMP player = ctx.getServerHandler().player;
                World world = player.getEntityWorld();
                player.getServerWorld().addScheduledTask(() -> {
                    int[] pos = message.nbt.getIntArray("myPosition");
                    try {
                        (world.getTileEntity(new BlockPos(pos[0], pos[1], pos[2]))).readFromNBT(message.nbt);
                    } catch (Exception e) {
                        MCML.logger.error("Could not sync display from client provided tag:");
                        MCML.logger.error(e);
                    }
                    //TODO: reply
                });
            } else {
                System.out.println("client got message");
                MLTensorDisplayGui.currentNBT = message.nbt;
            }
            return null;
        }
    }

}
