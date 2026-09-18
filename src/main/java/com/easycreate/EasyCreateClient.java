package com.easycreate;

import com.easycreate.ponder.EasyCreatePonderPlugin;
import com.simibubi.create.content.logistics.chute.SmartChuteRenderer;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(
   modid = "easycreate",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public final class EasyCreateClient {
   private static boolean ponderPluginRegistered;

   private EasyCreateClient() {
   }

   public static void registerPonderPlugin() {
      if (!ponderPluginRegistered) {
         PonderIndex.addPlugin(new EasyCreatePonderPlugin());
         ponderPluginRegistered = true;
      }
   }

   @SubscribeEvent
   public static void onClientSetup(FMLClientSetupEvent event) {
      registerPonderPlugin();
      event.enqueueWork(() -> {
         ItemBlockRenderTypes.setRenderLayer(ModBlocks.ENDER_FLUID_TANK.get(), RenderType.cutoutMipped());
         BlockEntityRenderers.register(ModBlockEntities.ENDER_FLUID_TANK.get(), com.simibubi.create.content.fluids.tank.FluidTankRenderer::new);
         BlockEntityRenderers.register(ModBlockEntities.ENDER_SPEED_CONTROLLER.get(), com.easycreate.client.EnderSpeedControllerRenderer::new);
         ItemBlockRenderTypes.setRenderLayer((Block)ModBlocks.ENDER_CHUTE.get(), RenderType.cutoutMipped());
         BlockEntityRenderers.register((BlockEntityType)ModBlockEntities.ENDER_CHUTE_BLOCK_ENTITY.get(), SmartChuteRenderer::new);
      });
   }
}

