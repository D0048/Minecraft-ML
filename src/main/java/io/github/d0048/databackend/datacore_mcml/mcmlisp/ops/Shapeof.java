package io.github.d0048.databackend.datacore_mcml.mcmlisp.ops;

import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.util.Util;
import org.apache.commons.lang3.Range;

import java.util.List;

public class Shapeof extends OPBase {
    public Shapeof() {
        setName("shapeof");
        setNumArgs(Range.between(1, 1));
    }

    @Override
    public MLDataWrap runRaw(List<MLDataWrap> args) throws Exception {
        return new MLDataWrap(Util.int2DoubleArray(args.get(0).getShape()));
    }

    @Override
    public String getUsage() {
        return "Shape of given matrix: \n    (" + getName() + "mat)";
    }
}
