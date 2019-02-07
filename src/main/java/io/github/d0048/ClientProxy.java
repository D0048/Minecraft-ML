package io.github.d0048;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy implements IProxy {
	@Override
	public void preInit() {
		// super.preInit();
		ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation("MLBlockBase:mbe01_block_simple",
				"inventory");
		final int DEFAULT_ITEM_SUBTYPE = 0;
		ModelLoader.setCustomModelResourceLocation(MCML.mlBlockBaseItemBlock, DEFAULT_ITEM_SUBTYPE,
				itemModelResourceLocation);
	}

	@Override
	public void init() {
	}

	@Override
	public void postInit() {
	}
}
