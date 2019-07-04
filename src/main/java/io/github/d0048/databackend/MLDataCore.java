package io.github.d0048.databackend;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

public abstract class MLDataCore {
    public enum BackEndType {
        TF, MCML
    }

    ;

    BackEndType backend;

    public MLDataCore(BackEndType b) {
        backend = b;
    }

    abstract public MLDataWrap registerDataForID(String id); // Register what to read/write

    abstract public MLDataWrap getDataForID(String id); // Read

    abstract public MLDataWrap writeDataForID(String id);// Write

    abstract public void handleCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException;

    public static float[] toFloatArray(double[] arr) {
        if (arr == null) return null;
        int n = arr.length;
        float[] ret = new float[n];
        for (int i = 0; i < n; i++) {
            ret[i] = (float) arr[i];
        }
        return ret;
    }

    abstract public List<String> parse_option(String arg);

    public static void copyToDoubleArray(float[] farr, double[] darr) {
        if (farr == null || darr == null) return;
        int n = Math.min(farr.length, darr.length);
        for (int i = 0; i < n; i++) {
            darr[i] = farr[i];
        }
    }

}
