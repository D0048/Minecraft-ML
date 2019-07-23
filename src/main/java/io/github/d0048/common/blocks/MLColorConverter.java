package io.github.d0048.common.blocks;

import io.github.d0048.MLConfig;
import io.github.d0048.client.gui.MLTensorDisplayGui;
import io.github.d0048.common.networking.MCMLNetworkingBus;
import io.github.d0048.common.networking.MLTensorDisplaySyncMessage;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;

public class MLColorConverter extends MLBlockBase {
    public static MLColorConverter mlColorConverter;// this holds the unique instance of your block
    public static ItemBlock mlColorConvertItemBlock;
    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    public static void commonInit() {
        ForgeRegistries.BLOCKS.register(mlColorConverter = new MLColorConverter());
        GameRegistry.registerTileEntity(MLColorConverterTileEntity.class,
                new ModelResourceLocation("minecraft_ml:ml_colorconverter" + (MLConfig.HQ_MODEL ? "" : "_simplified"),
                        "normal"));
        // Nonstandard naming, too lazy to change...
        ModelLoader.setCustomStateMapper(mlColorConverter, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation("minecraft_ml:ml_colorconverter" + (MLConfig.HQ_MODEL ? "" : "_simplified"),
                        state.getValue(FACING).toString());
            }
        });
        mlColorConvertItemBlock = new ItemBlock(mlColorConverter);
        mlColorConvertItemBlock.setRegistryName(mlColorConverter.getRegistryName());
        mlColorConvertItemBlock.setUnlocalizedName(mlColorConverter.getUnlocalizedName());
        ForgeRegistries.ITEMS.register(mlColorConvertItemBlock);
    }

    public static void clientInit() {
        ModelLoader.setCustomModelResourceLocation(mlColorConvertItemBlock, 0,
                new ModelResourceLocation("minecraft_ml:ml_colorconverter" + (MLConfig.HQ_MODEL ? "" : "_simplified"),
                        "inventory"));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        if (world.isRemote) return null;
        MLColorConverterTileEntity te = new MLColorConverterTileEntity();
        te.setWorld(world);
        return te;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
        refreshAt(worldIn, pos);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            refreshAt(worldIn, pos);
            playerIn.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "Converter re-rendered!"));
            return false;
        }
        return false;
    }

    public static void refreshAt(World world, BlockPos pos) {
        try {
            ((MLColorConverterTileEntity) world.getTileEntity(pos)).refresh();
        } catch (Exception e) {
            info("Error occur refreshing Color Converter at " + pos);
            e.printStackTrace();
        }
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

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    public MLColorConverter() {
        super("ml_colorconverter", "ColorConverter");
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }
}
