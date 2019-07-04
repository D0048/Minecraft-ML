package io.github.d0048;

import io.github.d0048.databackend.MLDataCore;
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

    @Config.Name("Data core Type")
    @Config.Comment({
                            "The backend used for computations",
                    })
    @Config.RequiresMcRestart
    public static MLDataCore.BackEndType backendType = MLDataCore.BackEndType.MCML;


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
                System.out.println("Config sync");
            }
        }
    }
}
