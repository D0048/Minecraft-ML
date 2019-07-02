package io.github.d0048.common;

import java.util.HashMap;

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

    public MLDataCoreTF() {
        super(MLDataCore.BackEndType.TF);
        // Info("Loading Tensorflow ML Backend: " + TensorFlow.version());
        // graph = new Graph();
        // Operation x = graph.opBuilder("Placeholder", "x").setAttr("dtype",
        // DataType.DOUBLE).build();
        // Operation y = graph.opBuilder("Placeholder", "y").setAttr("dtype",
        // DataType.DOUBLE).build();
        // session = new Session(graph);
    }

    @Override
    public MLDataWrap registerDataForID(String id) {
		/*dataMap.put(id, new MLDataWrap(new int[] { 5, 2, 5 },
				new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4,
						3, 2, 1, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 14, 13, 12, 11, 10, 9, 8, 7,
						6, 5, 4, 3, 2, 1, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 14, 13, 12, 11, 10,
						9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 14, 13, 12,
						11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 14,
						13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
						15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
						13, 14, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
						11, 12, 13, 14, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8,
						9, 10, 11, 12, 13, 14, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 }));*/
        dataMap.put(id, new MLDataWrap(new int[]{27, 1, 27}, new int[1000]));
        return getDataForID(id);
    }

    @Override
    public MLDataWrap getDataForID(String id) {
        return dataMap.get(id);
    }

    @Override
    public MLDataWrap writeDataForID(String id) {
        return null;
    }

    static void info(String s) {
        MCML.logger.info(s);
    }

}
