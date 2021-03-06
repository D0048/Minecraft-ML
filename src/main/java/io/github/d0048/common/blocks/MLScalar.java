package io.github.d0048.common.blocks;

import io.github.d0048.MLConfig;
import io.github.d0048.common.MLAsyncHelper;
import net.minecraft.util.text.TextFormatting;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.text.DecimalFormat;

public class MLScalar extends MLBlockBase {
    public static MLScalar mlScalar;// this holds the unique instance of your block
    public static ItemBlock mlScalarItemBlock;

    public static IProperty<Integer> propertyValue = PropertyInteger.create("value", 0, MLConfig.scalarResolution - 1);
    public static IStateMapper valueMapper;

    public static void commonInit() {
        ForgeRegistries.BLOCKS.register(mlScalar = new MLScalar());
        ModelLoader.setCustomStateMapper(mlScalar, valueMapper = new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                int stateVal = state.getValue(MLScalar.propertyValue).intValue();
                int modelVal = (int) ((((double) stateVal + 0.9) / MLConfig.scalarResolution) * 16);
                //info(stateVal + " -> " + modelVal);
                return new ModelResourceLocation("minecraft_ml:ml_scalar" + (MLConfig.HQ_MODEL ? "" : "_simplified"),
                        "value_" + modelVal);
            }
        });
        mlScalarItemBlock = new ItemBlock(mlScalar);
        mlScalarItemBlock.setRegistryName(mlScalar.getRegistryName());
        mlScalarItemBlock.setUnlocalizedName(mlScalar.getUnlocalizedName());
        ForgeRegistries.ITEMS.register(mlScalarItemBlock);
    }

    public static void clientInit() {
        ModelLoader.setCustomModelResourceLocation(mlScalarItemBlock, 0,
                new ModelResourceLocation("minecraft_ml:ml_scalar", "inventory"));
    }


    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, propertyValue);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(propertyValue).intValue();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(propertyValue, meta);
    }

    private static DecimalFormat df = new DecimalFormat("##.##");

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (hand.equals(EnumHand.OFF_HAND)) return false;
        int newVal = (getValueAt(worldIn, pos) + 1) % MLConfig.scalarResolution;
        //info(getValueAt(worldIn, pos) + " -> " + newVal);
        setValue(worldIn, pos, newVal);
        if (worldIn.isRemote)
            playerIn.sendStatusMessage(
                    new TextComponentString(
                            TextFormatting.LIGHT_PURPLE + "" + TextFormatting.BOLD
                                    + "Normalized: [" + String.format("%02d", newVal) + "/" + MLConfig.scalarResolution + "] -> "
                                    + df.format(((double) newVal * 100 / MLConfig.scalarResolution)) + "%"),
                    true);
        return false;
    }

    public MLScalar() {
        super("ml_scalar", "Scalar");
    }

    public String getInfoAt(World world, BlockPos pos) {
        String ret = TextFormatting.LIGHT_PURPLE + toString() + " of value: " + TextFormatting.YELLOW + world.getBlockState(pos);
        return ret;
    }

    public static void setValue(World worldIn, BlockPos pos, int val) {
        if (worldIn.getBlockState(pos).getBlock() != mlScalar)
            return;
        placeAt(worldIn, pos, val);
    }

    public static void placeAt(World world, BlockPos pos, int val) {
        //info("Value " + val + " placed at " + pos);
        val = Math.min(Math.max(0, val), MLConfig.scalarResolution - 1);
        if (world.getBlockState(pos).getBlock() != mlScalar || mlScalar.getMetaFromState(world.getBlockState(pos)) != val) {
            world.setBlockState(pos, mlScalar.getStateFromMeta(val));
            //MLAsyncHelper.placeAsync(world,pos,mlScalar.getStateFromMeta(val));
            int find = 4;
            while (find-- > 0) {
                if (world.getBlockState(pos.add(0, find, 0)).getBlock() == MLColorConverter.mlColorConverter) {
                    try {
                        ((MLColorConverterTileEntity) world.getTileEntity(pos.add(0, find, 0))).refresh();
                    } catch (Exception e) {
                    }
                    break;
                }
            }
        }
    }

    public static int getValueAt(World world, BlockPos pos) {
        IBlockState state;
        if ((state = world.getBlockState(pos)).getBlock() == mlScalar) {
            return mlScalar.getMetaFromState(state);
        }
        return -1;
    }
}