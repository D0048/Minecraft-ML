package io.github.d0048.common;

import java.util.HashMap;
import java.util.Random;
import java.util.Timer;

import org.tensorflow.DataType;
import org.tensorflow.Graph;
import org.tensorflow.Operation;
import org.tensorflow.Session;
import org.tensorflow.TensorFlow;

import akka.event.Logging.Info;
import io.github.d0048.MCML;
import io.github.d0048.common.blocks.MLTensorDisplayTileEntity;
import scala.inline;

public class MLDataCoreTF extends MLDataCore {
    // Graph graph;
    // Session session;
    HashMap<String, MLDataWrap> dataMap = new HashMap<String, MLDataWrap>();
    double[] datatmp = new double[1000];
    Thread backend;

    public MLDataCoreTF() {
        super(MLDataCore.BackEndType.TF);
        // Info("Loading Tensorflow ML Backend: " + TensorFlow.version());
        // graph = new Graph();
        // Operation x = graph.opBuilder("Placeholder", "x").setAttr("dtype",
        // DataType.DOUBLE).build();
        // Operation y = graph.opBuilder("Placeholder", "y").setAttr("dtype",
        // DataType.DOUBLE).build();
        // session = new Session(graph);
        for (int i = 0; i < 1000; i++) {
            datatmp[i] = 0;
        }
        backend = new Thread(() -> {
            backendThread();
        });
        backend.start();
    }

    @Override
    public MLDataWrap registerDataForID(String id) {
        if (!dataMap.containsKey(id))
            dataMap.put(id, new MLDataWrap(new int[]{100}, whiteData(100)));
        return getDataForID(id);
    }

    public void backendThread() {
        info("ML Backend is now up and running!");
        while (true) {
            MLDataWrap m1, m2, m3;
            try {
                if (
                        (m1 = dataMap.get("m1")) != null && (m2 = dataMap.get("m2")) != null && (m3 = dataMap.get("m3")) != null
                ) {
                    for (int i = 0; i < Math.min(Math.min(m1.getData().length, m2.getData().length), m3.getData().length); i++) {
                        m3.getData()[i] = m1.getData()[i] * m2.getData()[i];
                        //info("mutiply " + m1.getData()[i] + " * " + m2.getData()[i]);
                    }
                }
                Thread.sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public MLDataWrap getDataForID(String id) {
        return dataMap.get(id);
    }

    @Override
    public MLDataWrap writeDataForID(String id) {
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
