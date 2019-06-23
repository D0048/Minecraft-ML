package io.github.d0048.common.blocks;

import javax.annotation.Nullable;

import io.github.d0048.common.MLTab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MLBlockBase extends Block {
	public static MLBlockBase mlBlockBase;// this holds the unique instance of your block
	public static ItemBlock mlBlockBaseItemBlock;

	public static void commonInit() {
		ForgeRegistries.BLOCKS.register(mlBlockBase = new MLBlockBase("ml_blockbase", "MCML BlockBase"));
		mlBlockBaseItemBlock = new ItemBlock(mlBlockBase);
		mlBlockBaseItemBlock.setRegistryName(mlBlockBase.getRegistryName());
		ForgeRegistries.ITEMS.register(mlBlockBaseItemBlock);
		//mlBlockBaseItemBlock.setFull3D();
		mlBlockBase.setCreativeTab(MLTab.mlTab);
	}

	public static void clientInit() {
		ModelLoader.setCustomModelResourceLocation(mlBlockBaseItemBlock, 0,
				new ModelResourceLocation("minecraft_ml:ml_blockbase", "inventory"));
	}

	public MLBlockBase(String registryName, String unlocalizedName) {
		super(Material.ROCK);
		this.setRegistryName(registryName);
		this.setUnlocalizedName(unlocalizedName);
		this.setLightLevel(1f);
		this.setLightOpacity(1);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return new AxisAlignedBB(0.25D, 0.25D, 0.25D, 0.75D, 0.75D, 0.75D);
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return new AxisAlignedBB(0.25D, 0.25D, 0.25D, 0.75D, 0.75D, 0.75D);
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

	@Override
	public MLBlockBase setCreativeTab(CreativeTabs tab) {
		super.setCreativeTab(tab);
		return this;
	}
}
