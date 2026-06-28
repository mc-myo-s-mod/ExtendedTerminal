package me.myogoo.extendedterminal.client.screen.extendedterminal.gui.panels;

import appeng.client.Point;
import appeng.client.gui.Tooltip;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.widgets.Scrollbar;
import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.api.translation.ETTranslationKey;
import me.myogoo.extendedterminal.client.screen.extendedterminal.gui.ETTerminalModePanel;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.RegistryAccess;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class StoneCutterPanel extends ETTerminalModePanel {
    private static final Blitter BG = Blitter.texture("guis/et_terminal_panel.png",512,512).src(0, 134, 159, 65);
    private static final Blitter AE_BG = Blitter.texture("guis/pattern_modes.png").src(0, 140, 124, 66);
    private static final Blitter BG_SLOT = AE_BG
            .copy()
            .src(124, 140, 20, 22);
    private static final Blitter BG_SLOT_SELECTED = AE_BG
            .copy()
            .src(124, 162, 20, 22);
    private static final Blitter BG_SLOT_HOVER = AE_BG
            .copy()
            .src(124, 184, 20, 22);

    private final Scrollbar scrollbar;

    private static final int COLS = 4;
    private static final int ROWS = 2;

    public StoneCutterPanel(ETTerminalScreen screen, WidgetContainer widgets) {
        super(screen, widgets, ETTranslationKey.GUI.GUI_STONECUTTER_PANEL);
        this.scrollbar = widgets.addScrollBar("stonecuttingPatternModeScrollbar", Scrollbar.SMALL);
        this.scrollbar.setRange(0, 0, COLS);
        this.scrollbar.setCaptureMouseWheel(false);

        setVisible(false);
    }

    @Override
    public void updateBeforeRender() {
        //hmm..
        menu.updateStonecutterRecipes();

        // Set up the scroll bar to have a range only for the rows outside the viewport
        var totalRows = (menu.getStoneCutterRecipes().size() + COLS - 1) / COLS;
        scrollbar.setRange(0, totalRows - ROWS, ROWS);
    }

    @Override
    public ItemStack getIcon() {
        return Blocks.STONECUTTER.asItem().getDefaultInstance();
    }

    @Override
    public boolean onMouseWheel(Point mousePos, double delta) {
        return scrollbar.onMouseWheel(mousePos, delta);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        screen.setSlotsHidden(ETSlotSemantics.STONECUTTING_INPUT, !visible);
        screen.setSlotsHidden(ETSlotSemantics.STONECUTTING_RESULT, !visible);
        scrollbar.setVisible(visible);
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        BG.dest(bounds.getX() + 8, bounds.getY() + bounds.getHeight() - 165).blit(guiGraphics);
        drawRecipes(guiGraphics, bounds, mouse);
    }

    private RegistryAccess getRegistryAccess() {
        return Objects.requireNonNull(Minecraft.getInstance().level).registryAccess();
    }

    private void drawRecipes(GuiGraphics guiGraphics, Rect2i bounds, Point mouse) {
        var recipes = menu.getStoneCutterRecipes();
        var startIndex = scrollbar.getCurrentScroll() * COLS;
        var endIndex = startIndex + ROWS * COLS;

        var selectedRecipe = menu.getStoneCutterRecipeId();

        for (int i = startIndex; i < endIndex && i < recipes.size(); ++i) {
            var slotBounds = getRecipeBounds(i - startIndex);

            var recipe = recipes.get(i);
            boolean selected = selectedRecipe != null && selectedRecipe.equals(recipe.id());

            Blitter blitter = BG_SLOT;
            if (selected) {
                blitter = BG_SLOT_SELECTED;
            } else if (mouse.isIn(slotBounds)) {
                blitter = BG_SLOT_HOVER;
            }

            var renderX = bounds.getX() + slotBounds.getX();
            var renderY = bounds.getY() + slotBounds.getY();
            blitter.dest(renderX, renderY).blit(guiGraphics);
            ItemStack resultItem = recipe.value().getResultItem(getRegistryAccess());
            if (selected || mouse.isIn(slotBounds)) {
                guiGraphics.renderItem(resultItem, renderX + 2, renderY + 3);
                guiGraphics.renderItemDecorations(Minecraft.getInstance().font, resultItem, renderX + 2, renderY + 3);
            } else {
                guiGraphics.renderItem(resultItem, renderX + 2, renderY + 2);
                guiGraphics.renderItemDecorations(Minecraft.getInstance().font, resultItem, renderX + 2, renderY + 2);
            }
        }
    }

    @Override
    public boolean onMouseDown(Point mousePos, int button) {
        var recipe = getRecipeAt(mousePos);
        if (recipe != null) {
            menu.setStoneCutterRecipeId(recipe.id());
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
            var lines = screen.getTooltipFromContainerItem(recipe.value().getResultItem(getRegistryAccess()));
            return new Tooltip(lines);
        }
        return null;
    }

    @Nullable
    private RecipeHolder<StonecutterRecipe> getRecipeAt(Point point) {
        var recipes = menu.getStoneCutterRecipes();

        if (!recipes.isEmpty()) {
            var startIndex = scrollbar.getCurrentScroll() * COLS;
            var endIndex = startIndex + COLS * ROWS;

            for (int i = startIndex; i < endIndex && i < recipes.size(); ++i) {
                var slotBounds = getRecipeBounds(i - startIndex);
                if (point.isIn(slotBounds)) {
                    return recipes.get(i);
                }
            }
        }

        return null;
    }

    private Rect2i getRecipeBounds(int index) {
        var col = index % COLS;
        var row = index / COLS;
        int slotX = x + 26 + col * BG_SLOT.getSrcWidth();
        int slotY = y + 12 + row * BG_SLOT.getSrcHeight();
        return new Rect2i(slotX, slotY, BG_SLOT.getSrcWidth(), BG_SLOT.getSrcHeight());
    }
}
