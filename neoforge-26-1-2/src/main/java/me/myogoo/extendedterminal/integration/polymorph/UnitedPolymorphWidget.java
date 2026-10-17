package me.myogoo.extendedterminal.integration.polymorph;

import com.illusivesoulworks.polymorph.api.client.PolymorphWidgets;
import com.illusivesoulworks.polymorph.api.client.widgets.PlayerRecipesWidget;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.UnitedTerminalScreen;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class UnitedPolymorphWidget extends PlayerRecipesWidget {
    private final UnitedTerminalScreen<?> screen;

    public static void init() {
        PolymorphWidgets.getInstance().registerWidget(screen -> screen instanceof UnitedTerminalScreen<?> united
                ? new UnitedPolymorphWidget(united) : null);
    }

    private UnitedPolymorphWidget(UnitedTerminalScreen<?> screen) {
        super(screen, screen.getMenu().getSlots(screen.getMenu().getOutputSlotSemantic()).getFirst());
        this.screen = screen;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (screen.getMenu().getSelectedRecipeType() != MyoRecipeType.VANILLA) {
            selectionWidget.setActive(false);
            return;
        }
        resetWidgetOffsets();
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return screen.getMenu().getSelectedRecipeType() == MyoRecipeType.VANILLA
                && super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        return screen.getMenu().getSelectedRecipeType() == MyoRecipeType.VANILLA
                && super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    @Override
    public int getXPos() {
        return screen.getImageWidth() + 4;
    }

    @Override
    public int getYPos() {
        return getOutputSlot().y;
    }
}
