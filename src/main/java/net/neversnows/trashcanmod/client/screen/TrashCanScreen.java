package net.neversnows.trashcanmod.client.screen;


import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import net.neversnows.trashcanmod.TrashCanMod;
import net.neversnows.trashcanmod.menu.TrashCanMenu;

public class TrashCanScreen extends AbstractContainerScreen<TrashCanMenu>{
    private static final ResourceLocation TEXTURE = new ResourceLocation(TrashCanMod.MOD_ID, "textures/gui/trash_can_gui.png");
    public TrashCanScreen(TrashCanMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 176;
        this.imageHeight = 239;
        this.inventoryLabelY = 146;
    }
    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void renderTransparentBackground(GuiGraphics pGuiGraphics) {
        super.renderTransparentBackground(pGuiGraphics);
    }

    @Override//pPartialTick = Time.DeltaTime();
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics,pMouseX, pMouseY);
    }
}
