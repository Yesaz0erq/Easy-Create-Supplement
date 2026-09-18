package com.easycreate.content;

import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/** Same dimension, loaded endpoints, two nonempty frequencies, like the ender chute. */
public final class EnderWireless {
    private EnderWireless() {}

    public static List<BlockPos> adjacent(BlockPos pos) {
        var positions = new LinkedHashSet<BlockPos>();
        positions.add(pos.above());
        for (Direction direction : Direction.values()) positions.add(pos.relative(direction));
        return List.copyOf(positions);
    }

    public static LinkBehaviour linkAt(ServerLevel level, BlockPos machine) {
        for (BlockPos pos : adjacent(machine)) {
            if (!level.isLoaded(pos)) continue;
            if (level.getBlockEntity(pos) instanceof RedstoneLinkBlockEntity be) {
                LinkBehaviour link = be.getBehaviour(LinkBehaviour.TYPE);
                if (link != null && link.getNetworkKey().getFirst() != Frequency.EMPTY
                        && link.getNetworkKey().getSecond() != Frequency.EMPTY) return link;
            }
        }
        return null;
    }

    public static <T extends BlockEntity> List<T> peers(ServerLevel level, BlockPos pos, Class<T> type) {
        LinkBehaviour own = linkAt(level, pos);
        if (own == null) return List.of();
        var found = new LinkedHashSet<T>();
        // Snapshot: loading/removing block entities can change Create's network collection.
        for (var remote : new ArrayList<>(Create.REDSTONE_LINK_NETWORK_HANDLER.getNetworkOf(level, own))) {
            if (remote == own || !remote.isAlive() || remote.isListening() == own.isListening()) continue;
            var candidates = new LinkedHashSet<BlockPos>();
            candidates.add(remote.getLocation().below());
            candidates.addAll(adjacent(remote.getLocation()));
            for (BlockPos candidate : candidates) {
                if (candidate.equals(pos) || !level.isLoaded(candidate)) continue;
                BlockEntity be = level.getBlockEntity(candidate);
                if (!type.isInstance(be)) continue;
                LinkBehaviour selected = linkAt(level, candidate);
                if (selected == remote && selected.getNetworkKey().equals(own.getNetworkKey())) {
                    found.add(type.cast(be));
                    break;
                }
            }
        }
        return List.copyOf(found);
    }
}
