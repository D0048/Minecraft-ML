package io.github.d0048.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import io.github.d0048.MCML;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.Range;

public class Util {

    static Random rand = new Random();

    public static void fillArea(World world, EnumParticleTypes effect, BlockPos pos1, BlockPos pos2, int intensity) {
        fillArea(world, effect, pos1, pos2, intensity, 0D, 0D, 0D);
    }

    public static void fillArea(World world, EnumParticleTypes effect, BlockPos pos1, BlockPos pos2, int intensity, double xs,
                                double ys, double zs) {
        BlockPos[] ss = sortEdges(pos1, pos2);
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

    /**
     * {low,high}
     **/
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

    public static void fillArea(BlockPos[] ss, World world, IBlockState state) {
        ss = sortEdges(ss);
        BlockPos pos1 = ss[0], pos2 = ss[1];
        for (int i = ss[0].getX(); i <= ss[1].getX(); i++)
            for (int j = ss[0].getY(); j <= ss[1].getY(); j++)
                for (int k = ss[0].getZ(); k <= ss[1].getZ(); k++)
                    world.setBlockState(new BlockPos(i, j, k), state);
    }

    public static int arrCumSum(int[] arr) {
        int a = 0;
        for (int i : arr) a += i;
        return a;
    }

    public static int arrCumProduct(int[] arr) {
        int a = 1;
        for (int i : arr) a *= i;
        return a;
    }

    public static <Double> Range arrRange(double[] a) {
        double min = a[0], max = a[0];
        for (double i : a) {
            if (i < min) min = i;
            if (i > max) max = i;
        }
        return Range.between(min, max);
    }

    static void info(String s) {
        MCML.logger.info(s);
    }

    public static List<String> parse_option(String input, String... options) {
        List<String> l = new java.util.ArrayList<>(Arrays.asList(options));
        l.removeIf(n -> (!n.contains(input)));
        return l;
    }

    public static double[] parseDoubleArr(String str, boolean force) {
        if (force) {
            str = str.replaceAll("[^,\\-1234567890\\.]", "");
        } else {
            str = str.replace(" ", "");
            if (str.startsWith("[")) str = str.substring(1, str.length() - 1);
        }
        String[] strArgs = str.split(",");
        double[] buffer = new double[strArgs.length];
        for (int i = 0; i < strArgs.length; i++) {
            buffer[i] = Double.parseDouble(strArgs[i]);
        }
        return buffer;
    }

    public static int[] parseIntArr(String str, boolean force) {
        if (force) {
            str = str.replaceAll("[^,\\-1234567890\\.]", "");
        } else {
            str = str.replace(" ", "");
            if (str.startsWith("[")) str = str.substring(1, str.length() - 1);
        }
        String[] strArgs = str.split(",");
        int[] buffer = new int[strArgs.length];
        for (int i = 0; i < strArgs.length; i++) {
            buffer[i] = Integer.parseInt(strArgs[i]);
        }
        return buffer;
    }

    public static List<String> completeBlockName(String input) {
        List<String> list = new ArrayList<String>();
        for (Object block : Block.REGISTRY) {
            String name = Block.REGISTRY.getNameForObject((Block) block).toString();
            list.add(name);
        }
        list.removeIf(n -> (!n.contains(input)));
        return list;
    }

    public static void playerBashExecAsync(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 0) {
            playerBashExecAsync(server, sender, new String[]{"uname -a"});
            return;
        }
        String cmd = "";
        for (String s : args) cmd += s + " ";
        final String finalcmd = cmd;
        sender.sendMessage(new TextComponentString(TextFormatting.BLUE + "" + TextFormatting.BOLD + "==|EXEC: " + cmd + " |=="));
        new Thread(() -> executeBashCommand(finalcmd, sender)).start();
    }

    public static void executeBashCommand(String command, ICommandSender sender) {
        boolean success = false;
        System.out.println("Executing BASH command:\n   " + command);
        String[] commands = {"bash", "-c", command};
        try {
            Process p = Runtime.getRuntime().exec(commands);
            p.waitFor();
            BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = b.readLine()) != null) {
                System.out.println(line);
                if (sender != null) sender.sendMessage(new TextComponentString(TextFormatting.BLUE + line));
            }
            b.close();
        } catch (Exception e) {
            System.err.println("Failed to execute bash with command: " + command);
            if (sender != null) {
                sender.sendMessage(
                        new TextComponentString(TextFormatting.RED + "Failed to execute bash with command: " + command));
                sender.sendMessage(new TextComponentString(TextFormatting.RED + e.getMessage()));
            }
            e.printStackTrace();
        }
        if (sender != null)
            sender.sendMessage(new TextComponentString(TextFormatting.BLUE + "" + TextFormatting.BOLD + "==|EXEC " +
                    "END|=="));
    }

    public static String rgb2Hex(int[] rgb) {
        String color = "#";
        for (int i : rgb) {
            color += String.format("%02X", clipIntoRange(i, 0, 255));
        }
        return color.length() == 7 ? color : "#FF0000";
    }

    public static int clipIntoRange(int x, int upper, int lower) {
        return Math.min(Math.max(x, Math.min(upper, lower)), Math.max(upper, lower));
    }

    public static void main(String[] args) {
        completeBlockName("mi");
    }
}
