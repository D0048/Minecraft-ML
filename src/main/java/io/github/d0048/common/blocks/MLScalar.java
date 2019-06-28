package io.github.d0048.common.blocks;

import org.tensorflow.TensorFlow;

import io.github.d0048.MCML;
import io.github.d0048.common.MLTab;
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
import scala.collection.parallel.ParIterableLike.Max;

public class MLScalar extends MLBlockBase {
	public static MLScalar mlScalar;// this holds the unique instance of your block
	public static ItemBlock mlScalarItemBlock;

	public static IProperty<Integer> propertyValue = PropertyInteger.create("value", 0, MCML.scalarResolution - 1);
	public static IStateMapper valueMapper;

	public static void commonInit() {
		ForgeRegistries.BLOCKS.register(mlScalar = new MLScalar());
		ModelLoader.setCustomStateMapper(mlScalar, valueMapper = new ValueStateMapper());
		mlScalarItemBlock = new ItemBlock(mlScalar);
		mlScalarItemBlock.setRegistryName(mlScalar.getRegistryName());
		mlScalarItemBlock.setUnlocalizedName(mlScalar.getUnlocalizedName());
		ForgeRegistries.ITEMS.register(mlScalarItemBlock);
	}

	public static void clientInit() {
		ModelLoader.setCustomModelResourceLocation(mlScalarItemBlock, 0,
				new ModelResourceLocation("minecraft_ml:ml_scalar", "inventory"));
	}

	public static void placeAt(World world, BlockPos pos, int val) {
		info("Value " + val + " placed at " + pos);
		val = Math.min(Math.max(0, val), MCML.scalarResolution - 1);
		world.setBlockState(pos, MLScalar.mlScalar.getStateFromMeta(val));
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

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		worldIn.setBlockState(pos, getStateFromMeta((getMetaFromState(state) + 1) % MCML.scalarResolution));
		if (worldIn.isRemote)
			playerIn.sendMessage(
					new TextComponentString("New State: " + ((getMetaFromState(state) + 1) % MCML.scalarResolution)));
		return true;
	}

	public void setValue(World worldIn, BlockPos pos, int val, int lower, int upper) {
		if (val > upper || val < lower || upper < lower)
			return;
		worldIn.setBlockState(pos, getStateFromMeta((int) ((double) val / (upper - lower) * MCML.scalarResolution)));
	}

	public MLScalar() {
		super("ml_scalar", "Scalar");
	}

	static void info(String s) {
		MCML.logger.info(s);
	}
}

class ValueStateMapper extends StateMapperBase {
	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		int val = state.getValue(MLScalar.propertyValue).intValue();
		return new ModelResourceLocation("minecraft_ml:ml_scalar", "value_" + val);
	}
}