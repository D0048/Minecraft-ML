package io.github.d0048.common.items;

import io.github.d0048.MCML;
import io.github.d0048.common.MLTab;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class MLItemBase extends Item {

    public static MLItemBase mlItemBase;

    public static void commonInit() {
        mlItemBase = new MLItemBase("ml_itembase", "ML Item Base");
    }

    public static void clientInit() {
        ModelLoader.setCustomModelResourceLocation(mlItemBase, 0,
                new ModelResourceLocation(MCML.MODID + ":ml_itembase", "inventory"));
    }

    public MLItemBase(String regName, String dispName) {
        setUnlocalizedName(dispName).setRegistryName(regName).setCreativeTab(MLTab.mlTab).setMaxStackSize(1);
        ForgeRegistries.ITEMS.register(this);
        setMaxDamage(0);
    }


}
