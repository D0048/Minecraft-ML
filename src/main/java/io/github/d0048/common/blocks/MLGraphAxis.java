package io.github.d0048.common.blocks;

import io.github.d0048.MLConfig;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MLGraphAxis extends MLBlockBase {
    public static MLGraphAxis mlGraphAxis;// this holds the unique instance of your block
    public static ItemBlock mlGraphAxisItemBlock;
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public static void commonInit() {
        ForgeRegistries.BLOCKS.register(mlGraphAxis = new MLGraphAxis());
        //GameRegistry.registerTileEntity(MLGraphAxisTileEntity.class,new ModelResourceLocation("minecraft_ml:ml_graphaxis" + (MLConfig.HQ_MODEL ? "" : "_simplified"),         "normal"));
        ModelLoader.setCustomStateMapper(mlGraphAxis, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation("minecraft_ml:ml_graphaxis", state.getValue(FACING).toString());
            }
        });
        mlGraphAxisItemBlock = new ItemBlock(mlGraphAxis);
        mlGraphAxisItemBlock.setRegistryName(mlGraphAxis.getRegistryName());
        mlGraphAxisItemBlock.setUnlocalizedName(mlGraphAxis.getUnlocalizedName());
        ForgeRegistries.ITEMS.register(mlGraphAxisItemBlock);
    }

    public static void clientInit() {
        ModelLoader.setCustomModelResourceLocation(mlGraphAxisItemBlock, 0,
                new ModelResourceLocation("minecraft_ml:ml_graphaxis", "inventory"));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);
        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }
        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing) state.getValue(FACING)).getIndex();
    }

    public MLGraphAxis() {
        super("ml_graphaxis", "GraphAxis");
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }
}
