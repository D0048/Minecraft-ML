package io.github.d0048.common.blocks;

import javax.annotation.Nullable;

import io.github.d0048.MCML;
import io.github.d0048.common.MLTab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class MLScalar extends MLBlockBase {
	public static MLScalar mlScalar;// this holds the unique instance of your block
	public static ItemBlock mlScalarItemBlock;

	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		MCML.logger.info("clicked");
		return true;
	}

	public static void commonInit() {
		ForgeRegistries.BLOCKS.register(mlScalar = new MLScalar());
		mlScalarItemBlock = new ItemBlock(mlScalar);
		mlScalarItemBlock.setRegistryName(mlScalar.getRegistryName());
		ForgeRegistries.ITEMS.register(mlScalarItemBlock);
		mlScalar.setCreativeTab(MLTab.mlTab);
	}

	public static void clientInit() {
		ModelLoader.setCustomModelResourceLocation(mlScalarItemBlock, 0,
				new ModelResourceLocation("minecraft_ml:ml_scalar", "inventory"));
	}

	public MLScalar() {
		super("ml_scalar", "Scalar");
	}

}