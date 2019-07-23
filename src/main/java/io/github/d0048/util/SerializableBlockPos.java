package io.github.d0048.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.io.Serializable;

public class SerializableBlockPos extends SerializableBlockPosDummy implements Serializable {
    public SerializableBlockPos(BlockPos p) {
        super(p.getX(), p.getY(), p.getZ());
    }

    public SerializableBlockPos() {
    }
}
