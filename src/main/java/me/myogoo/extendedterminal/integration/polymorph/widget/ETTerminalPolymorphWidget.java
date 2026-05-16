package me.myogoo.extendedterminal.integration.polymorph.widget;

import appeng.menu.SlotSemantics;
import com.illusivesoulworks.polymorph.api.client.base.ITickingRecipesWidget;
import com.illusivesoulworks.polymorph.client.recipe.widget.PlayerRecipesWidget;
import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;

public class ETTerminalPolymorphWidget extends PlayerRecipesWidget implements ITickingRecipesWidget {
    private final ETTerminalScreen<?> screen;
    private Slot outputSlot;

    public ETTerminalPolymorphWidget(ETTerminalScreen<?> screen, Slot outputSlot) {
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
        var slots = screen.getMenu().getSlots(SlotSemantics.CRAFTING_RESULT);
        if (!slots.isEmpty() && this.outputSlot != slots.get(0)) {
            this.outputSlot = slots.get(0);
            if (this.selectionWidget != null && this.openButton != null) {
                resetWidgetOffsets();
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (screen.getMenu().getMode() != ETTerminalMode.CRAFTING) {
            return;
        }
        resetWidgetOffsets();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return screen.getMenu().getMode() == ETTerminalMode.CRAFTING && super.mouseClicked(mouseX, mouseY, button);
    }
}
