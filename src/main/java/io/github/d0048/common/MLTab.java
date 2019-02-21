package io.github.d0048.common;

import io.github.d0048.MCML;
import io.github.d0048.common.blocks.MLBlockBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class MLTab extends CreativeTabs {
	public static MLTab mlTab = new MLTab();

	public MLTab() {
		super(MCML.NAME);
	}

	public MLTab(int index, String label) {
		super(index, label);
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(MLBlockBase.mlBlockBaseItemBlock).setStackDisplayName(MCML.NAME);
	}
}
