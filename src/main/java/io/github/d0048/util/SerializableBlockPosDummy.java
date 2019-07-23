package io.github.d0048.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.io.Serializable;

public class SerializableBlockPosDummy extends BlockPos {
    public SerializableBlockPosDummy() {
        super(0, 0, 0);
    }

    public SerializableBlockPosDummy(int x, int y, int z) {
        super(x, y, z);
    }

    public SerializableBlockPosDummy(double x, double y, double z) {
        super(x, y, z);
    }

    public SerializableBlockPosDummy(Entity source) {
        super(source);
    }

    public SerializableBlockPosDummy(Vec3d vec) {
        super(vec);
    }

    public SerializableBlockPosDummy(Vec3i source) {
        super(source);
    }
}
