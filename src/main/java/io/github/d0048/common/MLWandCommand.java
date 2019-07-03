package io.github.d0048.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import io.github.d0048.MCML;
import io.github.d0048.common.blocks.MLBlockBase;
import io.github.d0048.common.blocks.MLTensorDisplayTileEntity;
import io.github.d0048.common.items.MLWand;
import io.github.d0048.util.Util;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.block.Block;

import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.lang3.Range;

public class MLWandCommand extends CommandBase {
    public static MLWandCommand commandStylus;

    public MLWandCommand() {
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(player.dimension);
            BlockPos[] selections = MLWand.mlWand.getPlayerSelection(player);
            if (args.length >= 1) {
                switch (args[0]) {
                    case "info":
                        showInfo(server, sender, args, selections, world);
                        return;
                    case "display":
                        displayAction(args[1], server, sender, args, selections, world);
                        return;
                    default:
                        break;
                }
            }
        }
        printHelp(server, sender, args);
    }

    public void showInfo(MinecraftServer server, ICommandSender sender, String[] args, BlockPos[] selections, World world) {
        try {
            MLBlockBase block = (MLBlockBase) world.getBlockState(selections[0]).getBlock();
            sender.sendMessage(new TextComponentString(block.getInfoAt(world, selections[0])));
        } catch (Exception e) {
            sender.sendMessage(new TextComponentString(
                    TextFormatting.RED + "Select a MCML block with your wand first! " + e.getMessage()));
        }

    }

    public void displayAction(String action, MinecraftServer server, ICommandSender sender, String[] args, BlockPos[] selections,
                              World world) {
        try {
            EntityPlayer player = (EntityPlayer) sender;
            MLTensorDisplayTileEntity display = MLWand.mlWand.getPlayerDisplaySelection(player);
            if (action.equals("setDataID") && args.length >= 3) {
                if (display.setDataID(args[2])) {
                    sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Success!"));
                } else {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Fail, maybe id corresponds to no Data?"));
                }
            } else if (action.equals("reshape")) {
                if (selections != null)
                    selections = Util.sortEdges(selections[0], selections[1]);
                BlockPos shapePos = args.length >= 5 ?
                        parseBlockPos(sender, args, 2, false)
                        : selections[1].subtract(selections[0]).add(1, 1, 1);
                int[] shape = new int[]{Math.max(0, shapePos.getX()), Math.max(0, shapePos.getY()), Math.max(0, shapePos.getZ())};
                if (display.setDisplayShape(shape)) {
                    sender.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "Display shape" +
                            TextFormatting.YELLOW + Arrays.toString(shape) + TextFormatting.LIGHT_PURPLE + " set!"));
                } else {
                    sender.sendMessage(new TextComponentString(
                            TextFormatting.RED + "Rejected as shape " + TextFormatting.YELLOW + Arrays.toString(shape) +
                                    TextFormatting.RED + "is invalid"));
                }
            } else if (action.equals("reroot")) {
                BlockPos pos = args.length >= 5 ?
                        parseBlockPos(sender, args, 2, false)
                        : selections[1];
                if (display.reroot(pos)) {
                    sender.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "Display lower edge" +
                            TextFormatting.YELLOW + pos + TextFormatting.LIGHT_PURPLE + " set!"));
                } else {
                    sender.sendMessage(new TextComponentString(
                            TextFormatting.RED + "Failed to reroot as " + TextFormatting.YELLOW + pos +
                                    TextFormatting.RED + "is invalid"));
                }
            } else if (action.equals("relocate")) {//Both reshape and reroot
                if (selections != null)
                    selections = Util.sortEdges(selections[0], selections[1]);
                BlockPos shapePos = selections[1].subtract(selections[0]).add(1, 1, 1);
                int[] shape = new int[]{Math.max(0, shapePos.getX()), Math.max(0, shapePos.getY()), Math.max(0, shapePos.getZ())};
                if (display.setDisplayShape(shape) && display.reroot(selections[0])) {
                    sender.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "Relocate into" +
                            TextFormatting.YELLOW + Arrays.toString(shape) + TextFormatting.LIGHT_PURPLE + " at " +
                            TextFormatting.YELLOW + selections[0]));
                } else {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Could not relocate into" +
                            TextFormatting.YELLOW + Arrays.toString(shape) + TextFormatting.RED + " at " +
                            TextFormatting.YELLOW + selections[0]));

                }
            } else if (action.equals("toggleWrite")) {
                if (args.length >= 3) display.setWritable(parseBoolean(args[2]));
                else display.toggleWritable();
            } else if (action.equals("normalize")) {
                if (args.length >= 4) display.setNormalizationRange(Range.between(parseDouble(args[2]), parseDouble(args[3])));
                else display.normalize();
            }
        } catch (Exception e) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Display action failed: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    public void printHelp(MinecraftServer server, ICommandSender sender, String[] args) {
        sender.sendMessage(new TextComponentString(TextFormatting.RED + "Incorrect Usage!"));
        sender.sendMessage(new TextComponentString(getUsage(sender)));
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
                                          @Nullable BlockPos targetPos) {
        switch (args.length) {
            case 1:
                return parse_option(args[0], "info", "display", "canvas");
            case 2:
                return parse_option(args[1], "setDataID", "reshape", "reroot", "relocate", "normalize", "toggleWrite");
            default:
                break;
        }
        return Collections.<String>emptyList();
    }

    @Override
    public String getName() {
        return "wand";
    }

    static List<String> parse_option(String input, String... options) {
        List<String> l = new java.util.ArrayList<>(Arrays.asList(options));
        l.removeIf(n -> (!n.contains(input)));
        return l;
    }

    static void info(String s) {
        MCML.logger.info(s);
    }

}
