package io.levendel.Minecraft;

import io.levendel.Core.Elevation;
import io.levendel.Core.ElevationProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class MinecraftElevationProvider implements ElevationProvider {
    public Level level;
    public BlockPos pos1;
    public BlockPos pos2;

    public MinecraftElevationProvider(Level level, BlockPos pos1, BlockPos pos2) {
        this.level = level;
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    @Override
    public Elevation provide() {
        int minX = Math.min(pos1.getX(), pos2.getX());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());

        Elevation ev = new Elevation(minX, minZ, maxX, maxZ);

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int elevation = level.getMinBuildHeight();

                for (int y = level.getMaxBuildHeight() - 1; y >= level.getMinBuildHeight(); y--) {

                    if (!level.getBlockState(new BlockPos(x, y, z)).isAir()) {
                        elevation = y;
                        break;
                    }
                }

                ev.setWithOffset(x, z, elevation);
            }
        }

        return ev;
    }
}
