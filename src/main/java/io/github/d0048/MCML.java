package io.github.d0048;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.ChunkDataEvent.Load;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;
import org.tensorflow.TensorFlow;

import io.github.d0048.common.CommandStylus;
import io.github.d0048.common.blocks.MLBlockBase;
import io.github.d0048.common.blocks.MLScalar;
import io.github.d0048.common.blocks.MLTensorDisplay;
import io.github.d0048.common.items.MLItemBase;
import io.github.d0048.common.items.MLWand;

@Mod(modid = MCML.MODID, name = MCML.NAME, version = MCML.VERSION)
public class MCML {
	public static final String MODID = "minecraft_ml";
	public static final String NAME = "Minecraft-ML";
	public static final String VERSION = "0.1a";

	public static int scalarResolution = 16;

	public static Logger logger;
	@Mod.Instance(MCML.MODID)
	public static MCML instance;
	@SidedProxy(clientSide = "io.github.d0048.client.ClientProxy", serverSide = "io.github.d0048.ServerProxy")
	public static IProxy proxy;
	public static Configuration config;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		config = new Configuration(event.getSuggestedConfigurationFile());
		// Common Init
		MCML.logger.info("--MCML Start Init---");
		// MCML.logger.info("Tensorflow Version: " + TensorFlow.version());
		// TODO: Add new
		MLBlockBase.commonInit();
		MLScalar.commonInit();
		MLTensorDisplay.commonInit();
		MLWand.commonInit();
		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
		MinecraftForge.EVENT_BUS.register(this);
		MCML.logger.info("--MCML End Init---");
	}

	@EventHandler
	public void start(FMLServerStartingEvent event) {
		event.registerServerCommand(CommandStylus.commandStylus = new CommandStylus());
	}
}
