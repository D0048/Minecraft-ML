package io.github.d0048.common;

import java.util.Arrays;

public class MLDataWrap {
	int[] shape;
	int[] data;

	public int[] getShape() {
		return shape;
	}

	public void setShape(int[] shape) {
		while (shape.length < 3) {
			shape = Arrays.copyOf(shape, shape.length + 1);
			shape[shape.length - 1] = 1;
		}
		this.shape = shape;
	}

	public int[] getData() {
		return data;
	}

	public void setData(int[] data) {
		this.data = data;
	}

	public MLDataWrap(int[] shape, int[] data) {
		super();
		this.setShape(shape);
		this.setData(data);
	}

}