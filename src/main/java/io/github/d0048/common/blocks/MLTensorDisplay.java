package io.github.d0048.common.blocks;

import io.github.d0048.MCML;
import io.github.d0048.common.items.MLWand;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
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

public class MLTensorDisplay extends MLBlockBase {
    public static MLTensorDisplay mlTensorDisplay;// this holds the unique instance of your block
    public static ItemBlock mlTensorDisplayItemBlock;

    public static IProperty<Boolean> propertyWritable = PropertyBool.create("writable");

    public static void commonInit() {
        ForgeRegistries.BLOCKS.register(mlTensorDisplay = new MLTensorDisplay());
        GameRegistry.registerTileEntity(MLTensorDisplayTileEntity.class,
                new ModelResourceLocation("minecraft_ml:ml_tensordisplay", "green"));
        ModelLoader.setCustomStateMapper(mlTensorDisplay, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return state.getValue(propertyWritable).booleanValue()
                        ? new ModelResourceLocation("minecraft_ml:ml_tensordisplay", "green")
                        : new ModelResourceLocation("minecraft_ml:ml_tensordisplay", "red");
            }
        });
        mlTensorDisplayItemBlock = new ItemBlock(mlTensorDisplay);
        mlTensorDisplayItemBlock.setRegistryName(mlTensorDisplay.getRegistryName());
        mlTensorDisplayItemBlock.setUnlocalizedName(mlTensorDisplay.getUnlocalizedName());
        ForgeRegistries.ITEMS.register(mlTensorDisplayItemBlock);
    }

    public static void clientInit() {
        ModelLoader.setCustomModelResourceLocation(mlTensorDisplayItemBlock, 0,
                new ModelResourceLocation("minecraft_ml:ml_tensordisplay", "inventory"));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        Item item = playerIn.inventory.getCurrentItem().getItem();
        if (!worldIn.isRemote) {
            MLTensorDisplayTileEntity display = ((MLTensorDisplayTileEntity) worldIn.getTileEntity(pos));
            display.reDraw();
            playerIn.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "Display re-rendered!"));
            return true;
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            ((MLTensorDisplayTileEntity) worldIn.getTileEntity(pos)).Cleanup().unregisterID();
            MLWand.mlWand.deSelectDisplay(((MLTensorDisplayTileEntity) worldIn.getTileEntity(pos)));
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    @Nullable
    public TileEntity createTileEntity(World world, IBlockState state) {
        return world.isRemote ? null : new MLTensorDisplayTileEntity();
        //return new MLTensorDisplayTileEntity();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, propertyWritable);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(propertyWritable).booleanValue() ? 1 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(propertyWritable, meta == 1);
    }

    public String getInfoAt(World world, BlockPos pos) {
        return world.getTileEntity(pos) + "";
    }

    public MLTensorDisplay() {
        super("ml_tensordisplay", "TensorDisplay");
    }
}
