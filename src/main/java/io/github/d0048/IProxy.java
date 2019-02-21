package io.github.d0048;

import com.jcraft.jorbis.Block;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IProxy {
	public void preInit();

	public void init();

	public void postInit();
	/*public void clientRegisterBlock(Block b);
	public void clientRegisterItem(Item i);*/
}
