package io.github.d0048;

import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.logging.log4j.Logger;

@Mod(modid = MCML.MODID, name = MCML.NAME, version = MCML.VERSION)
public class MCML {
	public static final String MODID = "minecraft-ml";
	public static final String NAME = "Minecraft-ML";
	public static final String VERSION = "0.1";

	private static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		event.getSuggestedConfigurationFile();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void pickupItem(EntityItemPickupEvent event) {
		event.getEntityPlayer().attemptTeleport(event.getEntityPlayer().posX, event.getEntityPlayer().posY, event.getEntityPlayer().posZ+5);
	}
}
