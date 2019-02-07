package io.github.d0048;

import io.github.d0048.MCML;
import io.github.d0048.common.blocks.MLBlockBase;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ServerProxy implements IProxy{
	public void preInit() {
		ForgeRegistries.BLOCKS.register(MCML.mlBlockBase = new MLBlockBase());
		MCML.mlBlockBaseItemBlock = new ItemBlock(MCML.mlBlockBase);
		MCML.mlBlockBaseItemBlock.setRegistryName(MCML.mlBlockBase.getRegistryName());
		ForgeRegistries.ITEMS.register(MCML.mlBlockBaseItemBlock);
	}

	public void init() {
	}

	public void postInit() {
	}
}
