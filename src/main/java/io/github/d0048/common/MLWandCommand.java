package io.github.d0048.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import io.github.d0048.common.blocks.MLBlockBase;
import io.github.d0048.common.blocks.MLTensorDisplayTileEntity;
import io.github.d0048.common.items.MLWand;
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
                        if (args.length >= 2) {
                            setDataID(server, sender, args, selections, world);
                            return;
                        }
                }
            }
        }
        printHelp(server, sender, args);
    }

    public void showInfo(MinecraftServer server, ICommandSender sender, String[] args, BlockPos[] selections, World world) {
        Block blk;
        if (selections == null || !((blk = world.getBlockState(selections[0]).getBlock()) instanceof MLBlockBase))
            sender.sendMessage(new TextComponentString(
                    TextFormatting.RED + "Select a MCML block with your wand first!"));
        else {
            sender.sendMessage(new TextComponentString(((MLBlockBase) blk).getInfoAt(world, selections[0])));
        }
    }

    public void setDataID(MinecraftServer server, ICommandSender sender, String[] args, BlockPos[] selections, World world) {
        TileEntity display;
        if (selections == null || (display = world.getTileEntity(selections[0])) == null
                || !(display instanceof MLTensorDisplayTileEntity))
            sender.sendMessage(new TextComponentString(
                    TextFormatting.RED + "Select a Display with your wand first!"));
        else {
            if (((MLTensorDisplayTileEntity) display).setDataID(args[1])) {
                sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Success!"));
            } else {
                sender.sendMessage(new TextComponentString(TextFormatting.RED + "Fail, maybe id corresponds to no Data?"));
            }
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
                return Arrays.asList("info", "setDataID", "reshape", "relocate");
            default:
                break;
        }
        return Collections.<String>emptyList();
    }

    @Override
    public String getName() {
        return "wand";
    }

}
