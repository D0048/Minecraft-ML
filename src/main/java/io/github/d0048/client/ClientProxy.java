package io.github.d0048.client;

import io.github.d0048.IProxy;
import io.github.d0048.MCML;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy implements IProxy {
	@Override
	public void preInit() {
		ModelLoader.setCustomModelResourceLocation(MCML.mlBlockBaseItemBlock, 0,
				new ModelResourceLocation("minecraft_ml:ml_blockbase_item", "inventory"));
	}

	@Override
	public void init() {
	}

	@Override
	public void postInit() {
	}
}
