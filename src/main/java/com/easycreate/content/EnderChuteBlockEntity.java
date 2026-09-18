package com.easycreate.content;

import com.easycreate.ModBlockEntities;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.chute.SmartChuteBlockEntity;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.ItemHelper.ExtractionCountMode;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

public class EnderChuteBlockEntity extends SmartChuteBlockEntity {
   private static final int MAX_WIRELESS_TRANSFER_PER_OPERATION = 16;
   private int wirelessCooldown;

   public EnderChuteBlockEntity(BlockPos pos, BlockState blockState) {
      super((BlockEntityType)ModBlockEntities.ENDER_CHUTE_BLOCK_ENTITY.get(), pos, blockState);
   }

   public void tick() {
      if (this.level instanceof ServerLevel serverLevel) {
         if (!hasAttachedRedstoneLink(serverLevel, this.worldPosition)) {
            super.tick();
         } else {
            LinkBehaviour sourceLink = getRedstoneLinkNear(serverLevel, this.worldPosition, false);
            if (this.canActivate()) {
               if (this.wirelessCooldown > 0) {
                  this.wirelessCooldown--;
               } else {
                  if (sourceLink != null && this.tryTransfer(serverLevel, sourceLink)) {
                     this.wirelessCooldown = (Integer)AllConfigs.server().logistics.defaultExtractionTimer.get();
                  }
               }
            }
         }
      } else {
         super.tick();
      }
   }

