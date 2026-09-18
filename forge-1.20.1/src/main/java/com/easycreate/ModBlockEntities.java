package com.easycreate;

import com.easycreate.content.EnderChuteBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public final class ModBlockEntities {
   public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "easycreate");
   public static final RegistryObject<BlockEntityType<EnderChuteBlockEntity>> ENDER_CHUTE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
      "ender_chute", () -> Builder.of(EnderChuteBlockEntity::new, new Block[]{(Block)ModBlocks.ENDER_CHUTE.get()}).build(null)
   );

   public static final RegistryObject<BlockEntityType<com.easycreate.content.EnderFluidTankBlockEntity>> ENDER_FLUID_TANK = BLOCK_ENTITY_TYPES.register(
      "ender_fluid_tank", () -> Builder.of(com.easycreate.content.EnderFluidTankBlockEntity::new, ModBlocks.ENDER_FLUID_TANK.get()).build(null));
   public static final RegistryObject<BlockEntityType<com.easycreate.content.EnderSpeedControllerBlockEntity>> ENDER_SPEED_CONTROLLER = BLOCK_ENTITY_TYPES.register(
      "ender_speed_controller", () -> Builder.of(com.easycreate.content.EnderSpeedControllerBlockEntity::new, ModBlocks.ENDER_SPEED_CONTROLLER.get()).build(null));

   private ModBlockEntities() {
   }
}

