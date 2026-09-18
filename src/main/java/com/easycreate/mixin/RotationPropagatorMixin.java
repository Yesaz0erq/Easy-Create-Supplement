package com.easycreate.mixin;

import com.easycreate.content.EnderSpeedControllerBlock;
import com.easycreate.content.EnderSpeedControllerBlockEntity;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RotationPropagator.class, remap = false)
public abstract class RotationPropagatorMixin {
    @Inject(method = "isLargeCogToSpeedController", at = @At("HEAD"), cancellable = true)
    private static void easycreate$controller(BlockState from, BlockState to, BlockPos diff,
                                             CallbackInfoReturnable<Boolean> cir) {
        if (!(to.getBlock() instanceof EnderSpeedControllerBlock)) return;
        cir.setReturnValue(ICogWheel.isLargeCog(from) && diff.equals(BlockPos.ZERO.below())
                && from.getValue(CogWheelBlock.AXIS).isHorizontal()
                && to.getValue(SpeedControllerBlock.HORIZONTAL_AXIS) != from.getValue(CogWheelBlock.AXIS));
    }

    @Inject(method = "getConveyedSpeed", at = @At("HEAD"), cancellable = true)
    private static void easycreate$wirelessSpeed(KineticBlockEntity from, KineticBlockEntity to,
                                               CallbackInfoReturnable<Float> cir) {
        if (from instanceof EnderSpeedControllerBlockEntity sender
                && to instanceof EnderSpeedControllerBlockEntity receiver && sender.hasWirelessPeer(receiver))
            cir.setReturnValue(sender.conveyedWirelessSpeed(receiver));
    }
}
