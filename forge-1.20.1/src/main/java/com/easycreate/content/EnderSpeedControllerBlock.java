package com.easycreate.content;

import com.easycreate.ModBlockEntities;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlock;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class EnderSpeedControllerBlock extends SpeedControllerBlock {
    public EnderSpeedControllerBlock(Properties properties) { super(properties); }

    @Override
    public BlockEntityType<? extends SpeedControllerBlockEntity> getBlockEntityType() {
        return ModBlockEntities.ENDER_SPEED_CONTROLLER.get();
    }
}
