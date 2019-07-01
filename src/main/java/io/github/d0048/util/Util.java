package io.github.d0048.util;

import java.util.Random;

import io.github.d0048.MCML;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Util {

    static Random rand = new Random();

    public static void fillArea(World world, EnumParticleTypes effect, BlockPos pos1, BlockPos pos2, int intensity) {
        BlockPos[] ss = sortEdges(pos1, pos2);
        pos1 = ss[0];
        pos2 = ss[1] = ss[1].add(1, 1, 1);
        for (int i = ss[0].getX(); i < ss[1].getX(); i++)
            for (int j = ss[0].getY(); j < ss[1].getY(); j++)
                for (int k = ss[0].getZ(); k < ss[1].getZ(); k++)
                    for (int d = 0; d < intensity; d++) {
                        world.spawnParticle(effect, i + 0.5, j + 0.5, k + 0.5, 0, 0, 0);
                    }
    }

    public static void surrendArea(World world, EnumParticleTypes effect, BlockPos pos1, BlockPos pos2, int intensity) {
        BlockPos[] ss = sortEdges(pos1, pos2);
        pos1 = ss[0];
        pos2 = ss[1];
        double x1 = pos1.getX() + 0.5, y1 = pos1.getY() + 0.5, z1 = pos1.getZ() + 0.5;
        double gapx = (pos2.getX() - x1 + 0.5), gapy = (pos2.getY() - y1 + 0.5), gapz = (pos2.getZ() - z1 + 0.5);
        Vec3d[] verteces = new Vec3d[]{new Vec3d(x1, y1, z1), new Vec3d(x1 + gapx, y1, z1),
                new Vec3d(x1, y1 + gapy, z1), new Vec3d(x1, y1, z1 + gapz), new Vec3d(x1 + gapx, y1 + gapy, z1),
                new Vec3d(x1, y1 + gapy, z1 + gapz), new Vec3d(x1 + gapx, y1, z1 + gapz),
                new Vec3d(x1 + gapx, y1 + gapy, z1 + gapz),};
        for (Vec3d e0 : verteces) {
            for (Vec3d e1 : verteces) {
                spawnLine(world, effect, new BlockPos(e0), new BlockPos(e1), intensity);
            }
        }

    }

    public static void surrendBlock(World world, EnumParticleTypes effect, BlockPos pos, int intensity) {
        double step = 1D / intensity;
        double x = pos.getX(), y = pos.getY(), z = pos.getZ();
        double recstd = 10;
        for (int i = 0; i < intensity; i++) {
            double dev = rand.nextGaussian() / recstd;
            world.spawnParticle(effect, x + dev, y + step * i + dev * 3, z + dev, -1, 0 + dev / 2, 0);
            world.spawnParticle(effect, x + 1 + dev, y + step * i + dev * 3, z + dev, -1, 0 + dev / 2, 0);
            world.spawnParticle(effect, x + dev, y + step * i + dev * 3, z + 1 + dev, -1, 0 + dev / 2, 0);
            world.spawnParticle(effect, x + 1 + dev, y + step * i + dev * 3, z + 1 + dev, -1, 0 + dev / 2, 0);
        }
    }

    public static void spawnLine(World world, EnumParticleTypes effect, BlockPos pos1, BlockPos pos2, int intensity) {
        BlockPos[] ss = sortEdges(pos1, pos2);
        pos1 = ss[0];
        pos2 = ss[1] = ss[1].add(0, 0, 0);
        double step = 1D / intensity;
        double x1 = pos1.getX() + 0.5, y1 = pos1.getY() + 0.5, z1 = pos1.getZ() + 0.5;
        double stepx = (pos2.getX() - x1 + 0.5) * step, stepy = (pos2.getY() - y1 + 0.5) * step,
                stepz = (pos2.getZ() - z1 + 0.5) * step;
        double recstd = 8;
        for (int i = 0; i < intensity; i++) {
            double dev = rand.nextGaussian() / recstd;
            world.spawnParticle(effect, x1 + stepx * i + dev, y1 + stepy * i + dev, z1 + stepz * i + dev,
                    0, 0.0, 0.0);
        }

    }

    public static void spawnParticle(World world, EnumParticleTypes particleType, Vec3d vec, double xSpeed,
                                     double ySpeed, double zSpeed) {
        info("Spawned: " + vec);
        world.spawnParticle(particleType, vec.x, vec.y, vec.z, xSpeed, ySpeed, zSpeed);
    }

    // {low,high}
    public static BlockPos[] sortEdges(BlockPos[] ss) {
        assert (ss.length == 2);
        return sortEdges(ss[0], ss[1]);
    }

    // {low,high}
    public static BlockPos[] sortEdges(BlockPos s0, BlockPos s1) {
        return new BlockPos[]{
                new BlockPos(Math.min(s0.getX(), s1.getX()), Math.min(s0.getY(), s1.getY()),
                        Math.min(s0.getZ(), s1.getZ())),
                new BlockPos(Math.max(s0.getX(), s1.getX()), Math.max(s0.getY(), s1.getY()),
                        Math.max(s0.getZ(), s1.getZ()))};
    }

    static void info(String s) {
        MCML.logger.info(s);
    }
}
