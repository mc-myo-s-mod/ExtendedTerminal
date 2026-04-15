package me.myogoo.extendedterminal.client.screen.extendedterminal.gui.panels;

import appeng.client.Point;
import appeng.client.gui.Tooltip;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.Scrollbar;
import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedterminal.gui.ETTerminalModePanel;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.RegistryAccess;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class StoneCutterPanel extends ETTerminalModePanel {
    private static final Blitter TEXTURE = Blitter.texture("guis/et_terminal_panel.png", 512, 512);
    private static final Blitter BG = TEXTURE.copy().src(0, 134, 159, 65);
    private static final Blitter BG_SLOT = TEXTURE.copy().src(160, 0, 20, 22);
    private static final Blitter BG_SLOT_SELECTED = TEXTURE.copy().src(160, 23, 20, 22);
    private static final Blitter BG_SLOT_HOVER = TEXTURE.copy().src(160, 45, 20, 22);

    private static final int COLS = 4;
    private static final int ROWS = 2;

    private final Scrollbar scrollbar;

    public StoneCutterPanel(ETTerminalScreen screen, WidgetContainer widgets) {
        super(screen, widgets);
        this.scrollbar = widgets.addScrollBar("stonecuttingPatternModeScrollbar", Scrollbar.SMALL);
        this.scrollbar.setRange(0, 0, COLS);
        this.scrollbar.setCaptureMouseWheel(false);
        setVisible(false);
    }

    @Override
    public void updateBeforeRender() {
        menu.updateStonecutterRecipes();
        var totalRows = (menu.getStoneCutterRecipes().size() + COLS - 1) / COLS;
        this.scrollbar.setRange(0, totalRows - ROWS, ROWS);
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Blocks.STONECUTTER);
    }

    @Override
    public boolean onMouseWheel(Point mousePos, double delta) {
        return this.scrollbar.onMouseWheel(mousePos, delta);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        screen.setSlotsHidden(ETSlotSemantics.STONECUTTING_INPUT, !visible);
        screen.setSlotsHidden(ETSlotSemantics.STONECUTTING_RESULT, !visible);
        this.scrollbar.setVisible(visible);
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        BG.dest(bounds.getX() + 8, bounds.getY() + bounds.getHeight() - 165).blit(guiGraphics);
        drawRecipes(guiGraphics, bounds, mouse);
    }

    @Override
    public boolean onMouseDown(Point mousePos, int button) {
        var recipe = getRecipeAt(mousePos);
        if (recipe != null) {
            menu.setStoneCutterRecipeId(recipe.getId());
            Minecraft.getInstance().getSoundManager()
                    .play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public Tooltip getTooltip(int mouseX, int mouseY) {
        var recipe = getRecipeAt(new Point(mouseX, mouseY));
        if (recipe != null) {
            return new Tooltip(screen.getTerminalTooltip(recipe.getResultItem(getRegistryAccess())));
        }
        return null;
    }

    private RegistryAccess getRegistryAccess() {
        return Objects.requireNonNull(Minecraft.getInstance().level).registryAccess();
    }

    private void drawRecipes(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        var recipes = menu.getStoneCutterRecipes();
        int startIndex = this.scrollbar.getCurrentScroll() * COLS;
        int endIndex = startIndex + ROWS * COLS;
        var selectedRecipe = menu.getStoneCutterRecipeId();

        for (int i = startIndex; i < endIndex && i < recipes.size(); i++) {
            var slotBounds = getRecipeBounds(i - startIndex);
            var recipe = recipes.get(i);
            boolean selected = selectedRecipe != null && selectedRecipe.equals(recipe.getId());

            Blitter blitter = BG_SLOT;
            if (selected) {
                blitter = BG_SLOT_SELECTED;
            } else if (mouse.isIn(slotBounds)) {
                blitter = BG_SLOT_HOVER;
            }

            int renderX = bounds.getX() + slotBounds.getX();
            int renderY = bounds.getY() + slotBounds.getY() + 1;
            blitter.dest(renderX, renderY).blit(guiGraphics);

            var resultItem = recipe.getResultItem(getRegistryAccess());
            if (selected || mouse.isIn(slotBounds)) {
                guiGraphics.renderItem(resultItem, renderX + 2, renderY + 2);
                guiGraphics.renderItemDecorations(Minecraft.getInstance().font, resultItem, renderX + 2, renderY + 2);
            } else {
                guiGraphics.renderItem(resultItem, renderX + 2, renderY + 2);
                guiGraphics.renderItemDecorations(Minecraft.getInstance().font, resultItem, renderX + 2, renderY + 2);
            }
        }
    }

    @Nullable
    private StonecutterRecipe getRecipeAt(Point point) {
        var recipes = menu.getStoneCutterRecipes();
        if (recipes.isEmpty()) {
            return null;
        }

        int startIndex = this.scrollbar.getCurrentScroll() * COLS;
        int endIndex = startIndex + COLS * ROWS;
        for (int i = startIndex; i < endIndex && i < recipes.size(); i++) {
            var slotBounds = getRecipeBounds(i - startIndex);
            if (point.isIn(slotBounds)) {
                return recipes.get(i);
            }
        }
        return null;
    }

    private Rect2i getRecipeBounds(int index) {
        int col = index % COLS;
        int row = index / COLS;
        int slotX = this.x + 26 + col * BG_SLOT.getSrcWidth();
        int slotY = this.y + 12 + row * BG_SLOT.getSrcHeight();
        return new Rect2i(slotX, slotY, BG_SLOT.getSrcWidth(), BG_SLOT.getSrcHeight());
    }
}
