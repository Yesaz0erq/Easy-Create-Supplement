package com.easycreate;

import com.simibubi.create.content.logistics.funnel.FunnelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModItems {
   public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "easycreate");
   public static final RegistryObject<Item> ENDER_CHUTE = ITEMS.register(
      "ender_chute", () -> new FunnelItem((Block)ModBlocks.ENDER_CHUTE.get(), new Properties())
   );

   public static final RegistryObject<net.minecraft.world.item.BlockItem> ENDER_FLUID_TANK = ITEMS.register(
      "ender_fluid_tank", () -> new com.simibubi.create.content.fluids.tank.FluidTankItem(ModBlocks.ENDER_FLUID_TANK.get(), new Properties()));
   public static final RegistryObject<net.minecraft.world.item.BlockItem> ENDER_SPEED_CONTROLLER = ITEMS.register(
      "ender_speed_controller", () -> new net.minecraft.world.item.BlockItem(ModBlocks.ENDER_SPEED_CONTROLLER.get(), new Properties()));

   private ModItems() {
   }
}

