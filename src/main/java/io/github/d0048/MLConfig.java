package io.github.d0048;

import io.github.d0048.common.blocks.MLColorConverterTileEntity;
import io.github.d0048.databackend.MLDataCore;
import io.github.d0048.databackend.datacore_mcml.mcmlisp.Evaluater;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = MCML.MODID, name = MCML.NAME)
public class MLConfig {
    @Config.Name("Resolution of Scalars")
    @Config.Comment({
                            "The resolution used to display mono-value scalars in scalar blocks",
                    })
    @Config.RangeInt(min = 1, max = 16)
    @Config.RequiresMcRestart
    public static int scalarResolution = 16;

    @Config.Name("Refresh Interval of Color Converter")
    @Config.Comment({
                            "How many ticks to wait before refreshing, lower is faster, -1 for on demand",
                    })
    @Config.RangeInt(min = -1, max = 10000)
    public static int colorConverterRefershInterval = -1;

    @Config.Name("Refresh Interval of Tensor Display")
    @Config.Comment({
                            "How many ticks to wait before refreshing, lower is faster.",
                    })
    @Config.RangeInt(min = 1, max = 10000)
    public static int tensorDisplayRefreshInterval = 10;

    @Config.Name("Async Operation Batch Size")
    @Config.Comment({
                            "How big a batch should async operation process each tick.",
                    })
    @Config.RangeInt(min = 1, max = Integer.MAX_VALUE)
    public static int asyncBatchSize = 100;

    @Config.Name("Data Core Type")
    @Config.Comment({
                            "The backend used for computations",
                    })
    @Config.RequiresMcRestart
    public static MLDataCore.BackEndType backendType = MLDataCore.BackEndType.MCML;

    @Config.Name("Color Converter Mode")
    @Config.Comment({
                            "Whether to use blocks of nearest color or glass",
                    })
    public static MLColorConverterTileEntity.ColorMode ConverterColorMode = MLColorConverterTileEntity.ColorMode.GLASS;


    @Config.Name("Data Core Update Frequency")
    @Config.Comment({
                            "How many milliseconds between each backend refresh, 0 for on-demand",
                    })
    @Config.RangeInt(min = 0, max = 100000)
    public static int backendUpdateInterval = 200;

    @Config.Name("Tolerance of Absolute Error")
    @Config.Comment({
                            "Compensating for float point errors, numbers with absolute difference less than this will be treated as" +
                                    " equal in compare operations",
                    })
    @Config.RangeDouble(min = 0, max = 0.99)
    public static double compareTolerance = 0;

    @Config.Name("High Quality Models")
    @Config.Comment({
                            "Use high-quality 3D models. They are fantastic but requires a beefy computer.",
                    })
    @Config.RequiresMcRestart
    public static boolean HQ_MODEL = false;

    // Handler below ------------------------------------------------------------------------------------------------------
    @Mod.EventBusSubscriber
    private static class EventHandler {

        /**
         * Inject the new values and save to the config file when the config has been changed from the GUI.
         */
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(MCML.MODID)) {
                ConfigManager.sync(MCML.MODID, Config.Type.INSTANCE);
                Evaluater.compareTolerance = compareTolerance;
                System.out.println("Config sync");
            }
        }
    }
}
