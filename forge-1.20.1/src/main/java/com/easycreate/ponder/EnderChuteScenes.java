package com.easycreate.ponder;

import static com.easycreate.ponder.EnderDeviceScenes.*;
import com.simibubi.create.content.logistics.chute.SmartChuteBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

/** Step-by-step scenes following Create's Smart Chute and Redstone Link tutorials. */
public final class EnderChuteScenes {
    private EnderChuteScenes() {}
    private static void inventory(CreateSceneBuilder scene, BlockPos pos, int count) {
        scene.world().modifyBlockEntity(pos, ChestBlockEntity.class,
                chest -> chest.setItem(0, count == 0 ? ItemStack.EMPTY : new ItemStack(Items.IRON_INGOT, count)));
    }
    public static void basic(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = new CreateSceneBuilder(builder);
        start(scene, "ender_chute_basics", "Filtering and moving items downward");
        BlockPos chute = util.grid().at(3, 2, 2);
        scene.world().showSection(util.select().fromTo(3, 1, 2, 3, 2, 2), Direction.DOWN);
        caption(scene, util, chute, "Without a Redstone Link, an Ender Chute works like Create's Smart Chute.");
        scene.world().showSection(util.select().position(chute.above()), Direction.DOWN);
        inventory(scene, chute.above(), 16);
        scene.world().createItemOnBeltLike(chute, Direction.UP, new ItemStack(Items.IRON_INGOT));
        scene.idle(20);
        inventory(scene, chute.above(), 0);
        inventory(scene, chute.below(), 16);
        caption(scene, util, chute.below(), "Items enter from above and move into the inventory below.");
        var filter = util.vector().blockSurface(chute, Direction.NORTH).add(0, 3 / 16f, 0);
        scene.overlay().showFilterSlotInput(filter, Direction.NORTH, 70);
        scene.overlay().showControls(filter, Pointing.DOWN, 50).rightClick().withItem(new ItemStack(Items.IRON_INGOT));
        scene.world().setFilterData(util.select().position(chute), SmartChuteBlockEntity.class, new ItemStack(Items.IRON_INGOT));
        caption(scene, util, chute, "Set a filter to select items. Use its value panel to choose the extraction amount.");
        scene.world().toggleRedstonePower(util.select().position(chute));
        scene.effects().indicateRedstone(chute);
        caption(scene, util, chute, "Redstone power pauses the chute. Remove the signal to allow items through again.");
        scene.world().toggleRedstonePower(util.select().position(chute));
        scene.markAsFinished();
    }
    public static void wireless(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = new CreateSceneBuilder(builder);
        start(scene, "ender_chute_wireless", "Transferring items wirelessly");
        BlockPos a = util.grid().at(1, 2, 2), b = util.grid().at(5, 2, 2);
        scene.world().showSection(util.select().fromTo(1, 2, 2, 1, 3, 2)
                .add(util.select().fromTo(5, 1, 2, 5, 2, 2)), Direction.DOWN);
        inventory(scene, a.above(), 16);
        caption(scene, util, a, "Put the source inventory above the sending chute and the destination below the receiving chute.");
        frequencies(scene, util, a.north(), b.north());
        scene.overlay().showLine(PonderPalette.GREEN, util.vector().centerOf(a.north()), util.vector().centerOf(b.north()), 90);
        for (int i = 1; i <= 4; i++) {
            inventory(scene, a.above(), 16 - i * 4);
            scene.world().createItemOnBeltLike(b, Direction.UP, new ItemStack(Items.IRON_INGOT, 4));
            scene.idle(12);
            inventory(scene, b.below(), i * 4);
        }
        caption(scene, util, b, "Items move from the transmitter to the receiver. Each operation transfers at most 16 items.");
        scene.overlay().showFilterSlotInput(util.vector().blockSurface(b, Direction.NORTH), Direction.NORTH, 70);
        caption(scene, util, b, "Both filters must accept the item. The receiver's value panel sets the amount for each transfer.");
        caption(scene, util, b.below(), "Place an inventory below the receiver to collect items. Without one, items drop out below it.");
        caption(scene, util, a, "Both ends must be loaded in the same dimension. There is no distance limit, and the chutes do not load chunks.");
        scene.markAsFinished();
    }
}
