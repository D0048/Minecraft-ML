package io.github.d0048.common;

import io.github.d0048.MCML;
import io.github.d0048.MLConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.concurrent.ConcurrentLinkedQueue;

@Mod.EventBusSubscriber
public class MLAsyncHelper {
    static ConcurrentLinkedQueue<Operation> toActionList = new ConcurrentLinkedQueue<>();

    static int loop = 10;

    @SubscribeEvent
    public static void call(TickEvent.ServerTickEvent e) {
        if (true) return;//disabled
        if (toActionList.isEmpty()) return;
        if (loop-- > 0) {
            info(toActionList.size() + " actions pending: ");
            long time = System.nanoTime();
            int i = MLConfig.asyncBatchSize;
            Operation op;

            long total = 0;
            while (i-- > 0 && (op = toActionList.poll()) != null) {
                long subtime = System.nanoTime();
                op.world.setBlockState(op.pos, op.state);
                total += (System.nanoTime() - subtime);
            }
            info("That took " + total + "/" + (System.nanoTime() - time) + " ns");

            loop = 10;
        }
    }

    public static void removeAsync(World world, BlockPos pos) {
        placeAsync(world, pos, Blocks.AIR.getDefaultState());
    }

    public static void placeAsync(World world, BlockPos pos, IBlockState state) {
        toActionList.add(new Operation(world, pos, state));
    }

    static class Operation {
        public final World world;
        public final BlockPos pos;
        public final IBlockState state;

        public Operation(World world, BlockPos pos, IBlockState state) {
            this.world = world;
            this.pos = pos;
            this.state = state;
        }

    }

    static void info(String s) {
        MCML.logger.info(s);
    }
}
