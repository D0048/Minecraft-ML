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
                    case "setDataID":
                    case "reshape":
                        displayAction(args[0], server, sender, args, selections, world);
                        return;
                    default:
                        break;
                }
            }
        }
        printHelp(server, sender, args);
    }

    public void showInfo(MinecraftServer server, ICommandSender sender, String[] args, BlockPos[] selections, World world) {
        MLTensorDisplayTileEntity display = MLWand.mlWand.getPlayerDisplaySelection((EntityPlayer) sender);
        if (display == null)
            sender.sendMessage(new TextComponentString(
                    TextFormatting.RED + "Select a MCML block with your wand first!"));
        else {
            sender.sendMessage(new TextComponentString(display + ""));
        }
    }

    public void displayAction(String action, MinecraftServer server, ICommandSender sender, String[] args, BlockPos[] selections,
                              World world) {
        try {
            EntityPlayer player = (EntityPlayer) sender;
            MLTensorDisplayTileEntity display = MLWand.mlWand.getPlayerDisplaySelection(player);
            if (action.equals("setDataID") && args.length >= 2) {
                if (display.setDataID(args[1])) {
                    sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Success!"));
                } else {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Fail, maybe id corresponds to no Data?"));
                }
            } else if (action.equals("reshape")) {
                selections = Util.sortEdges(selections[0], selections[1]);
                BlockPos shapePos = args.length >= 4 ?
                        parseBlockPos(sender, args, 1, false)
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
                return Arrays.asList("info", "setDataID", "reshape", "relocate", "canvas");
            default:
                break;
        }
        return Collections.<String>emptyList();
    }

    @Override
    public String getName() {
        return "wand";
    }

    static void info(String s) {
        MCML.logger.info(s);
    }

}
