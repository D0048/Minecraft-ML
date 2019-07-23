package io.github.d0048.util;

import io.github.d0048.common.MLAsyncHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.Random;

public class ParticleUtil {
    static Random rand = new Random();

    public static void fillArea(World world, EnumParticleTypes effect, BlockPos pos1, BlockPos pos2, int intensity) {
        fillArea(world, effect, pos1, pos2, intensity, 0D, 0D, 0D);
    }

    public static void fillArea(World world, EnumParticleTypes effect, BlockPos pos1, BlockPos pos2, int intensity, double xs,
                                double ys, double zs) {
        BlockPos[] ss = Util.sortEdges(pos1, pos2);
        pos1 = ss[0];
        pos2 = ss[1] = ss[1].add(1, 1, 1);
        for (int i = ss[0].getX(); i < ss[1].getX(); i++)
            for (int j = ss[0].getY(); j < ss[1].getY(); j++)
                for (int k = ss[0].getZ(); k < ss[1].getZ(); k++)
                    for (int d = 0; d < intensity; d++) {
                        spawnParticle(world, effect, i + 0.5, j + 0.5, k + 0.5, xs, ys, zs);
                    }
    }

    public static void surroundArea(World world, EnumParticleTypes effect, BlockPos pos1, BlockPos pos2, int intensity) {
        surroundArea(world, effect, pos1, pos2, intensity, 0D, 0D, 0D);
    }

    public static void surroundArea(World world, EnumParticleTypes effect, BlockPos pos1, BlockPos pos2, int intensity, double xs,
                                    double ys, double zs) {
        BlockPos[] ss = Util.sortEdges(pos1, pos2);
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
                spawnLine(world, effect, new BlockPos(e0), new BlockPos(e1), intensity, xs, ys, zs);
            }
        }

    }

    public static void surrondBlock(World world, EnumParticleTypes effect, BlockPos pos, int intensity) {
        surrondBlock(world, effect, pos, intensity, 0D, 0D, 0D);
    }

    public static void surrondBlock(World world, EnumParticleTypes effect, BlockPos pos, int intensity, double xs, double ys,
                                    double zs) {
        double step = 1D / intensity;
        double x = pos.getX(), y = pos.getY(), z = pos.getZ();
        double recstd = 10;
        for (int i = 0; i < intensity; i++) {
            double dev = rand.nextGaussian() / recstd;
            spawnParticle(world, effect, x + dev, y + step * i + dev * 3, z + dev, xs, ys, zs);
            spawnParticle(world, effect, x + 1 + dev, y + step * i + dev * 3, z + dev, xs, ys, zs);
            spawnParticle(world, effect, x + dev, y + step * i + dev * 3, z + 1 + dev, xs, ys, zs);
            spawnParticle(world, effect, x + 1 + dev, y + step * i + dev * 3, z + 1 + dev, xs, ys, zs);
        }
    }

    public static void spawnLine(World world, EnumParticleTypes effect, BlockPos pos1, BlockPos pos2, int intensity) {
        spawnLine(world, effect, pos1, pos2, intensity, 0D, 0D, 0D);
    }

    public static void spawnLine(World world, EnumParticleTypes effect, BlockPos pos1, BlockPos pos2, int intensity, double xs,
                                 double ys, double zs) {
        //BlockPos[] ss = sortEdges(pos1, pos2);
        //pos1 = ss[0];
        //pos2 = ss[1] = ss[1].add(0, 0, 0);
        double step = 1D / intensity;
        double x1 = pos1.getX() + 0.5, y1 = pos1.getY() + 0.5, z1 = pos1.getZ() + 0.5;
        double stepx = (pos2.getX() - x1 + 0.5) * step, stepy = (pos2.getY() - y1 + 0.5) * step,
                stepz = (pos2.getZ() - z1 + 0.5) * step;
        double recstd = 8;
        for (int i = 0; i < intensity; i++) {
            double dev = rand.nextGaussian() / recstd;
            spawnParticle(world, effect, x1 + stepx * i + dev, y1 + stepy * i + dev, z1 + stepz * i + dev,
                    xs, ys, zs);
        }

    }

    public static void spawnParticle(World world, EnumParticleTypes particleType, double x, double y, double z, double xSpeed,
                                     double ySpeed, double zSpeed) {
        if (world.isRemote)
            world.spawnParticle(particleType, x, y, z, xSpeed, ySpeed, zSpeed);
        else
            ((WorldServer) world).spawnParticle(particleType, x, y, z, 1, xSpeed, ySpeed, zSpeed, 0);
    }

    public static void fillArea(BlockPos[] ss, World world, IBlockState state) {
        ss = Util.sortEdges(ss);
        BlockPos pos1 = ss[0], pos2 = ss[1];
        for (int i = ss[0].getX(); i <= ss[1].getX(); i++)
            for (int j = ss[0].getY(); j <= ss[1].getY(); j++)
                for (int k = ss[0].getZ(); k <= ss[1].getZ(); k++)
                    //world.setBlockState(new BlockPos(i, j, k), state);
                    MLAsyncHelper.placeAsync(world,new BlockPos(i,j,k),state);
    }
}
