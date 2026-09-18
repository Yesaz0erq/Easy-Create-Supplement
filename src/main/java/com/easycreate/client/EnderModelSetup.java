package com.easycreate.client;

import com.easycreate.ModBlocks;
import com.easycreate.ModItems;
import com.simibubi.create.content.fluids.tank.FluidTankModel;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

/** Keep Create's original UVs and alpha masks; color metal without replacing its texture atlas. */
@EventBusSubscriber(modid = "easycreate", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class EnderModelSetup {
    public static final int ENDER_COLOR = 0x408FFF;

    @SubscribeEvent
    public static void blockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, index) -> index == 0 ? ENDER_COLOR : -1,
                ModBlocks.ENDER_FLUID_TANK.get(), ModBlocks.ENDER_SPEED_CONTROLLER.get());
    }

    @SubscribeEvent
    public static void itemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, index) -> index == 0 ? ENDER_COLOR : -1,
                ModItems.ENDER_FLUID_TANK.get(), ModItems.ENDER_SPEED_CONTROLLER.get());
    }

    @SubscribeEvent
    public static void tankModels(ModelEvent.ModifyBakingResult event) {
        for (var state : ModBlocks.ENDER_FLUID_TANK.get().getStateDefinition().getPossibleStates()) {
            var key = BlockModelShaper.stateToModelLocation(state);
            var original = event.getModels().get(key);
            if (original != null) event.getModels().put(key, FluidTankModel.standard(original));
        }
    }
}
