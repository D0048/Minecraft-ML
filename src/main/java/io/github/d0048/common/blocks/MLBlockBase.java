package io.github.d0048.common.blocks;

import javax.annotation.Nullable;

import io.github.d0048.MCML;
import io.github.d0048.MLConfig;
import io.github.d0048.common.MLTab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
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
        mlBlockBaseItemBlock.setUnlocalizedName(mlBlockBase.getUnlocalizedName());
        ForgeRegistries.ITEMS.register(mlBlockBaseItemBlock);

    }

    public static void clientInit() {
        ModelLoader.setCustomModelResourceLocation(mlBlockBaseItemBlock, 0,
                new ModelResourceLocation("minecraft_ml:ml_blockbase", "inventory"));
    }

    public MLBlockBase(String registryName, String unlocalizedName) {
        super(Material.ROCK);
        this.setRegistryName(registryName);
        this.setUnlocalizedName(unlocalizedName);
        this.setLightLevel(0.8f);
        this.setLightOpacity(1);
        setCreativeTab(MLTab.mlTab);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public String getInfoAt(World world, BlockPos pos) {
        return this.toString();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        //return new AxisAlignedBB(0.25D, 0.25D, 0.25D, 0.75D, 0.75D, 0.75D);
        return FULL_BLOCK_AABB;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        //return FULL_BLOCK_AABB;
        return new AxisAlignedBB(0.25D, 0.25D, 0.25D, 0.75D, 0.75D, 0.75D);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return new AxisAlignedBB(0.25D, 0.25D, 0.25D, 0.75D, 0.75D, 0.75D);
        //return FULL_BLOCK_AABB;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState iBlockState) {
        return EnumBlockRenderType.MODEL;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState iBlockState) {
        return !MLConfig.HQ_MODEL;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState iBlockState) {
        return false;
    }

    static void info(String s) {
        MCML.logger.info(s);
    }

}
