package io.github.d0048.client;

import io.github.d0048.IProxy;
import io.github.d0048.MCML;
import io.github.d0048.common.blocks.MLBlockBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy implements IProxy {
	@Override
	public void preInit() {
		MLBlockBase.clientInit();
	}

	@Override
	public void init() {
	}

	@Override
	public void postInit() {
	}
}
