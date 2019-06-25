package io.github.d0048.client;

import io.github.d0048.IProxy;
import io.github.d0048.MCML;
import io.github.d0048.common.blocks.MLBlockBase;
import io.github.d0048.common.blocks.MLScalar;
import io.github.d0048.common.blocks.MLTensorDisplay;
import net.minecraftforge.client.model.obj.OBJLoader;

public class ClientProxy implements IProxy {
	@Override
	public void preInit() {
		OBJLoader.INSTANCE.addDomain(MCML.MODID);
		//TODO: Add new
		MLBlockBase.clientInit();
		MLScalar.clientInit();
		MLTensorDisplay.clientInit();
	}

	@Override
	public void init() {
	}

	@Override
	public void postInit() {
	}
}
