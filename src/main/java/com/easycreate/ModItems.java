package com.easycreate;

import com.simibubi.create.content.logistics.funnel.FunnelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public final class ModItems {
   public static final Items ITEMS = DeferredRegister.createItems("easycreate");
   public static final DeferredItem<Item> ENDER_CHUTE = ITEMS.register(
      "ender_chute", () -> new FunnelItem((Block)ModBlocks.ENDER_CHUTE.get(), new Properties())
   );

   public static final DeferredItem<net.minecraft.world.item.BlockItem> ENDER_FLUID_TANK = ITEMS.register(
      "ender_fluid_tank", () -> new com.simibubi.create.content.fluids.tank.FluidTankItem(ModBlocks.ENDER_FLUID_TANK.get(), new Properties()));
   public static final DeferredItem<net.minecraft.world.item.BlockItem> ENDER_SPEED_CONTROLLER = ITEMS.register(
      "ender_speed_controller", () -> new net.minecraft.world.item.BlockItem(ModBlocks.ENDER_SPEED_CONTROLLER.get(), new Properties()));

   private ModItems() {
   }
}

