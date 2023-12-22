package net.neversnows.trashcanmod.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.neversnows.trashcanmod.block.TrashCanBlock;
import net.neversnows.trashcanmod.block.entity.TrashCanBlockEntity;

import java.util.List;

public class TrashCanBlockEntityRenderer implements BlockEntityRenderer<TrashCanBlockEntity> {
    public TrashCanBlockEntityRenderer(BlockEntityRendererProvider.Context pContext){

    }
    @Override
    public void render(TrashCanBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        Direction facing = pBlockEntity.getBlockState().getValue(TrashCanBlock.FACING);

        List<ItemStack> items = pBlockEntity.getItems();

        float height = 4.0F;
        for (int i = 0; i < pBlockEntity.getContainerSize(); i++) {

            pPoseStack.pushPose();
            int mod = i % 6;

            //This is what we call "top-notch programming"
            if(mod == 0 % 6){
                pPoseStack.translate(5.0F/16.0F, height/16.0F, 9.0F/16.0F);
            }else if (mod == 1 % 6){
                pPoseStack.translate(11.0F/16.0F, height/16.0F, 9.0F/16.0F);
            }else if(mod == 2 % 6){
                pPoseStack.translate(0.5F, height/16.0F, 5.0F/16.0F);
                height += 2.0F;
            }else if(mod == 3 % 6){
                pPoseStack.translate(0.5F, height/16.0F, 11.0F/16.0F);
            }else if(mod == 4 % 6){
                pPoseStack.translate(5.0F/16.0F, height/16.0F, 7.0F/16.0F);
            }else{
                pPoseStack.translate(11.0F/16.0F, height/16.0F, 7.0F/16.0F);
                height += 2.0F;
            }
            // Fun
            //pPoseStack.translate(pBlockEntity.getLevel().random.nextFloat() * 0.05F,0,pBlockEntity.getLevel().random.nextFloat() * 0.05F);
            pPoseStack.scale(0.35F, 0.35F, 0.35F);

            //A switch-case would not work, since it considers the ENUM index,
            // instead of its value, because of reasons yet unknown to mankind.
            if(facing == Direction.WEST){
                pPoseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            }else if(facing == Direction.SOUTH){
                pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            }else if(facing == Direction.EAST){
                pPoseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
            }
            pPoseStack.mulPose(Axis.XP.rotationDegrees(45));
            itemRenderer.renderStatic(items.get(i) ,ItemDisplayContext.FIXED,getLight(pBlockEntity.getLevel(), pBlockEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, pPoseStack, pBuffer, pBlockEntity.getLevel(), 1);

            pPoseStack.popPose();
        }
    }

    private int getLight(Level pLevel, BlockPos pPos){
        int blockLight = pLevel.getBrightness(LightLayer.BLOCK, pPos);
        int skyLight = pLevel.getBrightness(LightLayer.SKY, pPos);
        return LightTexture.pack(blockLight, skyLight);
    }
}
