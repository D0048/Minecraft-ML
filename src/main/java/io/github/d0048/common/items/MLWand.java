package io.github.d0048.common.items;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import io.github.d0048.MCML;
import io.github.d0048.common.MLTab;
import io.github.d0048.common.blocks.MLTensorDisplay;
import io.github.d0048.common.blocks.MLTensorDisplayTileEntity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import scala.swing.TextComponent;

public class MLWand extends MLItemBase {
	public static MLWand mlWand;
	HashMap<EntityPlayer, TileEntity> selectionMap = new HashMap<EntityPlayer, TileEntity>();

	public MLWand(String regName, String dispName) {
		super(regName, dispName);
	}

	public static void commonInit() {
		mlWand = new MLWand("ml_wand", "Wand");
	}

	public static void clientInit() {
		ModelLoader.setCustomModelResourceLocation(mlWand, 0,
				new ModelResourceLocation(MCML.MODID + ":ml_wand", "inventory"));
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			TileEntity te = worldIn.getTileEntity(pos);
			if (te != null) {
				selectionMap.put(player, te);
				info("Wand selected " + te);
				if (te instanceof MLTensorDisplayTileEntity) {
					player.sendStatusMessage(new TextComponentString("Current Selection: [Tensor Display] at " + pos),
							true);
				}
			}

		}
		return EnumActionResult.SUCCESS;
	}

	static void info(String s) {
		MCML.logger.info(s);
	}

}
