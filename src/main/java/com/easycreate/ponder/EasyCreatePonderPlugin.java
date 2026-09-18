package com.easycreate.ponder;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class EasyCreatePonderPlugin implements PonderPlugin {
   private static final ResourceLocation ENDER_CHUTE_ID = ResourceLocation.fromNamespaceAndPath("easycreate", "ender_chute");

   public String getModId() {
      return "easycreate";
   }

   public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
      helper.forComponents(id("ender_fluid_tank"))
         .addStoryBoard("ender_tank_basics", EnderDeviceScenes::tankBasics, AllCreatePonderTags.FLUIDS)
         .addStoryBoard("ender_tank_wireless", EnderDeviceScenes::tankWireless, AllCreatePonderTags.REDSTONE);
      helper.forComponents(id("ender_speed_controller"))
         .addStoryBoard("ender_controller_basics", EnderDeviceScenes::controllerBasics, AllCreatePonderTags.KINETIC_RELAYS)
         .addStoryBoard("ender_controller_wireless", EnderDeviceScenes::controllerWireless, AllCreatePonderTags.REDSTONE);
      helper.addStoryBoard(
         ENDER_CHUTE_ID,
         ResourceLocation.fromNamespaceAndPath("easycreate", "ender_chute_basics"),
         EnderChuteScenes::basic,
         new ResourceLocation[]{AllCreatePonderTags.LOGISTICS}
      );
      helper.addStoryBoard(
         ENDER_CHUTE_ID, ResourceLocation.fromNamespaceAndPath("easycreate", "ender_chute_wireless"), EnderChuteScenes::wireless, new ResourceLocation[0]
      );
   }

   public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
      helper.addTagToComponent(id("ender_fluid_tank"), AllCreatePonderTags.FLUIDS);
      helper.addTagToComponent(id("ender_speed_controller"), AllCreatePonderTags.KINETIC_RELAYS);
      helper.addTagToComponent(ENDER_CHUTE_ID, AllCreatePonderTags.LOGISTICS);
   }
   private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("easycreate", path); }
}
