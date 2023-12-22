package net.neversnows.trashcanmod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neversnows.trashcanmod.block.entity.ModBlockEntities;
import net.neversnows.trashcanmod.block.entity.TrashCanBlockEntity;
import net.neversnows.trashcanmod.block.entity.util.ITickableBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class TrashCanBlock extends Block implements EntityBlock{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final VoxelShape OPEN_SHAPE = Shapes.join(
            Block.box(2, 0, 2, 14, 12, 14),
            Block.box(3, 1, 3, 13, 12, 13),
            BooleanOp.NOT_SAME
    );
    public static final VoxelShape COLLISION_CLOSED_SHAPE = Stream.of(
            OPEN_SHAPE,
            Block.box(1, 12, 1, 15, 14, 15)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static final VoxelShape X_CLOSED_SHAPE = Stream.of(
            COLLISION_CLOSED_SHAPE,
            Block.box(7.5, 14, 5, 8.5, 15, 7),
            Block.box(7.5, 15, 6, 8.5, 16, 10),
            Block.box(7.5, 14, 9, 8.5, 15, 11)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static final VoxelShape Z_CLOSED_SHAPE = Stream.of(
            COLLISION_CLOSED_SHAPE,
            Block.box(5, 14, 7.5, 7, 15, 8.5),
            Block.box(6, 15, 7.5, 10, 16, 8.5),
            Block.box(9, 14, 7.5, 11, 15, 8.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    public static final VoxelShape INTERACTION_SHAPE = Block.box(2, 0, 2, 14, 12, 14);
    public TrashCanBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, Boolean.valueOf(false)));
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntities.TRASH_CAN_BLOCK_ENTITY.get().create(pPos, pState);
    }
    @Override
    @NotNull
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(pLevel.isClientSide()) return InteractionResult.SUCCESS;

        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if(!(blockEntity instanceof TrashCanBlockEntity castBlockEntity) || pHand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if(pPlayer instanceof ServerPlayer castPlayer){
            if(castPlayer.isShiftKeyDown()){
                castBlockEntity.swapOpenState(castBlockEntity.getBlockState());
            }else{
                castPlayer.openMenu(castBlockEntity, pPos);
            }
        }

        return InteractionResult.CONSUME;
    }
    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if(!pState.is(pNewState.getBlock())){
            if(!pLevel.isClientSide()){
                BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
                if(blockEntity instanceof TrashCanBlockEntity castBlockEntity){
                    Containers.dropContents(pLevel, pPos, castBlockEntity);
                }
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        }
    }
    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof TrashCanBlockEntity trashCanBlockEntity) {
            trashCanBlockEntity.recheckOpen();
        }
    }
    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        if (pStack.hasCustomHoverName()) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof TrashCanBlockEntity trashCanBlockEntity) {
                trashCanBlockEntity.setCustomName(pStack.getHoverName());
            }
        }
    }
    @Override
    @SuppressWarnings("deprecation")
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }
    @Override
    @SuppressWarnings("deprecation")
    public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(pLevel.getBlockEntity(pPos));
    }
    @Override
    @NotNull
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }
    @Override
    @NotNull
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, OPEN);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }
    @Override
    @NotNull
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof TrashCanBlockEntity) {
            if(pState.getValue(OPEN)){
                return OPEN_SHAPE;
            }
            switch (pState.getValue(FACING)) {
                case NORTH, SOUTH:
                    return Z_CLOSED_SHAPE;
                case EAST, WEST:
                    return X_CLOSED_SHAPE;
            }
        }
        return Shapes.block();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return pState.getValue(OPEN);
    }

    @Override
    @NotNull
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if(pState.getValue(OPEN)){
            return OPEN_SHAPE;
        }
        return COLLISION_CLOSED_SHAPE;
    }

    @Override
    @NotNull
    @SuppressWarnings("deprecation")
    public VoxelShape getInteractionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return INTERACTION_SHAPE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return ITickableBlockEntity.getTickerHelper(pLevel);
    }
}
