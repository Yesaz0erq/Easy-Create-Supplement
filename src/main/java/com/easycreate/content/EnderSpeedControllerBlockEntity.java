package com.easycreate.content;

import com.easycreate.ModBlockEntities;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class EnderSpeedControllerBlockEntity extends SpeedControllerBlockEntity {
    private Set<BlockPos> wirelessPeers = Set.of();
    private boolean receiving;
    private boolean wirelessInitialized;

    public EnderSpeedControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENDER_SPEED_CONTROLLER.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level instanceof ServerLevel server)) return;
        // Re-evaluate loaded endpoints and frequency/mode changes without forcing chunks.
        var link = EnderWireless.linkAt(server, worldPosition);
        boolean nextReceiving = link != null && link.isListening();
        Set<BlockPos> next = new LinkedHashSet<>();
        for (var peer : EnderWireless.peers(server, worldPosition, EnderSpeedControllerBlockEntity.class))
            next.add(peer.getBlockPos());
        if (wirelessInitialized && receiving == nextReceiving && wirelessPeers.equals(next)) return;
        wirelessInitialized = true;

        // Detach using OLD edges so dependent shafts lose their source before reconnection.
        detachKinetics();
        removeSource();
        wirelessPeers = next;
        receiving = nextReceiving;
        attachKinetics();
        sendData();
    }

    public boolean hasWirelessPeer(KineticBlockEntity other) {
        return other instanceof EnderSpeedControllerBlockEntity peer
                && (wirelessPeers.contains(other.getBlockPos()) || peer.wirelessPeers.contains(worldPosition));
    }

    public float conveyedWirelessSpeed(EnderSpeedControllerBlockEntity to) {
        if (getTheoreticalSpeed() == 0 || receiving == to.receiving) return 0;
        // The reverse edge reflects the real transmitter's speed. It never creates a source.
        return receiving ? to.getTheoreticalSpeed() : to.targetSpeed.getValue();
    }

    @Override
    public List<BlockPos> addPropagationLocations(IRotate block, BlockState state, List<BlockPos> neighbours) {
        super.addPropagationLocations(block, state, neighbours);
        for (BlockPos peer : wirelessPeers)
            if (level.isLoaded(peer) && !neighbours.contains(peer)) neighbours.add(peer);
        return neighbours;
    }

    @Override
    public boolean isCustomConnection(KineticBlockEntity other, BlockState state, BlockState otherState) {
        return hasWirelessPeer(other);
    }

    @Override
    protected Block getStressConfigKey() { return AllBlocks.ROTATION_SPEED_CONTROLLER.get(); }
}
