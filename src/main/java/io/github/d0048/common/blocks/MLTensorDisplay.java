package io.github.d0048.common.blocks;

import javax.annotation.Nullable;

import io.github.d0048.MCML;
import io.github.d0048.common.MLTab;
import io.github.d0048.common.gui.MLTensorDisplayGui;
import io.github.d0048.common.items.MLWand;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import scala.reflect.internal.Trees.This;

public class MLTensorDisplay extends MLBlockBase {
    public static MLTensorDisplay mlTensorDisplay;// this holds the unique instance of your block
    public static ItemBlock mlTensorDisplatItemBlock;

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
        mlTensorDisplatItemBlock = new ItemBlock(mlTensorDisplay);
        mlTensorDisplatItemBlock.setRegistryName(mlTensorDisplay.getRegistryName());
        mlTensorDisplatItemBlock.setUnlocalizedName(mlTensorDisplay.getUnlocalizedName());
        ForgeRegistries.ITEMS.register(mlTensorDisplatItemBlock);
    }

    public static void clientInit() {
        ModelLoader.setCustomModelResourceLocation(mlTensorDisplatItemBlock, 0,
                new ModelResourceLocation("minecraft_ml:ml_tensordisplay", "inventory"));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        Item item = playerIn.inventory.getCurrentItem().getItem();
        if (!worldIn.isRemote) {
            ((MLTensorDisplayTileEntity) worldIn.getTileEntity(pos)).toggleWritable();
            ((MLTensorDisplayTileEntity) worldIn.getTileEntity(pos)).setDataID("Display test 0");
            playerIn.sendMessage(
                    new TextComponentString("Display is now " + (getMetaFromState(state) == 0 ? "ro" : "rw")));
            return true;
        } else {

        }
        // playerIn.openGui(MCML.instance, 0, worldIn, pos.getX(), pos.getY(),
        // pos.getZ());

        return !(item instanceof MLWand);

    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            ((MLTensorDisplayTileEntity) worldIn.getTileEntity(pos)).Cleanup();
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
        // return new MLTensorDisplayTileEntity();
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

    static void info(String s) {
        MCML.logger.info(s);
    }

}
