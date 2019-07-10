package io.github.d0048.databackend.datacore_mcml;

import io.github.d0048.MCML;
import io.github.d0048.databackend.MLDataCore;
import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.databackend.datacore_mcml.mcmlisp.Parser;
import io.github.d0048.util.Util;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;

import javax.naming.OperationNotSupportedException;
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

    /**
     * ID Rule: name@[optional_shape]|type
     * e.g x.png|file or (+ x 1)|eval or (x@[28,28,3])|eval
     * types supported: eval,file
     **/
    public void decodeID(String id) throws Exception {
        //try {
            String[] ids = id.trim().split("\\|");
            if (ids.length == 1 || ids[1].equals("eval")) {// Default to eval OR or eval type
                if (test4Const(id) != null) return;// Constant, no need to register
                String[] dataMeta = ids[0].split("@");
                info("parse molecule");
                if (Parser.parse(ids[0]).isAtom()) {
                    info("atom");
                    dataMap.put(ids[0], MLDataWrap.fromStringShape(dataMeta[1]));
                } else {
                    info("non atom");
                    dataMap.put(ids[0], MLDataWrap.whiteData(1)); // non-atom, evaluate on the go
                }
            } else if (ids[1].equals("file"))
                throw new OperationNotSupportedException("Loading from file TODO");
        //} catch (Exception e) {
         //   info("Error decoding ID: " + id + " due to " + e.getMessage());
         //   e.printStackTrace();
          //  throw e;
        //}
    }

    @Override
    public MLDataWrap registerDataForID(String id) throws Exception {
        decodeID(id);
        return getDataForID(id);
    }

    @Override
    public MLDataWrap getDataForID(String id) {
        String[] ids = id.trim().split("\\|");
        MLDataWrap ret = test4Const(id);
        return ret == null ? dataMap.get(ids[0]) : ret;
    }

    public MLDataWrap test4Const(String id) {
        String[] ids = id.split("\\|");
        id = ids[0];
        ids = id.split("@");
        if (ids.length == 2) { // name@[shape]
            id = ids[1];
            return MLDataWrap.fromStringShape(id);
        }
        id = ids[ids.length - 1];
        try {//check is constant
            return MLDataWrap.sameValue(1, Double.parseDouble(id.trim()));
        } catch (Exception e) {
        }
        try {//check is constant
            return MLDataWrap.fromStringValue(id);
        } catch (Exception e) {
        }
        return null;
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