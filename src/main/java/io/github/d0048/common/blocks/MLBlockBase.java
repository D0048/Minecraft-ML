package io.github.d0048.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MLBlockBase extends Block {
	public static MLBlockBase mlBlockBase;// this holds the unique instance of your block
	public static ItemBlock mlBlockBaseItemBlock;

	public static void commonInit() {
		ForgeRegistries.BLOCKS.register(mlBlockBase = new MLBlockBase());
		mlBlockBaseItemBlock = new ItemBlock(mlBlockBase);
		mlBlockBaseItemBlock.setRegistryName(mlBlockBase.getRegistryName());
		ForgeRegistries.ITEMS.register(mlBlockBaseItemBlock);
	}

	public static void clientInit() {
		ModelLoader.setCustomModelResourceLocation(MLBlockBase.mlBlockBaseItemBlock, 0,
				new ModelResourceLocation("minecraft_ml:ml_blockbase_item", "inventory"));
	}

	public MLBlockBase() {
		super(Material.ROCK);
		this.setCreativeTab(CreativeTabs.REDSTONE); // the block will appear on the Blocks tab in creative
		this.setUnlocalizedName("MCML BlockBase");
		this.setRegistryName("ml_blockbase");
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState iBlockState) {
		return EnumBlockRenderType.MODEL;
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.SOLID;
	}

	@Override
	public boolean isOpaqueCube(IBlockState iBlockState) {
		return true;
	}
}
