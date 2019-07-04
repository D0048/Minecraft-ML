package io.github.d0048.databackend.datacore_tf;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HashMap;

import io.github.d0048.MCML;
import io.github.d0048.databackend.MLDataCore;
import io.github.d0048.databackend.MLDataWrap;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;

public class MLDataCoreTF extends MLDataCore {
    // Graph graph;
    // Session session;
    HashMap<String, MLDataWrap> dataMap = new HashMap<String, MLDataWrap>();
    double[] datatmp = new double[1000];
    Thread backend;

    public MLDataCoreTF() {
        super(MLDataCore.BackEndType.TF);
        info("Loading Tensorflow ML Backend: TF " + TensorFlow.version());
        backend = new Thread(() -> backendThread());
        backend.start();
    }


    public void backendThread() {
        MLDataWrap input, output;
        try {
            if ((input = dataMap.get("canvas_input")) != null && (output = dataMap.get("classification_output")) != null) {
                try (SavedModelBundle b = SavedModelBundle
                        .load("/home/d0048/Desktop/Program-code/Minecraft Mode " +
                                        "Dev/Minecraft-ML/src/main/java/io/github/d0048/databackend/datacore_tf/model",
                                "serve")) {
                    Session s = b.session();
                    FloatBuffer fb = FloatBuffer.allocate(784);
                    for (double i : input.getData()) {
                        fb.put((float) i);
                    }
                    fb.rewind();

                    float[] keep_prob_array = new float[1024];
                    Arrays.fill(keep_prob_array, 1f);

                    Tensor x = Tensor.create(new long[]{784}, fb);
                    Tensor keep_prob = Tensor.create(new long[]{1, 1024}, FloatBuffer.wrap(keep_prob_array));

                    float[][] outputContainer = new float[1][10];

                    float[][] matrix = s.runner()
                            .feed("x", x)
                            .feed("keep_prob", keep_prob)
                            .fetch("y_conv")
                            .run()
                            .get(0)
                            .copyTo(outputContainer);
                    for (int i = 0; i < outputContainer[0].length; i++) {
                        output.getData()[i] = outputContainer[0][i];
                    }
                    s.close();
                }
            }
            Thread.sleep(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public MLDataWrap registerDataForID(String id) {
        if (!dataMap.containsKey(id)) {
            switch (id) {
                case "canvas_input":
                    dataMap.put(id, new MLDataWrap(new int[]{28 * 28}, whiteData(28 * 28)));
                    break;
                case "classification_output":
                    dataMap.put(id, new MLDataWrap(new int[]{10}, whiteData(10)));
                    break;
                default:
                    dataMap.put(id, new MLDataWrap(new int[]{100}, whiteData(100)));
                    break;
            }
        }
        return getDataForID(id);
    }

    @Override
    public MLDataWrap getDataForID(String id) {
        return dataMap.get(id);
    }

    @Override//currently no used it seems
    public MLDataWrap writeDataForID(String id) {
        if (!backend.isAlive()) (backend = new Thread(() -> backendThread())).start();
        return null;
    }

    static double[] whiteData(int size) {
        double[] datatmp = new double[size];
        for (int i = 0; i < datatmp.length; i++) {
            datatmp[i] = 0;
        }
        return datatmp;
    }

    static void info(String s) {
        MCML.logger.info(s);
    }

}
