package io.github.d0048.databackend;

import io.github.d0048.MCML;

import java.util.HashMap;

public class MLDataCoreMCML extends MLDataCore {
    HashMap<String, MLDataWrap> dataMap = new HashMap<String, MLDataWrap>();
    Thread backend;

    public MLDataCoreMCML() {
        super(BackEndType.MCML);
        backend = new Thread(() -> backendThread());
        backend.start();
    }

    @Override
    public MLDataWrap registerDataForID(String id) {
        if (!dataMap.containsKey(id))
            dataMap.put(id, new MLDataWrap(new int[]{100}, whiteData(100)));
        return getDataForID(id);
    }

    public void backendThread() {
        info("MCML Backend is now up and running!");
            MLDataWrap m1, m2, m3;
            try {
                if ((m1 = dataMap.get("m1")) != null && (m2 = dataMap.get("m2")) != null && (m3 = dataMap.get("m3")) != null) {
                    for (int i = 0; i < Math.min(Math.min(m1.getData().length, m2.getData().length), m3.getData().length); i++) {
                        m3.getData()[i] = m1.getData()[i] * m2.getData()[i];
                    }
                }
                Thread.sleep(200);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @Override
    public MLDataWrap getDataForID(String id) {
        return dataMap.get(id);
    }

    @Override
    public MLDataWrap writeDataForID(String id) {
        if(!backend.isAlive())backend.start();
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