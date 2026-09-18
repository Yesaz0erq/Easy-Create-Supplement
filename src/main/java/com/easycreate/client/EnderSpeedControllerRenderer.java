package com.easycreate.client;

import com.easycreate.content.EnderSpeedControllerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlock;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class EnderSpeedControllerRenderer extends SmartBlockEntityRenderer<EnderSpeedControllerBlockEntity> {
    public EnderSpeedControllerRenderer(BlockEntityRendererProvider.Context context) { super(context); }

    @Override
    protected void renderSafe(EnderSpeedControllerBlockEntity be, float ticks, PoseStack pose,
                              MultiBufferSource buffers, int light, int overlay) {
        super.renderSafe(be, ticks, pose, buffers, light, overlay);
        var builder = buffers.getBuffer(Sheets.solidBlockSheet());
        // This custom type has no Flywheel visual: always draw its moving shaft here.
        KineticBlockEntityRenderer.renderRotatingBuffer(be,
                CachedBuffers.block(KineticBlockEntityRenderer.KINETIC_BLOCK,
                        KineticBlockEntityRenderer.shaft(KineticBlockEntityRenderer.getRotationAxisOf(be))),
                pose, builder, light);
        var above = be.getLevel().getBlockState(be.getBlockPos().above());
        if (!ICogWheel.isLargeCog(above) || !above.getValue(CogWheelBlock.AXIS).isHorizontal()) return;
        boolean alongX = be.getBlockState().getValue(SpeedControllerBlock.HORIZONTAL_AXIS) == Direction.Axis.X;
        CachedBuffers.partial(AllPartialModels.SPEED_CONTROLLER_BRACKET, be.getBlockState())
                .translate(0, 1, 0).rotateCentered((float) (alongX ? Math.PI : Math.PI / 2), Direction.UP)
                .color(EnderModelSetup.ENDER_COLOR).light(LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().above()))
                .renderInto(pose, builder);
    }
}

