package io.github.d0048.common;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class RunToCommand extends CommandBase {
    public static RunToCommand runToCommand;

    @Override
    public String getName() {
        return "runto";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return TextFormatting.LIGHT_PURPLE + "Fast forward to where your eyes point to.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            BlockPos pointedPos = player.rayTrace(400, 0f).getBlockPos();
            player.attemptTeleport(pointedPos.getX(), pointedPos.getY()+1, pointedPos.getZ());
        }
    }
}
