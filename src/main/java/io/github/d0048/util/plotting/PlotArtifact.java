package io.github.d0048.util.plotting;

import java.io.Serializable;

public interface PlotArtifact extends Serializable {
    void draw();

    void clean();
}
