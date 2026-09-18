package com.easycreate.content;

import com.easycreate.ModBlockEntities;
import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class EnderFluidTankBlock extends FluidTankBlock {
    public EnderFluidTankBlock(Properties properties) { super(properties, false); }

    @Override
    public BlockEntityType<? extends FluidTankBlockEntity> getBlockEntityType() {
        return ModBlockEntities.ENDER_FLUID_TANK.get();
    }
}
