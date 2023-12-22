package net.neversnows.trashcanmod.block.entity;

import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neversnows.trashcanmod.block.entity.util.ITickableBlockEntity;
import net.neversnows.trashcanmod.util.SetBlockFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.neversnows.trashcanmod.TrashCanMod;
import net.neversnows.trashcanmod.block.TrashCanBlock;
import net.neversnows.trashcanmod.client.sound.ModSoundEvents;
import net.neversnows.trashcanmod.menu.TrashCanMenu;
import net.neversnows.trashcanmod.util.ModUtils;

import java.util.List;

public class TrashCanBlockEntity extends RandomizableContainerBlockEntity implements ITickableBlockEntity {
    private static final Component TITLE = Component.translatable("container." + TrashCanMod.MOD_ID + ".trash_can");
    private static final int CONTAINER_SIZE = 15;
    private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        protected void onOpen(Level pLevel, BlockPos pPos, BlockState pState) {
            TrashCanBlockEntity.this.openContainer(pState, pPos);
        }
        protected void onClose(Level pLevel, BlockPos pPos, BlockState pState) {
            TrashCanBlockEntity.this.closeContainer(pState, pPos);
        }
        protected void openerCountChanged(Level pLevel, BlockPos pPos, BlockState pState, int pCount, int pOpenCount) {

        }
        protected boolean isOwnContainer(Player pPlayer) {
            if (pPlayer.containerMenu instanceof TrashCanMenu trashCanMenu) {
                return trashCanMenu.getBlockEntity() == TrashCanBlockEntity.this;
            } else {
                return false;
            }
        }
    };

    public TrashCanBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.TRASH_CAN_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        CompoundTag blackSnowsModData = new CompoundTag();

        if (!this.trySaveLootTable(blackSnowsModData)) {
            ContainerHelper.saveAllItems(blackSnowsModData, this.items);
        }
        pTag.put(TrashCanMod.MOD_ID, blackSnowsModData);
    }
    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);

        CompoundTag blackSnowsModData = pTag.getCompound(TrashCanMod.MOD_ID);

        if (!this.tryLoadLootTable(blackSnowsModData)) {
            ContainerHelper.loadAllItems(blackSnowsModData, this.items);
        }
    }
    @Override
    @NotNull
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }
    @Override
    public void setItems(NonNullList<ItemStack> pItemStacks) {
        this.items = pItemStacks;
    }
    @Override
    @NotNull
    public Component getDefaultName() {
        return TITLE;
    }
    @Override
    @NotNull
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new TrashCanMenu(pContainerId, pInventory, this);
    }
    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }
    @Override
    public void startOpen(Player pPlayer) {
        if (!this.remove && !pPlayer.isSpectator()) {
            this.openersCounter.incrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }
    @Override
    public void stopOpen(Player pPlayer) {
        if (!this.remove && !pPlayer.isSpectator()) {
            this.openersCounter.decrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }
    @Override
    public void setItem(int pIndex, ItemStack pStack) {
        super.setItem(pIndex, pStack);
        if(!getBlockState().getValue(TrashCanBlock.OPEN)){
            deleteContents();
        }else{
            int flags = SetBlockFlags.getCompoundFlag(SetBlockFlags.UPDATE_CLIENTS, SetBlockFlags.UPDATE_BLOCKS);
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), flags);
        }
    }

    private void openContainer(BlockState pState, BlockPos pPos){
        if(!pState.getValue(TrashCanBlock.OPEN)){
            playSound(pPos, ModSoundEvents.TRASH_CAN_OPEN.get());
        }
        updateBlockState(pState, true);
    }

    private void closeContainer(BlockState pState, BlockPos pPos){
        if(pState.getValue(TrashCanBlock.OPEN)) {
            playSound(pPos, ModSoundEvents.TRASH_CAN_CLOSE.get());
        }
        updateBlockState(pState, false);
        deleteContents();
        captureItemsInsideBoundingBox();
    }
    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    private void updateBlockState(BlockState pState, boolean pOpen) {
        int flags = SetBlockFlags.getCompoundFlag(SetBlockFlags.UPDATE_BLOCKS, SetBlockFlags.UPDATE_CLIENTS);

        this.level.setBlock(this.getBlockPos(), pState.setValue(TrashCanBlock.OPEN, Boolean.valueOf(pOpen)), flags);
    }

    public void swapOpenState(BlockState pState){
        if(openersCounter.getOpenerCount() <= 0){
            if(pState.getValue(TrashCanBlock.OPEN)){
                closeContainer(pState, getBlockPos());
            }else{
                openContainer(pState, getBlockPos());
            }
        }
    }
    public void playSound(BlockPos pPos, SoundEvent pSound) {
        if(level.isClientSide()) return;

        double x = pPos.getX() + 0.5D;
        double y = pPos.getY() + (13.0D / 16.0D);
        double z = pPos.getZ() + 0.5D;

        this.level.playSound(null, x, y, z, pSound, SoundSource.BLOCKS, 0.5F, (this.level.random.nextFloat() * 0.1F) + 0.9F);
    }
    private void deleteContents(){
        if(level.isClientSide()) return;
        items.clear();
        this.setChanged();
    }
    public void captureItemsInsideBoundingBox(){
        if(level.isClientSide()) return;

        for(Entity entity : getEntitiesInsideBoundingBox()) {
            if(entity instanceof ItemEntity itemEntity) {
                if (getBlockState().getValue(TrashCanBlock.OPEN)) {
                    ItemStack stack = itemEntity.getItem();
                    stack = this.addItem(stack);

                    if (stack.isEmpty()) {
                        itemEntity.discard();
                    } else {
                        itemEntity.setItem(stack);
                    }
                } else {
                    entity.discard();
                }
            }
        }
    }

    public void makePlayersInsideInvisible(){
        if(getBlockState().getValue(TrashCanBlock.OPEN)) return;

        for(Entity entity : getEntitiesInsideBoundingBox()) {
            if(entity instanceof Player player){
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 2, 0, false, false, false));
            }
        }
    }
    /**Attempts to add given ItemStack to items list.
     * @return leftover {@link ItemStack}*/
    private ItemStack addItem(ItemStack stack){
        int addAmount = stack.getCount();
        int maxSize;
        int targetIndex;
        int currentAmount;

        for(ItemStack targetStack : items){
            maxSize = targetStack.getMaxStackSize();
            currentAmount = targetStack.getCount();
            targetIndex= items.indexOf(targetStack);

            if(stack.isEmpty()) return ItemStack.EMPTY;

            if(currentAmount >= maxSize){
                continue;
            }

            if(targetStack.isEmpty()){
                this.setItem(targetIndex, stack);
                return ItemStack.EMPTY;
            }
            if(targetStack.is(stack.getItem())){
                if(addAmount + currentAmount > maxSize){
                    int leftover = currentAmount + addAmount - maxSize;
                    stack.setCount(leftover);
                    targetStack.setCount(maxSize);
                    this.setItem(targetIndex, targetStack);
                }else if(addAmount + currentAmount == maxSize) {
                    stack.setCount(0);
                    targetStack.setCount(maxSize);
                    this.setItem(targetIndex, targetStack);
                    return ItemStack.EMPTY;
                }else{
                    stack.setCount(0);
                    targetStack.grow(addAmount);
                    this.setItem(targetIndex, targetStack);
                    return ItemStack.EMPTY;
                }
            }
        }
        return stack;
    }

    public List<Entity> getEntitiesInsideBoundingBox(){
        Vec3 position = ModUtils.parseToVec3(getBlockPos());
        AABB boundingBox = new AABB(position.add(3.0D/16.0D, 1.0D/16.0D, 3.0D/16.0D), position.add(13.0D/16.0D, 2.0D/16.0D, 13.0D/16.0D));

        return level.getEntitiesOfClass(Entity.class, boundingBox);
    }

    @Override
    public void tick() {
        captureItemsInsideBoundingBox();
        makePlayersInsideInvisible();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    @NotNull
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }
}
