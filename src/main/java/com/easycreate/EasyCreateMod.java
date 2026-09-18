package com.easycreate;

import com.simibubi.create.Create;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod("easycreate")
public class EasyCreateMod {
   public static final String MOD_ID = "easycreate";

   public EasyCreateMod(IEventBus modBus) {
      ModBlocks.BLOCKS.register(modBus);
      ModItems.ITEMS.register(modBus);
      ModBlockEntities.BLOCK_ENTITY_TYPES.register(modBus);
      modBus.addListener(this::onBuildCreativeTabs);
      modBus.addListener((net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) ->
         event.registerBlockEntity(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,
            ModBlockEntities.ENDER_FLUID_TANK.get(), (be, side) -> be.fluidHandler()));
   }

   private void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
      if (event.getTabKey().location().equals(Create.asResource("base"))) {
         event.accept((ItemLike)ModItems.ENDER_CHUTE.get());
         event.accept(ModItems.ENDER_FLUID_TANK.get());
         event.accept(ModItems.ENDER_SPEED_CONTROLLER.get());
      }
   }
}

