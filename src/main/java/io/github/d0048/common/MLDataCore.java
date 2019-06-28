package io.github.d0048.common;

import io.github.d0048.common.blocks.MLTensorDisplay;
import io.github.d0048.common.blocks.MLTensorDisplayTileEntity;

public abstract class MLDataCore {
	public enum BackEndType {
		TF, MCML
	};

	BackEndType backend;

	public MLDataCore(BackEndType b) {
		backend = b;
	}

	abstract public MLDataWrap registerDataForID(String id); // Register what to read/write

	abstract public MLDataWrap getDataForID(String id); // Read

	abstract public MLDataWrap writeDataForID(String id);// Write

}
