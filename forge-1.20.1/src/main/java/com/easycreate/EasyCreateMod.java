package com.easycreate;

import com.simibubi.create.Create;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

@Mod("easycreate")
public class EasyCreateMod {
   public static final String MOD_ID = "easycreate";

   public EasyCreateMod() {
      IEventBus modBus = net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext.get().getModEventBus();
      ModBlocks.BLOCKS.register(modBus);
      ModItems.ITEMS.register(modBus);
      ModBlockEntities.BLOCK_ENTITY_TYPES.register(modBus);
      modBus.addListener(this::onBuildCreativeTabs);

   }

   private void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
      if (event.getTabKey().location().equals(Create.asResource("base"))) {
         event.accept((ItemLike)ModItems.ENDER_CHUTE.get());
         event.accept(ModItems.ENDER_FLUID_TANK.get());
         event.accept(ModItems.ENDER_SPEED_CONTROLLER.get());
      }
   }
}

