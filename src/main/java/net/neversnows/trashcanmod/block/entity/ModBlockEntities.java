package net.neversnows.trashcanmod.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.neversnows.trashcanmod.TrashCanMod;
import net.neversnows.trashcanmod.block.ModBlocks;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TrashCanMod.MOD_ID);
    public static final RegistryObject<BlockEntityType<TrashCanBlockEntity>> TRASH_CAN_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("trash_can_block_entity",
                    () -> BlockEntityType.Builder.of(TrashCanBlockEntity::new, ModBlocks.TRASH_CAN.get()).build(null));
    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}
