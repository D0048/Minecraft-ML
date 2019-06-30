package io.github.d0048.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import io.github.d0048.common.items.MLWand;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class MLWandCommand extends CommandBase {
	public static MLWandCommand commandStylus;

	public MLWandCommand() {
	}

	@Override
	public String getName() {
		return "wand";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return null;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (sender instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) sender;
			World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(player.dimension);

			BlockPos[] selections = MLWand.mlWand.getPlayerSelection(player);
			if (args.length == 1) {
				switch (args[0]) {
				case "info":
					if (selections != null) {
						player.sendMessage(new TextComponentString(world.getTileEntity(selections[0]) + ""));
					} else {
						player.sendMessage(new TextComponentString(
								TextFormatting.RED + "Selection something with your wand first!"));
					}
					return;
				}
			}

		}
		printHelp(server, sender, args);
	}

	public void printHelp(MinecraftServer server, ICommandSender sender, String[] args) {
		sender.sendMessage(new TextComponentString(TextFormatting.RED + "Incorrect Usage"));
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			@Nullable BlockPos targetPos) {
		switch (args.length) {
		case 1:
			return Arrays.asList("info", "setDataID", "reshape");
		default:
			break;
		}
		return Collections.<String>emptyList();
	}

}
