package io.github.d0048.common.items;

import io.github.d0048.MCML;
import io.github.d0048.common.MLTab;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class MLWand extends MLItemBase {
	public static MLWand mlWand;

	public MLWand(String regName, String dispName) {
		super(regName, dispName);
	}

	public static void commonInit() {
		mlWand = new MLWand("ml_wand", "Wand");
	}

	public static void clientInit() {
		ModelLoader.setCustomModelResourceLocation(mlWand, 0,
				new ModelResourceLocation(MCML.MODID + ":ml_wand", "inventory"));
	}
}
