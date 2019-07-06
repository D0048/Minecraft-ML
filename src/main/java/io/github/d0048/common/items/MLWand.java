package io.github.d0048.common.items;

import java.sql.SQLClientInfoException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import io.github.d0048.MCML;
import io.github.d0048.MLConfig;
import io.github.d0048.common.MLBrush;
import io.github.d0048.common.MLTab;
import io.github.d0048.common.blocks.*;
import io.github.d0048.util.Util;
import net.minecraft.block.state.BlockStateBase;
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
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.b3d.B3DModel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import scala.swing.TextComponent;

public class MLWand extends MLItemBase {
    public static MLWand mlWand;
    HashMap<EntityPlayer, BlockPos> selectionMapLeft = new HashMap<EntityPlayer, BlockPos>();
    HashMap<EntityPlayer, BlockPos> selectionMapRight = new HashMap<EntityPlayer, BlockPos>();
    HashMap<EntityPlayer, MLTensorDisplayTileEntity> selectionMapDisplay = new HashMap<EntityPlayer, MLTensorDisplayTileEntity>();
    HashMap<EntityPlayer, MLBrush> player2BrushMap = new HashMap<EntityPlayer, MLBrush>();
    MLBrush brush=new MLBrush();

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


    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBlockHarvested(BlockEvent.BreakEvent e) {
        Item i = e.getPlayer().inventory.getCurrentItem().getItem();
        World world = e.getWorld();
        if (i != null && i.equals(mlWand)) {
            if (!world.isRemote) {
                e.getPlayer().sendMessage(
                        new TextComponentString(TextFormatting.LIGHT_PURPLE + "Selection 1: " + e.getPos()));
            } else {
                Minecraft.getMinecraft().world.setBlockState(e.getPos(), Blocks.QUARTZ_BLOCK.getDefaultState());
            }
            selectionMapLeft.put(e.getPlayer(), e.getPos());

            TileEntity te = world.getTileEntity(e.getPos());
            if (te instanceof MLTensorDisplayTileEntity) {
                selectionMapDisplay.put(e.getPlayer(), ((MLTensorDisplayTileEntity) te).hint());
                e.getPlayer().sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "Selected Display:"));
                e.getPlayer().sendMessage(new TextComponentString(te + ""));
            }
            e.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRightClick(PlayerInteractEvent.RightClickBlock e) {
        Item i = e.getEntityPlayer().inventory.getCurrentItem().getItem();
        World world = e.getWorld();
        if (i != null && i.equals(mlWand)) {
            if (!world.isRemote && e.getHand() == EnumHand.MAIN_HAND) {
                // TileEntity te = world.getTileEntity(e.getPos());
                e.getEntityPlayer().sendMessage(
                        new TextComponentString(TextFormatting.LIGHT_PURPLE + "Selection 2: " + e.getPos()));
            } else {
                Minecraft.getMinecraft().world.setBlockState(e.getPos(), Blocks.QUARTZ_BLOCK.getDefaultState());
            }
            selectionMapRight.put(e.getEntityPlayer(), e.getPos());
            e.setCanceled(true);
        }
    }

    int effectInterval = 15, currentInterval = 0;

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        // Tick only for specific player
        if (worldIn.isRemote && entityIn instanceof EntityPlayer && currentInterval++ == effectInterval) {
            currentInterval = 0;
            EntityPlayer player = (EntityPlayer) entityIn;
            BlockPos[] ss = getPlayerSelection(player);
            MLTensorDisplayTileEntity display = getPlayerDisplaySelection(player);
            if (ss != null && !ss[0].equals(ss[1])) {
                Util.surrondBlock(worldIn, EnumParticleTypes.REDSTONE, ss[0], 7);
                Util.surrondBlock(worldIn, EnumParticleTypes.REDSTONE, ss[1], 7);
                // Util.fillArea(worldIn, EnumParticleTypes.VILLAGER_HAPPY, ss[0], ss[1], 1);
                Util.surroundArea(worldIn, EnumParticleTypes.REDSTONE, ss[0], ss[1],
                        (int) (Math.sqrt(ss[0].distanceSq(ss[1])) / 2));
            }
            if (display != null) {
                Util.surrondBlock(worldIn, EnumParticleTypes.REDSTONE, display.getPos(), 7);
            }
        }
    }

    public BlockPos[] getPlayerSelectionSorted(EntityPlayer player) {
        BlockPos[] ss = getPlayerSelection(player);
        return ss == null ? Util.sortEdges(ss[0], ss[1]) : ss;
    }

    public BlockPos[] getPlayerSelection(EntityPlayer player) {
        BlockPos s1 = selectionMapLeft.get(player), s2 = selectionMapRight.get(player);
        if (s1 != null && s2 != null) {
            return new BlockPos[]{s1, s2};
        }
        return null;
    }

    public MLTensorDisplayTileEntity getPlayerDisplaySelection(EntityPlayer player) {
        return selectionMapDisplay.get(player);
    }

    public void deSelectDisplay(MLTensorDisplayTileEntity display) {
        selectionMapDisplay.values().remove(display);
    }

    Vec3i b1 = new Vec3i(1, 0, 0), b2 = new Vec3i(0, 1, 0), b3 = new Vec3i(0, 0, 1),
            b4 = new Vec3i(-1, 0, 0), b5 = new Vec3i(0, -1, 0), b6 = new Vec3i(0, 0, -1);
    IBlockState ink = MLScalar.mlScalar.getStateFromMeta(MLConfig.scalarResolution - 1);

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn) {
        if (!world.isRemote) {
            BlockPos pointedPos = player.rayTrace(40, 0f).getBlockPos();
            if (player.getPosition().distanceSq(pointedPos) >= 25) {
                player.sendStatusMessage(
                        new TextComponentString(TextFormatting.LIGHT_PURPLE + "" + TextFormatting.BOLD + "Drawing: " + pointedPos),
                        true);
                //MLBrush brush = player2BrushMap.get(player);
                //if (brush == null) player2BrushMap.put(player, brush = new MLBrush());
                brush.nextPoint(world, pointedPos);
            }
        }
        return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(handIn));
    }

    public void setPlayer2Brush(EntityPlayer p, MLBrush b) {
        player2BrushMap.put(p, b);
        brush=b;
    }

    public MLBrush getPlayer2Brush(EntityPlayer p) {
        //return player2BrushMap.get(p);
        return brush;
    }

    static void info(String s) {
        MCML.logger.info(s);
    }
}
