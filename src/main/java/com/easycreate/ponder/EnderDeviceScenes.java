package com.easycreate.ponder;

import com.easycreate.content.EnderFluidTankBlockEntity;
import com.easycreate.content.EnderSpeedControllerBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlock;
import com.simibubi.create.content.redstone.link.RedstoneLinkBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

/** Uses the official FluidTankScenes, KineticsScenes and RedstoneScenes animation patterns. */
public final class EnderDeviceScenes {
    static void start(CreateSceneBuilder scene, String id, String title) {
        scene.title(id, title);
        scene.configureBasePlate(0, 0, 7);
        scene.scaleSceneView(0.8f);
        scene.showBasePlate();
        scene.idle(10);
    }

    static void caption(CreateSceneBuilder scene, SceneBuildingUtil util, BlockPos pos, String text) {
        scene.overlay().showText(80).text(text).attachKeyFrame()
                .pointAt(util.vector().centerOf(pos)).placeNearTarget();
        scene.idle(90);
    }

    private static void fluid(CreateSceneBuilder scene, BlockPos pos, int amount) {
        scene.world().modifyBlockEntity(pos, FluidTankBlockEntity.class, be -> {
            be.getTankInventory().setFluid(amount == 0 ? FluidStack.EMPTY : new FluidStack(Fluids.WATER, amount));
        });
    }

    public static void tankBasics(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = new CreateSceneBuilder(builder);
        start(scene, "ender_tank_basics", "Storing fluids in an Ender Fluid Tank");
        BlockPos tank = util.grid().at(3, 1, 2);
        scene.world().showSection(util.select().position(tank), Direction.DOWN);
        fluid(scene, tank, 4000);
        caption(scene, util, tank, "Ender Fluid Tanks store fluids just like Create's Fluid Tanks.");
        scene.world().showSection(util.select().fromTo(0, 1, 2, 2, 1, 2), Direction.EAST);
        scene.world().setKineticSpeed(util.select().position(1, 1, 2), 32);
        scene.world().propagatePipeChange(util.grid().at(1, 1, 2));
        for (int i = 1; i <= 4; i++) { fluid(scene, tank, 4000 + i * 1000); scene.idle(8); }
        caption(scene, util, util.grid().at(2, 1, 2), "Use pipes and powered pumps to fill or drain the tank from any side.");
        scene.world().setKineticSpeed(util.select().position(1, 1, 2), -32);
        for (int i = 1; i <= 4; i++) { fluid(scene, tank, 8000 - i * 1000); scene.idle(8); }
        caption(scene, util, tank, "Matching tanks can form larger tanks. Wireless transfer uses their shared fluid storage.");
        scene.markAsFinished();
    }

    static void frequencies(CreateSceneBuilder scene, SceneBuildingUtil util, BlockPos a, BlockPos b) {
        scene.world().showSection(util.select().position(a).add(util.select().position(b)), Direction.DOWN);
        scene.overlay().showControls(util.vector().centerOf(b), Pointing.DOWN, 50).rightClick().whileSneaking();
        scene.idle(15);
        scene.world().modifyBlock(b, s -> s.setValue(RedstoneLinkBlock.RECEIVER, true), false);
        scene.world().modifyBlockEntityNBT(util.select().position(b), RedstoneLinkBlockEntity.class,
                nbt -> nbt.putBoolean("Transmitter", false));
        caption(scene, util, b, "Keep the first link in transmit mode. Sneak-right-click the other link to receive.");
        for (BlockPos pos : new BlockPos[]{a, b}) {
            scene.overlay().showControls(util.vector().centerOf(pos), Pointing.DOWN, 35).withItem(new ItemStack(Items.DIAMOND));
            scene.world().modifyBlockEntityNBT(util.select().position(pos), RedstoneLinkBlockEntity.class,
                    nbt -> nbt.put("FrequencyFirst", new ItemStack(Items.DIAMOND).saveOptional(scene.world().getHolderLookupProvider())));
            scene.idle(20);
            scene.overlay().showControls(util.vector().centerOf(pos), Pointing.DOWN, 35).withItem(new ItemStack(Items.EMERALD));
            scene.world().modifyBlockEntityNBT(util.select().position(pos), RedstoneLinkBlockEntity.class,
                    nbt -> nbt.put("FrequencyLast", new ItemStack(Items.EMERALD).saveOptional(scene.world().getHolderLookupProvider())));
            scene.idle(20);
        }
        caption(scene, util, a, "Set the same two frequency items in the same order on both links. Neither slot may be empty.");
    }

