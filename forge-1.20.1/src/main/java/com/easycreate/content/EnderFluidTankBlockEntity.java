package com.easycreate.content;

import com.easycreate.ModBlockEntities;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import java.util.HashSet;

public class EnderFluidTankBlockEntity extends FluidTankBlockEntity {
    public static final int TRANSFER_PER_TICK = 1000;
    private long lastWirelessTick = Long.MIN_VALUE;

    public EnderFluidTankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENDER_FLUID_TANK.get(), pos, state);
    }

    public IFluidHandler fluidHandler() {
        // Resolve through Create so invalidated multiblock capabilities are refreshed.
        return getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.FLUID_HANDLER).orElseThrow(
                () -> new IllegalStateException("Tank fluid capability unavailable"));
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level instanceof ServerLevel server)) return;
        var link = EnderWireless.linkAt(server, worldPosition);
        if (link == null || link.isListening()) return;
        if (!(getControllerBE() instanceof EnderFluidTankBlockEntity source)) return;
        // One budget per multiblock, even if several parts have transmitting links.
        if (source.lastWirelessTick == server.getGameTime()) return;
        IFluidHandler from = source.fluidHandler();
        int budget = TRANSFER_PER_TICK;
        var visited = new HashSet<BlockPos>();
        for (var peer : EnderWireless.peers(server, worldPosition, EnderFluidTankBlockEntity.class)) {
            var receiver = peer.getControllerBE();
            if (!(receiver instanceof EnderFluidTankBlockEntity target) || target == source
                    || !visited.add(target.getBlockPos())) continue;
            int moved = transfer(from, target.fluidHandler(), budget);
            if (moved > 0) source.lastWirelessTick = server.getGameTime();
            budget -= moved;
            if (budget == 0) break;
        }
    }

    private static net.minecraftforge.fluids.FluidStack withAmount(net.minecraftforge.fluids.FluidStack stack, int amount) {
        var copy = stack.copy();
        copy.setAmount(amount);
        return copy;
    }

    /** Both handlers are our regular SmartFluidTanks; simulate before removing any fluid. */
    public static int transfer(IFluidHandler source, IFluidHandler target, int limit) {
        if (limit <= 0 || source == target) return 0;
        var offered = source.drain(limit, FluidAction.SIMULATE);
        int accepted = target.fill(offered, FluidAction.SIMULATE);
        if (accepted == 0) return 0;
        var drained = source.drain(withAmount(offered, accepted), FluidAction.EXECUTE);
        int filled = target.fill(drained, FluidAction.EXECUTE);
        if (filled < drained.getAmount())
            source.fill(withAmount(drained, drained.getAmount() - filled), FluidAction.EXECUTE);
        return filled;
    }
}

