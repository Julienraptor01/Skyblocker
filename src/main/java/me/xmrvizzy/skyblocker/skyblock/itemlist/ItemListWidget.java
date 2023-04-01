package me.xmrvizzy.skyblocker.skyblock.itemlist;

import com.mojang.blaze3d.systems.RenderSystem;
import me.xmrvizzy.skyblocker.mixin.RecipeBookWidgetAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(value= EnvType.CLIENT)
public class ItemListWidget extends RecipeBookWidget implements Drawable, Selectable {
    private int parentWidth;
    private int parentHeight;
    private int leftOffset;
    private TextFieldWidget searchField;
    private SearchResultsWidget results;

    public ItemListWidget() { super(); }

    public void updateSearchResult() {
        this.results.updateSearchResult(((RecipeBookWidgetAccessor)this).getSearchText());
    }

    @Override
    public void initialize(int parentWidth, int parentHeight, MinecraftClient client, boolean narrow, AbstractRecipeScreenHandler<?> craftingScreenHandler) {
        super.initialize(parentWidth, parentHeight, client, narrow, craftingScreenHandler);
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
        this.leftOffset = narrow ? 0 : 86;
        this.searchField = ((RecipeBookWidgetAccessor)this).getSearchField();
        int x = (this.parentWidth - 147) / 2 - this.leftOffset;
        int y = (this.parentHeight - 166) / 2;
        this.results = new SearchResultsWidget(this.client, x , y);
        this.updateSearchResult();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.isOpen()) {
            matrices.push();
            matrices.translate(0.0D, 0.0D, 100.0D);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            this.searchField = ((RecipeBookWidgetAccessor)this).getSearchField();
            int i = (this.parentWidth - 147) / 2 - this.leftOffset;
            int j = (this.parentHeight - 166) / 2;
            this.drawTexture(matrices, i, j, 1, 1, 147, 166);
            this.searchField = ((RecipeBookWidgetAccessor)this).getSearchField();
            if (!this.searchField.isFocused() && this.searchField.getText().isEmpty()) {
                Text hintText = (Text.translatable("gui.recipebook.search_hint")).formatted(Formatting.ITALIC).formatted(Formatting.GRAY);
                drawTextWithShadow(matrices, this.client.textRenderer, hintText, i + 25, j + 14, -1);
            } else {
                this.searchField.render(matrices, mouseX, mouseY, delta);
            }
            this.updateSearchResult();
            this.results.render(matrices, mouseX, mouseY, delta);
            matrices.pop();
        }
    }

    @Override
    public void drawTooltip(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
        if (this.isOpen()) {
            this.results.drawTooltip(matrices, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isOpen() && !this.client.player.isSpectator()) {
            if (this.searchField.mouseClicked(mouseX, mouseY, button)) {
                this.results.closeRecipeView();
                return true;
            }
            return this.results.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }
}
