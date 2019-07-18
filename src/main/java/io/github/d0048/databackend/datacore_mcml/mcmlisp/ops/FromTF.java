package io.github.d0048.databackend.datacore_mcml.mcmlisp.ops;

import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.databackend.datacore_mcml.mcmlisp.Molecule;
import io.github.d0048.util.Util;
import org.apache.commons.lang3.Range;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class FromTF extends OPBase {//TODO

    public FromTF() {
        setName("from_image");
        setNumArgs(Range.between(1, 100));
    }

    @Override
    public MLDataWrap run(List<Molecule> args) throws Exception {
        checkArguments(args);
        return null;
    }

    @Override
    public MLDataWrap runRaw(List<MLDataWrap> args) throws Exception {
        throw new Exception("Reading from image operation should never be run raw!");
    }

    @Override
    public String getUsage() {
        return "Load an image from file: \n    (" + getName() + " any_url_to_img)";
    }
}
