package net.neversnows.trashcanmod.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neversnows.trashcanmod.block.entity.TrashCanBlockEntity;
import org.jetbrains.annotations.NotNull;

public class TrashCanMenu extends AbstractContainerMenu {
    public static final int SLOTS_PER_ROW = 3;
    public static final int NUMBER_OF_ROWS = 5;
    private final TrashCanBlockEntity blockEntity;

    // Client Constructor
    public TrashCanMenu(int containerId, Inventory playerInv, FriendlyByteBuf additionalData) {
        this(containerId, playerInv, playerInv.player.level().getBlockEntity(additionalData.readBlockPos()));
    }

    // Server Constructor
    public TrashCanMenu(int containerId, Inventory playerInv, BlockEntity blockEntity) {
        super(ModMenuTypes.TRASH_CAN_MENU.get(), containerId);

        if(blockEntity instanceof TrashCanBlockEntity trashCanBlockEntity) {
            this.blockEntity = trashCanBlockEntity;
            trashCanBlockEntity.startOpen(playerInv.player);
        } else {
            throw new IllegalStateException("Incorrect BlockEntity class passed into " + this.getClass().getName() + ": " + blockEntity.getClass().getCanonicalName());
        }

        createPlayerHotbar(playerInv);
        createPlayerInventory(playerInv);
        createBlockEntityInventory(trashCanBlockEntity);
    }

    private void createBlockEntityInventory(TrashCanBlockEntity blockEntity) {
        for (int row = 0; row < NUMBER_OF_ROWS; row++) {
            for (int column = 0; column < SLOTS_PER_ROW; column++) {
                addSlot(new Slot(blockEntity, column + (row * SLOTS_PER_ROW), 62 + (column * 18), 47 + (row * 18)));
            }
        }
    }

    private void createPlayerInventory(Inventory playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInv, 9 + column + (row * 9), 8 + (column * 18), 157 + (row * 18)));
            }
        }
    }

    private void createPlayerHotbar(Inventory playerInv) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInv, column, 8 + (column * 18), 215));
        }
    }

    @Override
    @NotNull
    public ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        Slot fromSlot = getSlot(pIndex);
        ItemStack fromStack = fromSlot.getItem();
        int blockEntityInvSize = blockEntity.getContainerSize();

        if(fromStack.getCount() <= 0)
            fromSlot.set(ItemStack.EMPTY);

        if(!fromSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack copyFromStack = fromStack.copy();

        if(pIndex < 36) {
            // Inside the player's inventory
            if(!moveItemStackTo(fromStack, 36, 36 + blockEntityInvSize, false))
                return ItemStack.EMPTY;
        } else if (pIndex < 36 + blockEntityInvSize) {
            // Inside the block entity inventory
            if(!moveItemStackTo(fromStack, 0, 36, false))
                return ItemStack.EMPTY;
        } else {
            System.err.println("Invalid slot index: " + pIndex);
            return ItemStack.EMPTY;
        }

        fromSlot.setChanged();
        fromSlot.onTake(pPlayer, fromStack);

        return copyFromStack;
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return this.blockEntity.stillValid(pPlayer);
    }
    public void removed(@NotNull Player pPlayer) {
        super.removed(pPlayer);
        this.blockEntity.stopOpen(pPlayer);
    }

    public TrashCanBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

}