   private boolean tryTransfer(ServerLevel serverLevel, LinkBehaviour sourceLink) {
      IItemHandler source = findSourceHandler(serverLevel, this.worldPosition);
      Set<IRedstoneLinkable> network = Create.REDSTONE_LINK_NETWORK_HANDLER.getNetworkOf(serverLevel, sourceLink);
      if (network != null && !network.isEmpty()) {
         for (IRedstoneLinkable linkable : network) {
            if (linkable != sourceLink && linkable.isListening()) {
               BlockPos linkedChutePos = findAttachedChutePos(serverLevel, linkable.getLocation());
               if (linkedChutePos != null && !linkedChutePos.equals(this.worldPosition)) {
                  BlockEntity linkedBE = serverLevel.getBlockEntity(linkedChutePos);
                  if (linkedBE instanceof EnderChuteBlockEntity) {
                     EnderChuteBlockEntity receiver = (EnderChuteBlockEntity)linkedBE;
                     if (receiver.canActivate()) {
                        LinkBehaviour receiverLink = getRedstoneLinkNear(serverLevel, receiver.worldPosition, true);
                        if (receiverLink != null && receiverLink.getNetworkKey().equals(sourceLink.getNetworkKey())) {
                           IItemHandler target = findTargetHandler(serverLevel, receiver.worldPosition);
                           int transferAmount = getLimitedTransferAmount(receiver.getExtractionAmount());
                           ExtractionCountMode transferMode = receiver.getExtractionMode();
                           ItemStack moved = ItemStack.EMPTY;
                           if (source != null) {
                              moved = this.transferFromInventoryWithCreateThroughput(
                                 serverLevel, source, target, receiver.worldPosition, transferAmount, transferMode, receiver
                              );
                           }

                           if (moved.isEmpty()) {
                              moved = this.transferFromEntityWithCreateThroughput(
                                 serverLevel, receiver.worldPosition, target, transferAmount, transferMode, receiver
                              );
                           }

                           if (!moved.isEmpty()) {
                              this.updatePush(moved.getCount());
                              receiver.updatePush(moved.getCount());
                              return true;
                           }
                        }
                     }
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private ItemStack transferFromInventoryWithCreateThroughput(
      ServerLevel level, IItemHandler source, IItemHandler target, BlockPos receiverPos, int amount, ExtractionCountMode mode, EnderChuteBlockEntity receiver
   ) {
      amount = getLimitedTransferAmount(amount);
      ItemStack simulated = ItemHelper.extract(source, stack -> this.canTransferStack(stack, receiver), mode, amount, true);
      if (simulated.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         int movable = simulated.getCount();
         if (target != null) {
            ItemStack simulatedRemainder = ItemHandlerHelper.insertItem(target, simulated.copy(), true);
            movable = simulated.getCount() - simulatedRemainder.getCount();
         }

         if (movable > 0 && (mode != ExtractionCountMode.EXACTLY || movable >= amount)) {
            int toExtract = mode == ExtractionCountMode.EXACTLY ? amount : movable;
            ItemStack extracted = ItemHelper.extract(source, ItemHelper.sameItemPredicate(simulated), ExtractionCountMode.EXACTLY, toExtract, false);
            return extracted.isEmpty() ? ItemStack.EMPTY : this.deliverToReceiver(level, extracted, target, receiverPos);
         } else {
            return ItemStack.EMPTY;
         }
      }
   }

   private ItemStack transferFromEntityWithCreateThroughput(
      ServerLevel level, BlockPos receiverPos, IItemHandler target, int amount, ExtractionCountMode mode, EnderChuteBlockEntity receiver
   ) {
      ItemEntity sourceEntity = this.findSourceItemEntity(level);
      if (sourceEntity != null && sourceEntity.isAlive()) {
         ItemStack sourceStack = sourceEntity.getItem();
         if (!this.canTransferStack(sourceStack, receiver)) {
            return ItemStack.EMPTY;
         } else {
            amount = getLimitedTransferAmount(amount);
            if (mode == ExtractionCountMode.EXACTLY && sourceStack.getCount() < amount) {
               return ItemStack.EMPTY;
            } else {
               int toMove = mode == ExtractionCountMode.EXACTLY ? amount : Math.min(amount, sourceStack.getCount());
               if (toMove <= 0) {
                  return ItemStack.EMPTY;
               } else {
                  ItemStack extracted = sourceStack.copyWithCount(toMove);
                  sourceStack.shrink(toMove);
                  if (sourceStack.isEmpty()) {
                     sourceEntity.discard();
                  } else {
                     sourceEntity.setItem(sourceStack);
                  }

                  return this.deliverToReceiver(level, extracted, target, receiverPos);
               }
            }
         }
      } else {
         return ItemStack.EMPTY;
      }
   }

   private boolean canTransferStack(ItemStack stack, EnderChuteBlockEntity receiver) {
      return !stack.isEmpty() && this.canAcceptItem(stack) && receiver.canAcceptItem(stack);
   }

   private static int getLimitedTransferAmount(int amount) {
      return amount <= 0 ? 1 : Math.min(amount, 16);
   }

   private ItemStack deliverToReceiver(ServerLevel level, ItemStack extracted, IItemHandler target, BlockPos receiverPos) {
      if (extracted.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack remainder = extracted.copy();
         if (target != null) {
            remainder = ItemHandlerHelper.insertItem(target, extracted.copy(), false);
         }

         int inserted = extracted.getCount() - remainder.getCount();
         if (!remainder.isEmpty()) {
            spawnAtReceiverBottom(level, receiverPos, remainder.copy());
         }

         int delivered = inserted + remainder.getCount();
         return delivered > 0 ? extracted.copyWithCount(delivered) : ItemStack.EMPTY;
      }
   }

   private ItemEntity findSourceItemEntity(ServerLevel level) {
      Vec3 center = Vec3.atCenterOf(this.worldPosition.above());
      AABB scan = new AABB(center.x - 0.5, center.y - 0.5, center.z - 0.5, center.x + 0.5, center.y + 0.5, center.z + 0.5);
      List<ItemEntity> entities = level.getEntitiesOfClass(ItemEntity.class, scan, Entity::isAlive);
      return entities.isEmpty() ? null : entities.get(0);
   }

   private static void spawnAtReceiverBottom(ServerLevel level, BlockPos chutePos, ItemStack stack) {
      Vec3 spawnPos = Vec3.atCenterOf(chutePos.below()).add(0.0, 0.2, 0.0);
      ItemEntity entity = new ItemEntity(level, spawnPos.x, spawnPos.y, spawnPos.z, stack);
      entity.setDeltaMovement(0.0, -0.08, 0.0);
      entity.setDefaultPickUpDelay();
      level.addFreshEntity(entity);
   }

   private static IItemHandler findSourceHandler(ServerLevel level, BlockPos chutePos) {
      return getItemHandlerAt(level, chutePos.above(), Direction.DOWN);
   }

   private static IItemHandler findTargetHandler(ServerLevel level, BlockPos chutePos) {
      return getItemHandlerAt(level, chutePos.below(), Direction.UP);
   }

   private static LinkBehaviour getRedstoneLinkNear(ServerLevel level, BlockPos chutePos, boolean shouldListen) {
      List<BlockPos> candidates = new ArrayList<>();
      candidates.add(chutePos.above());

      for (Direction direction : Direction.values()) {
         candidates.add(chutePos.relative(direction));
      }

      for (BlockPos pos : candidates) {
         BlockEntity be = level.getBlockEntity(pos);
         if (be instanceof RedstoneLinkBlockEntity) {
            RedstoneLinkBlockEntity linkBlockEntity = (RedstoneLinkBlockEntity)be;
            LinkBehaviour link = (LinkBehaviour)linkBlockEntity.getBehaviour(LinkBehaviour.TYPE);
            if (link != null && link.isListening() == shouldListen) {
               Couple<Frequency> key = link.getNetworkKey();
               if (key != null && key.getFirst() != Frequency.EMPTY && key.getSecond() != Frequency.EMPTY) {
                  return link;
               }
            }
         }
      }

      return null;
   }

   private static boolean hasAttachedRedstoneLink(ServerLevel level, BlockPos chutePos) {
      List<BlockPos> candidates = new ArrayList<>();
      candidates.add(chutePos.above());

      for (Direction direction : Direction.values()) {
         candidates.add(chutePos.relative(direction));
      }

      for (BlockPos pos : candidates) {
         if (level.getBlockEntity(pos) instanceof RedstoneLinkBlockEntity) {
            return true;
         }
      }

      return false;
   }

   private static BlockPos findAttachedChutePos(ServerLevel level, BlockPos linkPos) {
      List<BlockPos> priority = new ArrayList<>();
      priority.add(linkPos.below());

      for (Direction direction : Direction.values()) {
         priority.add(linkPos.relative(direction));
      }

      for (BlockPos pos : priority) {
         if (level.getBlockEntity(pos) instanceof EnderChuteBlockEntity) {
            return pos;
         }
      }

      return null;
   }

   private static IItemHandler getItemHandlerAt(ServerLevel level, BlockPos pos, Direction accessSide) {
      IItemHandler sided = (IItemHandler)level.getCapability(ItemHandler.BLOCK, pos, accessSide);
      if (sided != null) {
         return sided;
      } else {
         IItemHandler unsided = (IItemHandler)level.getCapability(ItemHandler.BLOCK, pos, null);
         if (unsided != null) {
            return unsided;
         } else {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof WorldlyContainer worldlyContainer) {
               return new SidedInvWrapper(worldlyContainer, accessSide);
            } else {
               return be instanceof Container container ? new InvWrapper(container) : null;
            }
         }
      }
   }
}
