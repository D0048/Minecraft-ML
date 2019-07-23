package io.github.d0048.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

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

    public static int arrCumSum(int[] arr) {
        int a = 0;
        for (int i : arr) a += i;
        return a;
    }

    public static int[] arrCumDiff(int[] arr1, int[] arr2) {
        int[] ret = arr1.clone();
        for (int i = 0; i < ret.length; i++) ret[i] -= arr2[i];
        return ret;
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
            str = str.replace(" ", "").replace("[", "").replace("]", "");
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
        return double2IntArray(parseDoubleArr(str, force));
        /*
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
        return buffer;*/
    }

    /**
     * Parse shape of matrix, e.g: [[1,0,0][0,1,0][0,0,1]]
     *
     * @param str
     * @return
     */
    public static int[] parseArrShape(String str) {
        if (str.startsWith("[")) str = str.substring(1, str.length() - 1);
        //str = str.replaceAll("[^,\\]\\[]", "");
        if (!str.contains("[")) {
            //System.out.println("shape of " + str + " :" + (str.split(",").length));
            return new int[]{str.split(",").length};
        }
        int partCount = 0, cutpos = 0;
        Stack<Character> stack = new Stack<Character>();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '[') {
                stack.push(c);
            } else if (c == ']') {
                stack.pop();
                if (stack.empty()) {
                    partCount++;
                    cutpos = cutpos == 0 ? i : cutpos;
                }
            }
        }
        int[] subShape = parseArrShape(str.substring(0, cutpos + 1));
        int[] shape = new int[subShape.length + 1];
        System.arraycopy(subShape, 0, shape, 1, subShape.length);
        shape[0] = partCount;
        //System.out.println("shape of " + str + " :" +Arrays.toString(shape));
        return shape;
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

    public static int[] double2IntArray(double[] arr) {
        int[] buffer = new int[arr.length];
        for (int i = 0; i < arr.length; i++) buffer[i] = (int) arr[i];
        return buffer;
    }

    public static double[] int2DoubleArray(int[] arr) {
        double[] buffer = new double[arr.length];
        for (int i = 0; i < arr.length; i++) buffer[i] = arr[i];
        return buffer;
    }

    public static void main(String[] args) {
        completeBlockName("mi");
    }
}
