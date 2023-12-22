package net.neversnows.trashcanmod.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.neversnows.trashcanmod.TrashCanMod;
import net.neversnows.trashcanmod.block.ModBlocks;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TrashCanMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TRASH_CAN_TAB = CREATIVE_MODE_TABS.register("trash_can_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.TRASH_CAN.get()))
                    .title(Component.translatable("creativetab.trash_can_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModBlocks.TRASH_CAN.get());
                    }).build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
