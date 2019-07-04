package io.github.d0048.databackend.datacore_tf;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import io.github.d0048.MCML;
import io.github.d0048.databackend.MLDataCore;
import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.util.Util;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;

public class MLDataCoreTF extends MLDataCore {
    // Graph graph;
    // Session session;
    HashMap<String, MLDataWrap> feedDataMap = new HashMap<String, MLDataWrap>();
    HashMap<String, MLDataWrap> fetchDataMap = new HashMap<String, MLDataWrap>();
    HashMap<String, MLDataWrap> otherDataMap = new HashMap<String, MLDataWrap>();
    boolean isActive = true;

    String modelDir =
            "/home/d0048/Desktop/Program-code/Minecraft Mode Dev/Minecraft-ML/src/main/java/io/github/d0048/databackend/datacore_tf/model";
    SavedModelBundle savedModelBundle = SavedModelBundle
            .load(modelDir,
                    "serve");
    Thread backend;

    public MLDataCoreTF() {
        super(MLDataCore.BackEndType.TF);
        info("Loading Tensorflow ML Backend: TF " + TensorFlow.version());
        backend = new Thread(() -> backendThread());
    }

    Session s;

    public void backendThread() {
        MLDataWrap input, output;
        try {
            if (savedModelBundle != null && !feedDataMap.isEmpty() && !fetchDataMap.isEmpty()) {
                try {
                    if (s == null) s = savedModelBundle.session();
                    Session.Runner runner = s.runner();
                    HashMap<String, Tensor> tensorsFeed = new HashMap<String, Tensor>();
                    for (String s : feedDataMap.keySet()) {
                        MLDataWrap feedData = feedDataMap.get(s);
                        tensorsFeed.put(s, Tensor.create(new long[]{feedData.getData().length},
                                FloatBuffer.wrap(toFloatArray(feedData.getData()))));
                        runner.feed(s, tensorsFeed.get(s));
                    }

                    List<String> toFetch = new ArrayList<>(fetchDataMap.keySet());
                    for (String s : toFetch) {
                        runner.fetch(s);
                    }
                    List<Tensor<?>> results = runner.run();

                    for (int i = 0; i < results.size(); i++) {
                        MLDataWrap dataWrap = fetchDataMap.get(toFetch.get(i));
                        Tensor<?> tensor = results.get(i);
                        float[][] floatTmp = new float[1][dataWrap.getData().length];
                        tensor.copyTo(floatTmp);
                        copyToDoubleArray(floatTmp[0], dataWrap.getData());
                    }
                    tensorsFeed.values().forEach((t) -> t.close());
                    //s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> parse_option(String arg) {
        return Util.parse_option(arg, "loadModel", "inquire", "enable", "disable");
    }

    @Override
    public void handleCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        info(Arrays.toString(args));
        if (args.length == 0) return;
        switch (args[0]) {
            case "enable":
                setActive(true);
                break;
            case "disable":
                setActive(false);
                break;
            case "inquire":
                sender.sendMessage(new TextComponentString(toString()));
                break;
            case "loadModel":
                String dir = "";
                try {
                    for (int i = 1; i < args[0].length(); i++) {
                        dir += args[i];
                    }
                    setModelDir(dir);
                    sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Loaded " + TextFormatting.YELLOW + dir));
                } catch (Exception e) {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Failed on " + TextFormatting.YELLOW + dir));
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + e.getMessage()));
                    e.printStackTrace();
                }
                break;
        }
    }


