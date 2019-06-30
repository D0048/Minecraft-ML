package io.github.d0048.common.items;

import java.sql.SQLClientInfoException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import io.github.d0048.MCML;
import io.github.d0048.common.MLTab;
import io.github.d0048.common.blocks.MLTensorDisplay;
import io.github.d0048.common.blocks.MLTensorDisplayTileEntity;
import io.github.d0048.util.Util;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.dragon.phase.PhaseChargingPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import scala.swing.TextComponent;

public class MLWand extends MLItemBase {
	public static MLWand mlWand;
	HashMap<EntityPlayer, BlockPos> selectionMapLeft = new HashMap<EntityPlayer, BlockPos>();
	HashMap<EntityPlayer, BlockPos> selectionMapRight = new HashMap<EntityPlayer, BlockPos>();

	public MLWand(String regName, String dispName) {
		super(regName, dispName);
		setMaxStackSize(1);
	}

	public static void commonInit() {
		mlWand = new MLWand("ml_wand", "Wand");
	}

	public static void clientInit() {
		ModelLoader.setCustomModelResourceLocation(mlWand, 0,
				new ModelResourceLocation(MCML.MODID + ":ml_wand", "inventory"));
	}

	@SubscribeEvent
	public void BlockHarvested(BlockEvent.BreakEvent event) {
		Item i = event.getPlayer().inventory.getCurrentItem().getItem();
		if (i != null && i.equals(mlWand)) {
			if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
				event.getPlayer().sendMessage(
						new TextComponentString(TextFormatting.LIGHT_PURPLE + "Selection 1: " + event.getPos()));
				selectionMapLeft.put(event.getPlayer(), event.getPos());
			}
			event.setCanceled(true);
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			TileEntity te = worldIn.getTileEntity(pos);
			if (te != null) {
				if (te instanceof MLTensorDisplayTileEntity) {// TensorDisplay action
				}
			}
			// Selection action
			player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "Selection 2: " + pos));
		} else {
			Minecraft.getMinecraft().world.setBlockState(pos, Blocks.QUARTZ_BLOCK.getDefaultState());
		}
		selectionMapRight.put(player, pos);
		return EnumActionResult.SUCCESS;
	}

	int effectInterval = 15, currentInterval = 0;

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		// Tick only for specific player
		if (worldIn.isRemote && entityIn instanceof EntityPlayer && currentInterval++ == effectInterval) {
			currentInterval = 0;
			EntityPlayer player = (EntityPlayer) entityIn;
			BlockPos[] ss = getPlayerSelection(player);
			if (ss != null) {
				Util.surrendBlock(worldIn, EnumParticleTypes.REDSTONE, ss[0], 5);
				Util.surrendBlock(worldIn, EnumParticleTypes.REDSTONE, ss[1], 5);
			}
		}
	}

	public BlockPos[] getPlayerSelection(EntityPlayer player) {
		BlockPos s1 = selectionMapLeft.get(player), s2 = selectionMapRight.get(player);
		if (s1 != null && s2 != null && !s1.equals(s2)) {
			return Util.sortEdges(s1, s2);
		}
		return null;
	}

	static void info(String s) {
		MCML.logger.info(s);
	}
}
