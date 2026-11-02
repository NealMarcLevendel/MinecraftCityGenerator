package io.levendel.Minecraft;

import io.levendel.CityGenerator;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

//import java.awt.Color;

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

                                                                                        // return generateCity(context
                                                                                        // .getSource()
                                                                                        // .getLevel(),
                                                                                        // pos1, pos2);
                                                                                        return 1;
                                                                                }))));
        }

        // public static int generateCity(Level level, BlockPos pos1, BlockPos pos2) {
        // Elevation ev = ElevationProvider.provide(level, pos1, pos2);

        // AttractionField af = new AttractionField(ev, 1, level.getMaxBuildHeight(),
        // level.getMinBuildHeight());

        // BufferedImage bi = new BufferedImage(ev.getWidth(), ev.getHeight(),
        // BufferedImage.TYPE_INT_ARGB);

        // for (int z = 0; z < ev.getHeight(); z++) {
        // for (int x = 0; x < ev.getWidth(); x++) {

        // double cost = af.get(new Point(x, z));

        // int grayscale = (int) (cost * 255);
        // grayscale = Math.max(0, Math.min(255, grayscale));

        // bi.setRGB(
        // x,
        // z,
        // new Color(255, grayscale, grayscale, grayscale).toInteger());
        // }
        // }

        // File output = new File("el.png");

        // try {
        // ImageIO.write(bi, "png", output);
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

        // return 1;
        // }
}
