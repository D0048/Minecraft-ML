package io.github.d0048.databackend.datacore_mcml;

import io.github.d0048.MCML;
import io.github.d0048.databackend.MLDataCore;
import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.databackend.datacore_mcml.mcmlisp.Evaluater;
import io.github.d0048.databackend.datacore_mcml.mcmlisp.Molecule;
import io.github.d0048.databackend.datacore_mcml.mcmlisp.Parser;
import io.github.d0048.util.Util;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.Sys;

import javax.naming.OperationNotSupportedException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class MLDataCoreMCML extends MLDataCore {
    ConcurrentHashMap<String, MLDataWrap> dataMap = new ConcurrentHashMap<String, MLDataWrap>();
    Thread backend;
    public static Logger logger = Logger.getLogger("MCMLisp");

    public MLDataCoreMCML() {
        super(BackEndType.MCML);
        Evaluater.init();
        (backend = new Thread(() -> backendThread())).start();
    }

    public void backendThread() {
        info("MCML Backend is now up and running!");
        try {
            while (true) {
                Set<String> ids = dataMap.keySet();
                for (String id : ids) {
                    Molecule m = null;
                    try {
                        m = Parser.parse(id);
                        MLDataWrap dataWrap = m.evaluate();
                        dataMap.put(id, dataWrap);
                    } catch (Exception e) {
                        //dataMap.remove(id);
                        info("Error updating data ID: " + id + " because " + e.getMessage());
                        info(m + "");
                        e.printStackTrace();
                    }
                }
                Thread.sleep(1200);
            }
        } catch (Exception e) {
            info("MCML Backend experience a problem: ");
            e.printStackTrace();
        }
    }

    /**
     * ID Rule: name@[optional_shape]|type
     * e.g x.png|file or (+ x 1)|eval or (x@[28,28,3])|eval
     * types supported: eval,file
     **/
    public void decodeID(String id) throws Exception {
        try {
            String[] washedID = washID2Internal(id);
            if (washedID[2].equals("eval")) {// Default to eval OR or eval type
                if (test4Const(washedID[0]) != null) {
                    System.out.println("No need to register constant: " + washedID[0]);
                    return;// Constant, no need to register
                }
                if (Parser.parse(washedID[0]).isAtom()) {
                    info("atom");
                    dataMap.put(washedID[0], MLDataWrap.fromStringShape(washedID[1]));
                } else {
                    info("non atom");
                    dataMap.put(washedID[0],  MLDataWrap.fromStringShape(washedID[1])); // non-atom, evaluate on the go
                }
            } else if (washedID[2].equals("file"))
                throw new OperationNotSupportedException("Loading from file TODO");
        } catch (Exception e) {
            info("Error decoding ID: " + id + " due to " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public MLDataWrap registerDataForID(String id) throws Exception {
        decodeID(id);
        return getDataForID(id);
    }

    @Override
    public MLDataWrap getDataForID(String id) {
        String[] washedID = washID2Internal(id);
        MLDataWrap ret = test4Const(washedID[0]);
        return ret == null ? dataMap.get(washedID[0]) : ret;
    }

    private String[] washID2Internal(String id) {//[name,shape,type]
        String[] ret = new String[3], typeSplit = id.split("\\|"), shapeSplit = typeSplit[0].split("@");
        ret[0] = shapeSplit[0].trim();// name/expression
        if (ret[0].startsWith("(")) ret[0] = ret[0].substring(1, ret[0].length() - 1);
        ret[1] = shapeSplit.length > 1 ? shapeSplit[1].trim() : "[1]";//shape
        ret[2] = typeSplit.length > 1 ? typeSplit[1].trim() : "eval";//type
        return ret;
    }

    public MLDataWrap test4Const(String constantString) {
        try {//check is constant
            return MLDataWrap.sameValue(1, Double.parseDouble(constantString.trim()));
        } catch (Exception e) {
        }
        try {//check is constant
            return MLDataWrap.fromStringValue(constantString, false);
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public void unregisterID(String id) {
        dataMap.remove(id);
    }

    @Override
    public void handleCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) return;
        switch (args[0]) {
            case "inquire":
                sender.sendMessage(new TextComponentString(toString()));
                break;
        }
    }

    public List<String> parse_option(String arg) {
        return Util.parse_option(arg, "inquire", "enable", "disable");
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
        //logger.info(s);
        System.out.println(s);
    }
}