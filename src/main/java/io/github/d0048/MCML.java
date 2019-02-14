package io.github.d0048;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

import org.apache.logging.log4j.Logger;
import org.tensorflow.TensorFlow;

import com.typesafe.config.Config;

import io.github.d0048.common.blocks.MLBlockBase;

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
	public static Configuration config;

	public static MLBlockBase mlBlockBase; // this holds the unique instance of your block
	public static ItemBlock mlBlockBaseItemBlock;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		config = new Configuration(event.getSuggestedConfigurationFile());
		// Common Init
		MCML.logger.error("--MCML Start Init---");
		ForgeRegistries.BLOCKS.register(MCML.mlBlockBase = new MLBlockBase());
		MCML.mlBlockBaseItemBlock = new ItemBlock(MCML.mlBlockBase);
		MCML.mlBlockBaseItemBlock.setRegistryName(MCML.mlBlockBase.getRegistryName());
		ForgeRegistries.ITEMS.register(MCML.mlBlockBaseItemBlock);
		
		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
		// just a small test
		MinecraftForge.EVENT_BUS.register(this);
		MCML.logger.error("--MCML End Init---");
	}

	@SubscribeEvent
	public void pickupItem(EntityItemPickupEvent event) {
		event.getEntityPlayer().attemptTeleport(event.getEntityPlayer().posX, event.getEntityPlayer().posY + 5,
				event.getEntityPlayer().posZ);
	}
}
