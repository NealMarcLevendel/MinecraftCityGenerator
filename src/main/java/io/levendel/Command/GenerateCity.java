package io.levendel.Command;

import com.mojang.brigadier.arguments.IntegerArgumentType;

import io.levendel.CityGenerator;
import io.levendel.Utility.AttractionField;
import io.levendel.Utility.Elevation;
import io.levendel.Utility.ElevationProvider;
import io.levendel.Utility.StreetGrower;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.synchronization.brigadier.IntegerArgumentInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CityGenerator.MODID)
public class GenerateCity {

        @SuppressWarnings("null")
        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event) {
                event.getDispatcher().register(
                                Commands.literal("generatecity")
                                                .then(Commands.argument("radius", IntegerArgumentType.integer())
                                                                .then(Commands.argument("y1",
                                                                                IntegerArgumentType.integer())
                                                                                .then(Commands.argument("y2",
                                                                                                IntegerArgumentType
                                                                                                                .integer())
                                                                                                .executes(context -> {
                                                                                                        Level level = context
                                                                                                                        .getSource()
                                                                                                                        .getLevel();

                                                                                                        // Level level =
                                                                                                        // context.getSource().getLevel();

                                                                                                        BlockPos pos1 = BlockPosArgument
                                                                                                                        .getLoadedBlockPos(
                                                                                                                                        context,
                                                                                                                                        "pos1");
                                                                                                        BlockPos pos2 = BlockPosArgument
                                                                                                                        .getLoadedBlockPos(
                                                                                                                                        context,
                                                                                                                                        "pos2");

                                                                                                        // int minX =
                                                                                                        // Math.min(pos1.getX(),
                                                                                                        // pos2.getX());
                                                                                                        // int minY =
                                                                                                        // Math.min(pos1.getY(),
                                                                                                        // pos2.getY());
                                                                                                        // int minZ =
                                                                                                        // Math.min(pos1.getZ(),
                                                                                                        // pos2.getZ());
                                                                                                        // int maxX =
                                                                                                        // Math.max(pos1.getX(),
                                                                                                        // pos2.getX());
                                                                                                        // int maxY =
                                                                                                        // Math.max(pos1.getY(),
                                                                                                        // pos2.getY());
                                                                                                        // int maxZ =
                                                                                                        // Math.max(pos1.getZ(),
                                                                                                        // pos2.getZ());

                                                                                                        // for (BlockPos
                                                                                                        // pos :
                                                                                                        // BlockPos.betweenClosed(minX,
                                                                                                        // minY, minZ,
                                                                                                        // maxX, maxY,
                                                                                                        // maxZ)) {
                                                                                                        // if
                                                                                                        // (level.getBlockState(pos).is(Blocks.STONE))
                                                                                                        // {
                                                                                                        // level.setBlock(pos,
                                                                                                        // Blocks.ACACIA_PLANKS.defaultBlockState(),
                                                                                                        // 3);
                                                                                                        // }
                                                                                                        // }
                                                                                                        return generateCity(
                                                                                                                        level,
                                                                                                                        pos1,
                                                                                                                        pos2);
                                                                                                })))));
        }

        public static int generateCity(Level level, BlockPos pos1, BlockPos pos2) {
                Elevation ev = ElevationProvider.provide(level, pos1, pos2);

                AttractionField af = new AttractionField(ev);

                //StreetGrower rg = new StreetGrower(ev, 10);

                return 1;
        }
}
