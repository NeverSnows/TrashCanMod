package net.neversnows.trashcanmod.client.handler;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.neversnows.trashcanmod.block.entity.ModBlockEntities;
import net.neversnows.trashcanmod.block.entity.renderer.TrashCanBlockEntityRenderer;
import net.neversnows.trashcanmod.client.screen.TrashCanScreen;
import net.neversnows.trashcanmod.TrashCanMod;
import net.neversnows.trashcanmod.menu.ModMenuTypes;

@Mod.EventBusSubscriber(modid = TrashCanMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModHandler {
    @SubscribeEvent
    public static void ClientSetup(FMLClientSetupEvent event){
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.TRASH_CAN_MENU.get(), TrashCanScreen::new);
        });
    }
    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(ModBlockEntities.TRASH_CAN_BLOCK_ENTITY.get(), TrashCanBlockEntityRenderer::new);
    }
}
