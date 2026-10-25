package io.levendel.Utility;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class ElevationProvider {
    public static Elevation provide(Level level, BlockPos pos1, BlockPos pos2) {
        Elevation ev = new Elevation();

        int minX = Math.min(pos1.getX(), pos2.getX());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                for (int y = level.getMaxBuildHeight(); y >= level.getMinBuildHeight(); y--) {
                    if (!level.getBlockState(new BlockPos(x, y, z)).isAir()) {
                        ev.set(new ElevationPoint(x, z), y);
                    }
                }
            }
        }

        return ev;

    }
}
