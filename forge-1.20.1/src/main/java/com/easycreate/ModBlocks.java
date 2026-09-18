package com.easycreate;
import com.easycreate.content.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
public final class ModBlocks {
 public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "easycreate");
 public static final RegistryObject<EnderChuteBlock> ENDER_CHUTE = BLOCKS.register("ender_chute", () -> new EnderChuteBlock(
  Properties.copy(net.minecraft.world.level.block.Blocks.HOPPER).noOcclusion().isRedstoneConductor((s,l,p)->false).isSuffocating((s,l,p)->false).isViewBlocking((s,l,p)->false), ModBlockEntities.ENDER_CHUTE_BLOCK_ENTITY));
 public static final RegistryObject<EnderFluidTankBlock> ENDER_FLUID_TANK = BLOCKS.register("ender_fluid_tank", () -> new EnderFluidTankBlock(Properties.copy(net.minecraft.world.level.block.Blocks.COPPER_BLOCK).noOcclusion()));
 public static final RegistryObject<EnderSpeedControllerBlock> ENDER_SPEED_CONTROLLER = BLOCKS.register("ender_speed_controller", () -> new EnderSpeedControllerBlock(Properties.copy(net.minecraft.world.level.block.Blocks.GOLD_BLOCK)));
}
