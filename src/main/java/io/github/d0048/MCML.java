package io.github.d0048;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Chat;
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

import org.apache.logging.log4j.Logger;
import org.tensorflow.TensorFlow;

import com.typesafe.config.Config;

import io.github.d0048.common.blocks.MLBlockBase;


@Mod(modid = MCML.MODID, name = MCML.NAME, version = MCML.VERSION)
public class MCML {
	public static final String MODID = "minecraft-ml";
	public static final String NAME = "Minecraft-ML";
	public static final String VERSION = "0.1a";

	private static Logger logger;
	@Mod.Instance(MCML.MODID)
	public static MCML instance;
	@SidedProxy(clientSide = "io.github.d0048.ClientProxy", serverSide = "io.github.d0048.ServerProxy")
	public static ServerProxy proxy;
	public static Configuration config;

	public static MLBlockBase mlBlockBase; // this holds the unique instance of your block
	public static ItemBlock mlBlockBaseItemBlock;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		config = new Configuration(event.getSuggestedConfigurationFile());
		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void pickupItem(EntityItemPickupEvent event) {
		event.getEntityPlayer().attemptTeleport(event.getEntityPlayer().posX, event.getEntityPlayer().posY,
				event.getEntityPlayer().posZ + 5);
	}
}
