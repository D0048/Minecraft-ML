package io.github.d0048.databackend.datacore_mcml.mcmlisp.ops;

import io.github.d0048.databackend.MLDataWrap;
import io.github.d0048.databackend.datacore_mcml.mcmlisp.Molecule;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.lang3.Range;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class FromImageFile extends OPBase {
    public static boolean doCaching = false;

    public FromImageFile() {
        setName("from_image");
        setNumArgs(Range.between(1, 100));
    }


    ConcurrentHashMap<URL, MLDataWrap> pictureCacheMap = new ConcurrentHashMap<URL, MLDataWrap>();

    @Override
    public MLDataWrap run(List<Molecule> args) throws Exception {
        checkArguments(args);
        String urlStr = "";
        for (Molecule m : args) urlStr += m.getFullStr() + " ";
        URL url = new URL(urlStr);
        if (doCaching && pictureCacheMap.keySet().contains(url)) {
            return pictureCacheMap.get(url).clone();
        } else {
            //System.out.println("Loading file: [" + urlStr + "]");
            BufferedImage img = ImageIO.read(url);
            pictureCacheMap.put(url, MLDataWrap.fromBufferedImage(img));
            return pictureCacheMap.get(url);
        }
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
