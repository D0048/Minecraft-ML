package io.github.d0048.util.plotting;

import io.github.d0048.common.blocks.MLGraphAxisTileEntity;

import java.io.Serializable;

public interface PlotArtifact extends Serializable {
    void draw(MLGraphAxisTileEntity graph);

    void clean(MLGraphAxisTileEntity graph);
}
