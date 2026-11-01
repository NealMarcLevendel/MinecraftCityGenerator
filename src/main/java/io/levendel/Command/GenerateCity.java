package io.levendel.Command;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.antlr.v4.parse.ANTLRParser.grammarSpec_return;

import com.mojang.brigadier.arguments.IntegerArgumentType;

import io.levendel.CityGenerator;
import io.levendel.Utility.AttractionField;
import io.levendel.Utility.Elevation;
import io.levendel.Utility.ElevationPoint;
import io.levendel.Utility.ElevationProvider;
import io.levendel.Utility.StreetGrower;
import io.levendel.Utility.Color;
import net.minecraft.client.Minecraft;
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

//import java.awt.Color;
import java.awt.image.BufferedImage;

@Mod.EventBusSubscriber(modid = CityGenerator.MODID)
public class GenerateCity {

        @SuppressWarnings("null")
        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event) {
                event.getDispatcher().register(
                                Commands.literal("generatecity")
                                                .then(Commands.argument("pos1", BlockPosArgument.blockPos())
                                                                .then(Commands.argument("pos2",
                                                                                BlockPosArgument.blockPos())
                                                                                .executes(context -> {
                                                                                        BlockPos pos1 = BlockPosArgument
                                                                                                        .getLoadedBlockPos(
                                                                                                                        context,
                                                                                                                        "pos1");
                                                                                        BlockPos pos2 = BlockPosArgument
                                                                                                        .getLoadedBlockPos(
                                                                                                                        context,
                                                                                                                        "pos2");

                                                                                        // Your logic here

                                                                                        return generateCity(context
                                                                                                        .getSource()
                                                                                                        .getLevel(),
                                                                                                        pos1, pos2);
                                                                                }))));
        }

        public static int generateCity(Level level, BlockPos pos1, BlockPos pos2) {
                Elevation ev = ElevationProvider.provide(level, pos1, pos2);

                AttractionField af = new AttractionField(ev, 3, level.getMaxBuildHeight(), level.getMinBuildHeight());

                BufferedImage bi = new BufferedImage(
                                ev.bound2.x() - ev.bound1.x() + 1,
                                ev.bound2.z() - ev.bound1.z() + 1,
                                BufferedImage.TYPE_INT_ARGB);

                for (int x = ev.bound1.x(); x <= ev.bound2.x(); x++) {
                        for (int z = ev.bound1.z(); z <= ev.bound2.z(); z++) {
                                double cost = af.get(new ElevationPoint(x, z));

                                int grayscale = (int) (cost * 255);

                                if (grayscale <= 360) {
                                        bi.setRGB(x - ev.bound1.x(), z - ev.bound1.z(),
                                                        new Color(255, grayscale, grayscale, grayscale).toInteger());
                                }
                                System.out.println("Grayscale at " + x + ", " + z + ": " + grayscale);
                        }
                }

                File output = new File("el.png");/*
                                                  * new File(
                                                  * Minecraft.getInstance().gameDirectory,
                                                  * "el.png");
                                                  */

                try {
                        ImageIO.write(bi, "png", output);
                } catch (IOException e) {
                        e.printStackTrace();
                }

                return 1;
        }
}
