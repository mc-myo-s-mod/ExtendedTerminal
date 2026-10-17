package me.myogoo.extendedterminal.integration.polymorph.widget;

import com.illusivesoulworks.polymorph.api.client.base.ITickingRecipesWidget;
import com.illusivesoulworks.polymorph.client.recipe.widget.PlayerRecipesWidget;
import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;

public class ETTerminalPolymorphWidget extends PlayerRecipesWidget implements ITickingRecipesWidget {
    private final ETTerminalBaseScreen<?, ?> screen;
    private Slot outputSlot;

    public ETTerminalPolymorphWidget(ETTerminalBaseScreen<?, ?> screen, Slot outputSlot) {
        super(screen, outputSlot);
        this.screen = screen;
        this.outputSlot = outputSlot;
    }

    @Override
    public void selectRecipe(ResourceLocation id) {
        super.selectRecipe(id);
        screen.getMenu().getPlayer().level().getRecipeManager().byKey(id)
                .ifPresent(recipe -> screen.getMenu().updatePolymorphRecipeSelection());
    }

    @Override
    public Slot getOutputSlot() {
        return this.outputSlot;
    }

    @Override
    public void tick() {
        var slots = screen.getMenu().getSlots(screen.getMenu().getOutputSlotSemantic());
        if (!slots.isEmpty() && this.outputSlot != slots.get(0)) {
            this.outputSlot = slots.get(0);
            if (this.selectionWidget != null && this.openButton != null) {
                resetWidgetOffsets();
            }
        }
        if (!isCraftingMode() && this.selectionWidget != null) {
            this.selectionWidget.setActive(false);
        }
    }

    @Override
    public int getXPos() {
        return screen.getMenu() instanceof UnitedTerminalMenu ? screen.getXSize() + 4 : super.getXPos();
    }

    @Override
    public int getYPos() {
        return screen.getMenu() instanceof UnitedTerminalMenu ? getOutputSlot().y : super.getYPos();
    }

    private boolean isCraftingMode() {
        return screen.getMenu() instanceof ETTerminalMenu etMenu && etMenu.getMode() == ETTerminalMode.CRAFTING
                || screen.getMenu() instanceof UnitedTerminalMenu unitedMenu
                && unitedMenu.getSelectedRecipeKind() == UnitedTerminalMenu.UnitedRecipeKind.VANILLA;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!isCraftingMode()) {
            return;
        }
        resetWidgetOffsets();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return isCraftingMode() && super.mouseClicked(mouseX, mouseY, button);
    }
}