    /**
     * Format used: name|[feed/fetch/other]|length
     * e.g: conv_y|fetch|784
     */
    @Override
    public MLDataWrap registerDataForID(String id_whole) {
        String[] id;
        int len;
        try {
            id = id_whole.split("\\|");
            len = Integer.parseInt(id[2]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        info(Arrays.toString(id));
        switch (id[1]) {
            case "feed":
                feedDataMap.put(id[0], new MLDataWrap(new int[]{len}, whiteData(len)));
                break;
            case "fetch":
                fetchDataMap.put(id[0], new MLDataWrap(new int[]{len}, whiteData(len)));
                break;
            default:
                otherDataMap.put(id[0], new MLDataWrap(new int[]{len}, whiteData(len)));
                break;
        }
        return getDataForID(id_whole);
    }

    @Override
    public MLDataWrap getDataForID(String id_whole) {
        String[] id;
        int len;
        try {
            id = id_whole.split("\\|");
            len = Integer.parseInt(id[2]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        switch (id[1]) {
            case "feed":
                return feedDataMap.get(id[0]);
            case "fetch":
                return fetchDataMap.get(id[0]);
            default:
                return otherDataMap.get(id[0]);
        }
    }

    @Override//currently no used it seems
    public MLDataWrap writeDataForID(String id) {
        flush();
        return null;
    }

    public void flush() {
        if (isActive && !backend.isAlive()) (backend = new Thread(() -> backendThread())).start();
    }

    public void setActive(boolean active) {
        isActive = active;
        flush();
    }

    public boolean setModelDir(String dir) {
        this.modelDir = dir;
        savedModelBundle = SavedModelBundle.load(modelDir, "serve");
        return true;
    }

    static double[] whiteData(int size) {
        double[] datatmp = new double[size];
        for (int i = 0; i < datatmp.length; i++) {
            datatmp[i] = 0;
        }
        return datatmp;
    }

    // Tensorflow test
    public static void main(String[] args) {
        MLDataCoreTF core = new MLDataCoreTF();
        core.registerDataForID("x|feed|784");
        core.registerDataForID("keep_prob|feed|1");
        core.registerDataForID("y_conv|fetch|10");
        core.writeDataForID("x|feed|728");
        if (true) return;
        try (SavedModelBundle b = SavedModelBundle
                .load("/home/d0048/Desktop/Program-code/Minecraft Mode " +
                                "Dev/Minecraft-ML/src/main/java/io/github/d0048/databackend/datacore_tf/model",
                        "serve")) {
            Session s = b.session();
            FloatBuffer fb = FloatBuffer.allocate(784);
            double[] input = whiteData(784);
            for (double i : input) {
                fb.put((float) i);
            }
            fb.rewind();

            float[] keep_prob_array = new float[1];
            Arrays.fill(keep_prob_array, 1f);

            Tensor x = Tensor.create(new long[]{784}, fb);
            Tensor keep_prob = Tensor.create(new long[]{1, 1}, FloatBuffer.wrap(keep_prob_array));

            double[] output = whiteData(10);
            float[][] outputContainer = new float[1][10];

            s.runner()
                    .feed("x", x)
                    .feed("keep_prob", keep_prob)
                    .fetch("y_conv")
                    .run()
                    .get(0)
                    .copyTo(outputContainer);
            for (int i = 0; i < outputContainer[0].length; i++) {
                output[i] = outputContainer[0][i];
            }
            System.out.println(output.length + "|" + Arrays.toString(output));
            x.close();
            keep_prob.close();
            s.close();
        }
    }

    @Override
    public String toString() {
        String ret = TextFormatting.LIGHT_PURPLE + super.toString() + ":\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - Status: " + TextFormatting.YELLOW + (isActive ? "Enabled" : "Dormant") + "|" +
                backend.getState().toString()
                + TextFormatting.LIGHT_PURPLE + "\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - Feeding: " + TextFormatting.YELLOW + feedDataMap.toString()
                + TextFormatting.LIGHT_PURPLE + "\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - Fetching: " + TextFormatting.YELLOW + fetchDataMap.toString()
                + TextFormatting.LIGHT_PURPLE + "\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - Other: " + TextFormatting.YELLOW + otherDataMap.toString()
                + TextFormatting.LIGHT_PURPLE + "\n";
        ret += TextFormatting.LIGHT_PURPLE + "    - Model Dir: " + modelDir + "\n";
        return ret;
    }

    static void info(String s) {
        Logger.getLogger("TF core").info(s);
    }

}
