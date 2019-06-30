package io.github.d0048.util;

import java.util.Random;

import io.github.d0048.MCML;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Util {

	static Random rand = new Random();

	public static void surrendBlock(World world, EnumParticleTypes effect, BlockPos pos, int intensity) {
		double step = 1D / intensity;
		double x = pos.getX(), y = pos.getY(), z = pos.getZ();
		double recstd = 10;
		for (int i = 0; i < intensity; i++) {
			double dev = rand.nextGaussian() / recstd;
			world.spawnParticle(effect, x + dev, y + step * i, z + dev, 0, 1 + dev / 2, 0);
			world.spawnParticle(effect, x + 1 + dev, y + step * i, z, 0, 1 + dev / 2, 0);
			world.spawnParticle(effect, x + dev, y + step * i, z + 1, 0, 1 + dev / 2, 0);
			world.spawnParticle(effect, x + 1 + dev, y + step * i, z + 1, 0, 1 + dev / 2, 0);

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

	public static BlockPos[] sortEdges(BlockPos s0, BlockPos s1) {
		return new BlockPos[] {
				new BlockPos(Math.min(s0.getX(), s1.getX()), Math.min(s0.getY(), s1.getY()),
						Math.min(s0.getZ(), s1.getZ())),
				new BlockPos(Math.max(s0.getX(), s1.getX()), Math.max(s0.getY(), s1.getY()),
						Math.max(s0.getZ(), s1.getZ())) };
	}

	static void info(String s) {
		MCML.logger.info(s);
	}
}
