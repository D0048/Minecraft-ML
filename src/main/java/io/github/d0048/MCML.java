package io.github.d0048;

import io.github.d0048.common.RunToCommand;
import io.github.d0048.common.blocks.MLColorConverter;
import io.github.d0048.databackend.datacore_mcml.MLDataCoreMCML;
import io.github.d0048.databackend.datacore_tf.MLDataCoreTF;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import org.apache.logging.log4j.Logger;

import io.github.d0048.common.MLWandCommand;
import io.github.d0048.databackend.MLDataCore;
import io.github.d0048.common.blocks.MLBlockBase;
import io.github.d0048.common.blocks.MLScalar;
import io.github.d0048.common.blocks.MLTensorDisplay;
import io.github.d0048.client.gui.MLGuiHandler;
import io.github.d0048.common.items.MLWand;

@Mod(modid = MCML.MODID, name = MCML.NAME, version = MCML.VERSION)
public class MCML {
    public static final String MODID = "minecraft_ml";
    public static final String NAME = "Minecraft-ML";
    public static final String VERSION = "0.1a";

    public static Logger logger;
    @Mod.Instance(MCML.MODID)
    public static MCML instance;
    @SidedProxy(clientSide = "io.github.d0048.client.ClientProxy", serverSide = "io.github.d0048.ServerProxy")
    public static IProxy proxy;

    public static MLDataCore mlDataCore;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        ConfigManager.sync(MCML.MODID, Config.Type.INSTANCE);
        // Common Init
        MCML.logger.info("--MCML Start Init---");
        // TODO: Add new
        switch (MLConfig.backendType) {
            case TF:
                mlDataCore = new MLDataCoreTF();
                break;
            default:
            case MCML:
                mlDataCore = new MLDataCoreMCML();
                break;
        }
        MLBlockBase.commonInit();
        MLScalar.commonInit();
        MLTensorDisplay.commonInit();
        MLWand.commonInit();
        MLColorConverter.commonInit();
        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(MLWand.mlWand);
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, MLGuiHandler.INSTANCE);
        MCML.logger.info("--MCML End Init---");
    }

    @EventHandler
    public void start(FMLServerStartingEvent event) {
        event.registerServerCommand(MLWandCommand.commandStylus = new MLWandCommand());
        event.registerServerCommand(RunToCommand.runToCommand = new RunToCommand());
    }
}
