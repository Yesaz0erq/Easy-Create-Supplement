package com.easycreate.content;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.content.logistics.chute.SmartChuteBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredHolder;

public class EnderChuteBlock extends SmartChuteBlock {
   private final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnderChuteBlockEntity>> blockEntityType;

   public EnderChuteBlock(Properties properties, DeferredHolder<BlockEntityType<?>, BlockEntityType<EnderChuteBlockEntity>> blockEntityType) {
      super(properties);
      this.blockEntityType = blockEntityType;
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      BlockState smartChute = AllBlocks.SMART_CHUTE.getDefaultState();
      return smartChute.getShape(level, pos, context);
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      BlockState smartChute = AllBlocks.SMART_CHUTE.getDefaultState();
      return smartChute.getCollisionShape(level, pos, context);
   }

   public BlockEntityType<? extends ChuteBlockEntity> getBlockEntityType() {
      return (BlockEntityType<? extends ChuteBlockEntity>)this.blockEntityType.get();
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      BlockPos belowPos = pos.below();
      MenuProvider provider = level.getBlockState(belowPos).getMenuProvider(level, belowPos);
      if (provider != null) {
         if (!level.isClientSide) {
            player.openMenu(provider);
         }

         return InteractionResult.sidedSuccess(level.isClientSide);
      } else {
         return super.useWithoutItem(state, level, pos, player, hitResult);
      }
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      ItemInteractionResult result = super.useItemOn(stack, state, level, pos, player, hand, hitResult);
      if (stack.isEmpty() && result != ItemInteractionResult.SUCCESS) {
         BlockPos belowPos = pos.below();
         MenuProvider provider = level.getBlockState(belowPos).getMenuProvider(level, belowPos);
         if (provider == null) {
            return result;
         } else {
            if (!level.isClientSide) {
               player.openMenu(provider);
            }

            return ItemInteractionResult.SUCCESS;
         }
      } else {
         return result;
      }
   }
}
