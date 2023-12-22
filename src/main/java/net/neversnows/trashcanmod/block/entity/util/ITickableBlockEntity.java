package net.neversnows.trashcanmod.block.entity.util;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

public interface ITickableBlockEntity {
    void tick();

    static <T extends BlockEntity>BlockEntityTicker<T> getTickerHelper(Level pLevel){
        return pLevel.isClientSide() ? null : (pLevel1, pPos, pState1, pBlockEntity) -> ((ITickableBlockEntity)pBlockEntity).tick();
    }
}
