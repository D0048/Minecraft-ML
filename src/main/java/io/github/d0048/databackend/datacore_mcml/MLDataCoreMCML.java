package io.github.d0048.databackend.datacore_mcml;

import io.github.d0048.MCML;
import io.github.d0048.MLConfig;
import io.github.d0048.databackend.MLDataCore;
import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.databackend.datacore_mcml.mcmlisp.Evaluater;
import io.github.d0048.databackend.datacore_mcml.mcmlisp.Molecule;
import io.github.d0048.databackend.datacore_mcml.mcmlisp.Parser;
import io.github.d0048.databackend.datacore_mcml.mcmlisp.ops.OPBase;
import io.github.d0048.util.Util;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.Sys;

import javax.naming.OperationNotSupportedException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class MLDataCoreMCML extends MLDataCore {
    ConcurrentHashMap<String, MLDataWrap> dataMap = new ConcurrentHashMap<String, MLDataWrap>();
    ConcurrentHashMap<String, String> aliasMap = new ConcurrentHashMap<String, String>();
    Thread backend;
    public static Logger logger = Logger.getLogger("MCMLisp");

    public MLDataCoreMCML() {
        super(BackEndType.MCML);
        Evaluater.init();
        (backend = new Thread(() -> backendThread())).start();
    }

    long lastRunTimeMs = 0;

    public void backendThread() {
        if (MCML.logger == null) return;
        info("MCML Backend is now up and running!");
        try {
            while (true) {
                Thread.sleep(MLConfig.backendUpdateInterval);
                long timeStart = System.currentTimeMillis();
                Set<String> ids = dataMap.keySet();
                for (String id : ids) {
                    Molecule m = null;
                    try {
                        m = Parser.parse(id);
                        MLDataWrap dataWrap = m.evaluate();
                        dataMap.put(id, dataWrap);
                    } catch (Throwable e) {
                        //dataMap.remove(id);
                        info("Error updating data ID: " + id + " because " + e + ": " + e.getMessage());
                        //e.printStackTrace();
                    }
                }
                lastRunTimeMs = System.currentTimeMillis() - timeStart;
            }
        } catch (Throwable e) {
            info("MCML Backend experience a problem: " + e + ": " + e.getMessage());
            //e.printStackTrace();
        }
    }

    /**
     * ID Rule: [optional_alias_name]=name@[optional_shape]|type
     * e.g (x@[28,28,3])|eval or a=b
     * types supported: eval,file
     **/
    public void decodeID(String id) throws Exception {
        try {
            String[] washedID = washID2Internal(id);//[name,shape,type,alias]
            System.out.println("Registering: " + Arrays.toString(washedID));
            if (!washedID[3].equals("")) {// define an alias with =
                aliasMap.put(washedID[3], washedID[0]);
            }
            if (dataMap.keySet().contains(washedID[0])) {
                System.out.println("This id is already registered, remove first: " + washedID[0] + " | " + washedID[3]);
                return;
            }
            if (washedID[2].equals("eval")) {// Default to eval
                if (test4Const(washedID[0]) != null) {
                    System.out.println("No need to register constant: " + washedID[0]);
                    return;// Constant, no need to register
                }
                try {
                    Molecule m = Parser.parse(washedID[0]);
                    MLDataWrap dataWrap = m.evaluate();
                    if (dataWrap == null) throw new Exception();
                    dataMap.put(washedID[0], dataWrap);
                } catch (Throwable e) {
                    dataMap.put(washedID[0], MLDataWrap.fromStringShape(washedID[1], true));
                }
            } else
                throw new OperationNotSupportedException("Other data types TODO");
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
        String internalID = washID2Internal(id)[0];
        //System.out.println("get " + internalID);
        while (aliasMap.get(internalID) != null) {
            //System.out.println("Internal Lookup: " + internalID + " -> " + aliasMap.get(internalID));
            internalID = aliasMap.get(internalID);
        }
        MLDataWrap ret = test4Const(internalID);
        return ret == null ? dataMap.get(internalID) : ret;
    }

    @Override
    public void unregisterID(String id) {
        String[] washedID = washID2Internal(id);
        dataMap.remove(washedID[0]);
        aliasMap.remove(washedID[0]);
        aliasMap.remove(washedID[3]);
    }

    private String[] washID2Internal(String id) {//[name,shape,type]
        String[] ret = new String[4];
        String[] typeSplit = id.split("\\|"), shapeSplit = typeSplit[0].split("@"), aliasSplit =
                shapeSplit[0].split("=");
        ret[3] = aliasSplit.length > 1 ? aliasSplit[0].trim() : "";// alias name
        ret[0] = aliasSplit.length > 1 ? aliasSplit[1].trim() : aliasSplit[0].trim();// name/expression
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
        String ret = TextFormatting.LIGHT_PURPLE + "MCML Core Usage : \n";
        ret += TextFormatting.YELLOW + "inquire" + TextFormatting.LIGHT_PURPLE + ": get the current status information of Datacore\n";
        ret += TextFormatting.LIGHT_PURPLE + "Available OPs: \n";
        for (OPBase op : Evaluater.opMap.values()) {
            ret += "    " + TextFormatting.LIGHT_PURPLE + op.getUsage() + "\n";
        }
        return ret;
    }

    @Override
    public String toString() {
        String ret = TextFormatting.LIGHT_PURPLE + "MCML Datacore " + TextFormatting.LIGHT_PURPLE + MCML.VERSION +
                TextFormatting.LIGHT_PURPLE + " :\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - Status: " + TextFormatting.YELLOW + backend.getState().toString()
                + TextFormatting.LIGHT_PURPLE + "\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - Data: " + dataMap.toString()
                + TextFormatting.LIGHT_PURPLE + "\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - Alias: " + TextFormatting.YELLOW + aliasMap.toString()
                + TextFormatting.LIGHT_PURPLE + "\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - OPs Avail: ";
        for (String s : Evaluater.opMap.keySet()) ret += TextFormatting.YELLOW + s + TextFormatting.LIGHT_PURPLE + ", ";
        ret += TextFormatting.LIGHT_PURPLE + "\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - Last run took: " + TextFormatting.YELLOW + lastRunTimeMs + " ms"
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