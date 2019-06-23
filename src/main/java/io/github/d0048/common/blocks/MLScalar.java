package io.github.d0048.common.blocks;

import io.github.d0048.common.MLTab;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class MLScalar extends MLBlockBase {
	public static MLScalar mlScalar;// this holds the unique instance of your block
	public static ItemBlock mlScalarItemBlock;

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