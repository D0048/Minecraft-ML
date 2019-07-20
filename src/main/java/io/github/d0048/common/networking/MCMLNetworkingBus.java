package io.github.d0048.common.networking;

import io.github.d0048.MCML;
import io.github.d0048.common.blocks.MLTensorDisplay;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class MCMLNetworkingBus {
    private static int packetId = 0;
    private static SimpleNetworkWrapper INSTANCE = null;

    public static SimpleNetworkWrapper getWrapperInstance() {
        if (INSTANCE == null) {
            INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(MCML.MODID);
        }
        return INSTANCE;
    }

    public static void init() {
        registerMessageBothSide(MLTensorDisplaySyncMessage.Handler.class, MLTensorDisplaySyncMessage.class, packetId++);
    }

    public static <REQ extends IMessage, REPLY extends IMessage> void registerMessageBothSide(
            Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, int discriminator) {
        getWrapperInstance().registerMessage(messageHandler, requestMessageType, discriminator, Side.SERVER);
        getWrapperInstance().registerMessage(messageHandler, requestMessageType, discriminator, Side.CLIENT);
    }
}
