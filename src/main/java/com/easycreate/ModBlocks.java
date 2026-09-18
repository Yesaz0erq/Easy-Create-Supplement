package com.easycreate;

import com.easycreate.content.EnderChuteBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;

public final class ModBlocks {
   public static final Blocks BLOCKS = DeferredRegister.createBlocks("easycreate");
   public static final DeferredBlock<EnderChuteBlock> ENDER_CHUTE = BLOCKS.registerBlock(
      "ender_chute",
      properties -> new EnderChuteBlock(properties, ModBlockEntities.ENDER_CHUTE_BLOCK_ENTITY),
      Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.HOPPER)
         .noOcclusion()
         .isRedstoneConductor((state, level, pos) -> false)
         .isSuffocating((state, level, pos) -> false)
         .isViewBlocking((state, level, pos) -> false)
   );

   public static final DeferredBlock<com.easycreate.content.EnderFluidTankBlock> ENDER_FLUID_TANK = BLOCKS.registerBlock(
      "ender_fluid_tank", com.easycreate.content.EnderFluidTankBlock::new,
      Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.COPPER_BLOCK).noOcclusion().isRedstoneConductor((state, level, pos) -> true));
   public static final DeferredBlock<com.easycreate.content.EnderSpeedControllerBlock> ENDER_SPEED_CONTROLLER = BLOCKS.registerBlock(
      "ender_speed_controller", com.easycreate.content.EnderSpeedControllerBlock::new,
      Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.GOLD_BLOCK).mapColor(net.minecraft.world.level.material.MapColor.TERRACOTTA_YELLOW));

   private ModBlocks() {
   }
}


