package io.github.d0048.databackend.datacore_mcml;

import io.github.d0048.MCML;
import io.github.d0048.databackend.MLDataCore;
import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.util.Util;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class MLDataCoreMCML extends MLDataCore {
    HashMap<String, MLDataWrap> dataMap = new HashMap<String, MLDataWrap>();
    Thread backend;
    public static Logger logger = Logger.getLogger("MCMLisp");

    public MLDataCoreMCML() {
        super(BackEndType.MCML);
        (backend = new Thread(() -> backendThread())).start();
    }

    public void backendThread() {
        info("MCML Backend is now up and running!");
        try {

            Thread.sleep(200);
        } catch (Exception e) {
            info("MCML Backend experience a problem: ");
            e.printStackTrace();
        }
    }


    @Override
    public MLDataWrap registerDataForID(String id) {
        id = id.trim();
        try {
            Double.parseDouble(id.trim());
        } catch (Exception e) {
        }
        try {
            MLDataWrap.fromString(id);
        } catch (Exception e) {
        }
        if (!dataMap.containsKey(id))
            dataMap.put(id, MLDataWrap.whiteData(1000));
        return getDataForID(id);
    }

    @Override
    public MLDataWrap getDataForID(String id) {
        id = id.trim();
        try {
            return MLDataWrap.sameValue(1, Double.parseDouble(id.trim()));
        } catch (Exception e) {
        }
        try {
            return MLDataWrap.fromString(id);
        } catch (Exception e) {
        }
        return dataMap.get(id);
    }


    @Override
    public void handleCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

    }

    public List<String> parse_option(String arg) {
        return Util.parse_option(arg, "loadModel", "inquire", "enable", "disable");
    }

    public String getUsage(ICommandSender sender) {
        return "MCML Core Usage : \n";
    }

    @Override
    public String toString() {
        String ret = TextFormatting.LIGHT_PURPLE + "MCML Datacore " + TextFormatting.LIGHT_PURPLE + MCML.VERSION +
                TextFormatting.LIGHT_PURPLE + " :\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - Status: " + TextFormatting.YELLOW + backend.getState().toString()
                + TextFormatting.LIGHT_PURPLE + "\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - Data: " + TextFormatting.YELLOW + dataMap.toString()
                + TextFormatting.LIGHT_PURPLE + "\n";
        return ret;
    }

    @Override
    public MLDataWrap writeDataForID(String id) {
        /*id = id.trim();
        if (!backend.isAlive()) (backend = new Thread(() -> backendThread())).start();*/
        return null;
    }

    static void info(String s) {
        logger.info(s);
    }
}