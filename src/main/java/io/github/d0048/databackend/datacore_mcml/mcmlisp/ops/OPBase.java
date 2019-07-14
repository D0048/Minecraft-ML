package io.github.d0048.databackend.datacore_mcml.mcmlisp.ops;

import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.databackend.datacore_mcml.MLDataCoreMCML;
import io.github.d0048.databackend.datacore_mcml.mcmlisp.Evaluater;
import io.github.d0048.databackend.datacore_mcml.mcmlisp.Molecule;
import org.apache.commons.lang3.Range;

import java.util.List;

public abstract class OPBase {
    String name;
    Range<Integer> numArgs;

    public void checkArguments(List args) {
        if (!getNumArgs().contains(args.size()))
            throw new IllegalArgumentException("Got " + args.size() + " elements but expecting " + numArgs);
    }

    public MLDataWrap run(List<Molecule> args) throws Exception {
        checkArguments(args);
        return runRaw(Evaluater.evaluateAll(args));
    }

    abstract public MLDataWrap runRaw(List<MLDataWrap> args) throws Exception;

    @Override
    public String toString() {
        return "OPBase{" +
                "name='" + name + '\'' +
                ", numArgs=" + numArgs +
                '}';
    }

    public abstract String getUsage();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Range<Integer> getNumArgs() {
        return numArgs;
    }

    public void setNumArgs(Range<Integer> numArgs) {
        this.numArgs = numArgs;
    }

    static void info(String s) {
        MLDataCoreMCML.logger.info(s);
    }
}
