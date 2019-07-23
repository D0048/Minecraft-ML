package io.github.d0048.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.io.Serializable;

public class SerializableBlockPos implements Serializable {
    double x, y, z;

    public SerializableBlockPos(BlockPos p) {
        x = p.getX();
        y = p.getY();
        z = p.getZ();
    }

    public SerializableBlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public SerializableBlockPos(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPos getPos() {
        return new BlockPos(x, y, z);
    }

    @Override
    public String toString() {
        return "SerializableBlockPos{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