    public static void tankWireless(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = new CreateSceneBuilder(builder);
        start(scene, "ender_tank_wireless", "Transferring fluids wirelessly");
        BlockPos a = util.grid().at(1, 1, 2), b = util.grid().at(5, 1, 2);
        scene.world().showSection(util.select().fromTo(0, 1, 2, 6, 1, 2), Direction.UP);
        fluid(scene, a, 8000);
        caption(scene, util, a, "Place a Redstone Link next to each Ender Fluid Tank, as with Ender Chutes.");
        frequencies(scene, util, a.above(), b.above());
        scene.overlay().showLine(PonderPalette.GREEN, util.vector().centerOf(a.above()), util.vector().centerOf(b.above()), 85);
        for (int i = 1; i <= 8; i++) {
            fluid(scene, a, 8000 - i * 1000); fluid(scene, b, i * 1000); scene.idle(8);
        }
        caption(scene, util, b, "Fluid moves from sender to receiver, up to 1 bucket per tick per sending multiblock. No fluid is created.");
        scene.overlay().showOutline(PonderPalette.RED, new Object(), util.select().position(b), 80);
        caption(scene, util, b, "A full tank or a different fluid stops transfer without losing the sender's fluid.");
        caption(scene, util, a, "Both ends must be loaded in the same dimension. The Ender connection has no distance limit and does not load chunks.");
        scene.markAsFinished();
    }

    public static void controllerBasics(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = new CreateSceneBuilder(builder);
        start(scene, "ender_controller_basics", "Adjusting rotational speed");
        BlockPos controller = util.grid().at(3, 1, 2), cog = controller.above();
        scene.world().showSection(util.select().fromTo(2, 1, 2, 3, 1, 2), Direction.DOWN);
        scene.world().setKineticSpeed(util.select().fromTo(2, 1, 2, 3, 1, 2), 16);
        caption(scene, util, controller, "Without a wireless link, the Ender controller works like Create's Rotation Speed Controller.");
        scene.world().showSection(util.select().fromTo(3, 2, 2, 3, 2, 3), Direction.DOWN);
        scene.world().setKineticSpeed(util.select().fromTo(3, 2, 2, 3, 2, 3), 16);
        caption(scene, util, cog, "Place a Large Cogwheel above it, with its horizontal axis perpendicular to the controller's shaft.");
        scene.overlay().showFilterSlotInput(util.vector().of(3.5, 1.7, 2), Direction.NORTH, 70);
        scene.overlay().showControls(util.vector().of(3.5, 1.7, 2), Pointing.DOWN, 50).rightClick();
        scene.world().modifyBlockEntity(controller, EnderSpeedControllerBlockEntity.class, be -> be.targetSpeed.setValue(64));
        scene.world().setKineticSpeed(util.select().fromTo(3, 2, 2, 3, 2, 3), 64);
        scene.effects().rotationSpeedIndicator(cog);
        caption(scene, util, controller, "Use the side value panel to set the target RPM. The connected cogwheel changes speed.");
        scene.world().modifyBlockEntity(controller, EnderSpeedControllerBlockEntity.class, be -> be.targetSpeed.setValue(-32));
        scene.world().setKineticSpeed(util.select().fromTo(3, 2, 2, 3, 2, 3), -32);
        caption(scene, util, cog, "Negative RPM reverses rotation. A real power source and sufficient stress capacity are still required.");
        scene.markAsFinished();
    }

    public static void controllerWireless(SceneBuilder builder, SceneBuildingUtil util) {
        var scene = new CreateSceneBuilder(builder);
        start(scene, "ender_controller_wireless", "Transmitting rotational power wirelessly");
        BlockPos a = util.grid().at(1, 1, 2), b = util.grid().at(5, 1, 2);
        scene.world().showSection(util.select().fromTo(0, 1, 2, 6, 1, 2), Direction.UP);
        scene.world().setKineticSpeed(util.select().fromTo(0, 1, 2, 1, 1, 2), 16);
        scene.world().setKineticSpeed(util.select().fromTo(5, 1, 2, 6, 1, 2), 0);
        caption(scene, util, a, "Connect the transmitting controller to a real power source. The receiving shaft starts stopped.");
        frequencies(scene, util, a.above(), b.above());
        scene.overlay().showLine(PonderPalette.GREEN, util.vector().centerOf(a.above()), util.vector().centerOf(b.above()), 90);
        scene.world().setKineticSpeed(util.select().fromTo(5, 1, 2, 6, 1, 2), 16);
        scene.effects().rotationSpeedIndicator(b);
        caption(scene, util, b, "Matching links connect the two controllers. The receiving shaft now outputs rotation.");
        scene.overlay().showControls(util.vector().of(5.5, 1.7, 2), Pointing.DOWN, 50).rightClick();
        scene.world().modifyBlockEntity(b, EnderSpeedControllerBlockEntity.class, be -> be.targetSpeed.setValue(64));
        scene.world().setKineticSpeed(util.select().fromTo(5, 1, 2, 6, 1, 2), 64);
        scene.effects().rotationSpeedIndicator(b);
        caption(scene, util, b, "Adjust the receiver's target RPM. Its machines consume stress capacity from the sender's network.");
        scene.world().hideSection(util.select().position(a.above()), Direction.UP);
        scene.idle(15);
        scene.world().setBlock(a.above(), Blocks.AIR.defaultBlockState(), false);
        scene.world().setKineticSpeed(util.select().fromTo(5, 1, 2, 6, 1, 2), 0);
        caption(scene, util, b, "Removing a link, changing its frequency or stopping the power source stops the receiver.");
        caption(scene, util, a, "Like Ender Chutes, this works between loaded endpoints in one dimension, without a distance limit.");
        scene.markAsFinished();
    }
}
