package io.github.d0048.common.blocks;

import io.github.d0048.MLConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.concurrent.CopyOnWriteArrayList;

@Mod.EventBusSubscriber
public class MLAsyncHelper {
    static CopyOnWriteArrayList<Operation> toActionList = new CopyOnWriteArrayList<>();

    @SubscribeEvent
    public static void call(TickEvent.ServerTickEvent e) {
        for (int i = 0; i < MLConfig.asyncBatchSize; i++) {
            if (toActionList.isEmpty()) break;
            Operation op = toActionList.remove(0);
            op.world.setBlockState(op.pos, op.state);
        }
    }

    public static void removeAsync(WorldServer world, BlockPos pos) {
        placeAsync(world, pos, Blocks.AIR.getDefaultState());
    }

    public static void placeAsync(WorldServer world, BlockPos pos, IBlockState state) {
        toActionList.add(new Operation(world, pos, state));
    }

    static class Operation {
        public final WorldServer world;
        public final BlockPos pos;
        public final IBlockState state;

        public Operation(WorldServer world, BlockPos pos, IBlockState state) {
            this.world = world;
            this.pos = pos;
            this.state = state;
        }
    }
}
