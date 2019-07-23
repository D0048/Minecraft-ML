package io.github.d0048.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import io.github.d0048.MCML;
import io.github.d0048.common.blocks.MLBlockBase;
import io.github.d0048.common.blocks.MLScalar;
import io.github.d0048.common.blocks.MLTensorDisplayTileEntity;
import io.github.d0048.common.items.MLWand;
import io.github.d0048.util.ColorUtil;
import io.github.d0048.util.ParticleUtil;
import io.github.d0048.util.Util;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
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
                    case "shell":
                        Util.playerBashExecAsync(server, sender, Arrays.copyOfRange(args, 1, args.length));
                        return;
                    case "info":
                        showInfo(server, sender, args, selections, world);
                        return;
                    case "display":
                        displayAction(args[1], server, sender, args, selections, world);
                        return;
                    case "canvas":
                        canvasAction(server, sender, Arrays.copyOfRange(args, 1, args.length), selections, world);
                    case "datacore":
                        MCML.mlDataCore.handleCommand(server, sender, Arrays.copyOfRange(args, 1, args.length));
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
            try {
                MLBlockBase block = (MLBlockBase) world.getBlockState(selections[0]).getBlock();
                sender.sendMessage(new TextComponentString(block.getInfoAt(world, selections[0]) + ""));
            } catch (Exception e) {
                sender.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "Color: " +
                        ColorUtil.getColorFromState(world.getBlockState(selections[0]))));
            }
        } catch (Exception e) {
            sender.sendMessage(new TextComponentString(
                    TextFormatting.RED + "Select a block with your wand first! " + e.getMessage()));
        }
    }

    public void displayAction(String action, MinecraftServer server, ICommandSender sender, String[] args, BlockPos[] selections,
                              World world) {
        try {
            EntityPlayer player = (EntityPlayer) sender;
            MLTensorDisplayTileEntity display = MLWand.mlWand.getPlayerDisplaySelection(player);
            if (action.equals("setDataID") && args.length >= 3) {
                String id = "";
                for (int i = 2; i < args.length; i++) id += " " + args[i];
                if (display.setDataID(id)) {
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
                    sender.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "Display lower edge " +
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
                sender.sendMessage(new TextComponentString(
                        TextFormatting.LIGHT_PURPLE + "Display now " + TextFormatting.YELLOW + (display.isWritable() ? "rw" : "ro")));
            } else if (action.equals("normalize")) {
                if (args.length >= 4) display.setNormalizationRange(Range.between(parseDouble(args[2]), parseDouble(args[3])));
                else display.normalize();
                sender.sendMessage(new TextComponentString(
                        TextFormatting.LIGHT_PURPLE + "Display now between" + TextFormatting.YELLOW +
                                display.getNormalizationRange()));
            }
        } catch (Exception e) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Display action failed: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    public void canvasAction(MinecraftServer server, ICommandSender sender, String[] args, BlockPos[] selections, World world) {
        try {
            info(Arrays.toString(args));
            if (args[0].equals("fill")) {
                Block b = Block.getBlockFromName(args[1]);
                int val = 0;
                if (args.length >= 3) val = parseInt(args[2]);
                IBlockState state = (b == MLScalar.mlScalar) ? MLScalar.mlScalar.getStateFromMeta(val) : b.getDefaultState();
                ParticleUtil.fillArea(selections, world, state);
            } else if (args[0].equals("ink")) {
                if (args.length >= 5) MLWand.mlWand.setPlayer2Brush((EntityPlayer) sender, new MLBrush(parseInt(args[1]),
                        parseInt(args[2]), parseInt(args[3]), parseInt(args[4])));
                sender.sendMessage(new TextComponentString(MLWand.mlWand.getPlayer2Brush((EntityPlayer) sender).toString()));
            }
        } catch (Exception e) {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "Canvas action failed: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    public void printHelp(MinecraftServer server, ICommandSender sender, String[] args) {
        sender.sendMessage(new TextComponentString(TextFormatting.RED + "Incorrect Usage!"));
        sender.sendMessage(new TextComponentString(getUsage(sender)));
    }


    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
                                          @Nullable BlockPos targetPos) {
        switch (args.length) {
            case 1:
                return Util.parse_option(args[0], "info", "display", "datacore", "canvas", "shell");
            case 2:
                switch (args[0]) {
                    case "display":
                        return Util.parse_option(args[1], "setDataID", "reshape", "reroot", "relocate", "normalize", "toggleWrite");
                    case "datacore":
                        return MCML.mlDataCore.parse_option(args[1]);
                    case "canvas":
                        return Util.parse_option(args[1], "fill", "ink");
                }
            case 3:
                switch (args[1]) {
                    case "fill":
                        return Util.completeBlockName(args[2]);
                }
            default:
                break;
        }
        return Collections.<String>emptyList();

    }

    @Override
    public String getUsage(ICommandSender sender) {
        String ret = "\n";
        ret += TextFormatting.YELLOW + "info" + TextFormatting.LIGHT_PURPLE + ": display what's under your 1st wand selection\n";

        ret += TextFormatting.YELLOW + "display" + TextFormatting.LIGHT_PURPLE + ": \n";
        ret += "    " + TextFormatting.YELLOW + "setDataID <id>" + TextFormatting.LIGHT_PURPLE + ": give display a MCML-Lisp " +
                "expression for what to display\n";
        ret += "    " + TextFormatting.YELLOW + "reshape <x> <y> <z>" + TextFormatting.LIGHT_PURPLE + ": reshape display from lowest" +
                " end\n";
        ret += "    " + TextFormatting.YELLOW + "reroot" + TextFormatting.LIGHT_PURPLE +
                ": move display lower edge to 2nd selection\n";
        ret += "    " + TextFormatting.YELLOW + "relocate" + TextFormatting.LIGHT_PURPLE + ": reshape + reroot display between " +
                "selection 1 & 2\n";
        ret += "    " + TextFormatting.YELLOW + "normalize <optional_lower> <optional_upper>" + TextFormatting.LIGHT_PURPLE + ": " +
                "normalize display into given values, automatically choose range if not given\n";
        ret += "    " + TextFormatting.YELLOW + "toggleWrite" + TextFormatting.LIGHT_PURPLE + ": " +
                "Toggle whether you can write to the display with your wand/hand\n";

        ret += TextFormatting.YELLOW + "datacore" + TextFormatting.LIGHT_PURPLE + ": (commands may differ depending on which core " +
                "you use)\n";
        ret += MCML.mlDataCore.getUsage(sender).replace("\n","\n    ")+"\n";

        ret += TextFormatting.YELLOW + "canvas" + TextFormatting.LIGHT_PURPLE + ": \n";
        ret += "    " + TextFormatting.YELLOW + "fill <block_type> <optional_value>" + TextFormatting.LIGHT_PURPLE + ": fill the " +
                "volume between both wand selection with block. e.g /canvas fill minecraft_ml:ml_scalar 0 for a white canvas\n";
        ret += "    " + TextFormatting.YELLOW + "ink <R~[0,255]> <G~[0,255]> <B~[0,255]> <Radius>" + TextFormatting.LIGHT_PURPLE +
                ": set color & radius the current stylus is using(for painting with right click) \n";

        ret += TextFormatting.YELLOW + "shell <cmd>" + TextFormatting.LIGHT_PURPLE + ": execute a bash command(*nix only function)\n";

        return ret;
    }

    @Override
    public String getName() {
        return "wand";
    }

    static void info(String s) {
        MCML.logger.info(s);
    }

}
