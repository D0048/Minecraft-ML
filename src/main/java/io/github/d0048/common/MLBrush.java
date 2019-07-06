package io.github.d0048.common;

import io.github.d0048.MLConfig;
import io.github.d0048.common.blocks.MLScalar;
import io.github.d0048.util.Util;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.awt.*;

import static io.github.d0048.common.MLWandCommand.info;

public class MLBrush {
    int R = 255, G = 255, B = 255, radius = 1;
    double attn = 0;

    public void nextPoint(World world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() == MLScalar.mlScalar) {
            int val = (int) (((double) (R + G + B)) / (255 * 3) * MLConfig.scalarResolution);
            Iterable<BlockPos> poss = BlockPos.getAllInBox(pos.add(radius, radius, radius), pos.add(-radius, -radius, -radius));
            poss.forEach(p -> {
                if (p.distanceSq(pos) <= radius * radius) MLScalar.setValue(world, p, val);
            });
        } else if (world.getBlockState(pos.add(0, -3, 0)).getBlock() == MLScalar.mlScalar) {
            paintLayer(world, pos.add(0, -3, 0), R);
            paintLayer(world, pos.add(0, -4, 0), G);
            paintLayer(world, pos.add(0, -5, 0), B);
        }
    }

    void paintLayer(World world, BlockPos pos, int val) {
        int v = (int) (((double) val) / (255 * 3) * MLConfig.scalarResolution);
        Iterable<BlockPos> poss = BlockPos.getAllInBox(pos.add(radius, 0, radius), pos.add(-radius, 0, -radius));
        poss.forEach(p -> {
            if (p.distanceSq(pos) <= radius * radius) MLScalar.setValue(world, p, v);
        });
    }

    public MLBrush(int r, int g, int b, int radius, double attn) {
        R = r;
        G = g;
        B = b;
        this.radius = radius;
        this.attn = attn;
    }

    public MLBrush(int r, int g, int b, int radius) {
        R = r;
        G = g;
        B = b;
        this.radius = radius;
    }

    public MLBrush(int r, int g, int b) {
        R = r;
        G = g;
        B = b;
    }

    @Override
    public String toString() {
        String ret = TextFormatting.LIGHT_PURPLE + "Brush: \n";
        ret += TextFormatting.LIGHT_PURPLE + "    - Color: " + Util.rgb2Hex(new int[]{R, G, B});
        ret += TextFormatting.LIGHT_PURPLE + "    - Size: " + radius;
        ret += TextFormatting.LIGHT_PURPLE + "    - Attenuation: " + attn;
        return ret;
    }

    public MLBrush() {
    }
}
