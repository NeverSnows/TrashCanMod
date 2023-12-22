package net.neversnows.trashcanmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neversnows.trashcanmod.block.ModBlocks;
import net.neversnows.trashcanmod.block.entity.ModBlockEntities;
import net.neversnows.trashcanmod.item.ModItems;
import net.neversnows.trashcanmod.menu.ModMenuTypes;
import net.neversnows.trashcanmod.client.sound.ModSoundEvents;
import net.neversnows.trashcanmod.item.ModCreativeModeTabs;

@Mod(TrashCanMod.MOD_ID)
public class TrashCanMod {
    public static final String MOD_ID = "trashcanmod";
    public TrashCanMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeModeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModSoundEvents.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }
}
