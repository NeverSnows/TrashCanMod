package net.neversnows.trashcanmod.client.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.neversnows.trashcanmod.TrashCanMod;

public class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TrashCanMod.MOD_ID);
    public static final RegistryObject<SoundEvent> TRASH_CAN_OPEN = registerSoundEvent("trash_can_open");
    public static final RegistryObject<SoundEvent> TRASH_CAN_CLOSE = registerSoundEvent("trash_can_close");


    private static RegistryObject<SoundEvent> registerSoundEvent(String name){
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TrashCanMod.MOD_ID, name)));
    }
    public static void register(IEventBus eventBus){
        SOUND_EVENTS.register(eventBus);
    }
}
