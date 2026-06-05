package me.myogoo.extendedterminal.client.screen.extendedcrafting;

import appeng.client.gui.style.ScreenStyle;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import me.myogoo.extendedterminal.network.serverbound.ETCycleUnitedRecipeKindPacket;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class UnitedTerminalScreen extends ETTerminalBaseScreen<ITableRecipe, UnitedTerminalMenu> {
    private CycleRecipeKindButton cycleRecipeKindButton;

    public UnitedTerminalScreen(UnitedTerminalMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    public void init() {
        super.init();
        if (this.getMenu().hasMultipleRecipeKinds()) {
            this.cycleRecipeKindButton = addRenderableWidget(new CycleRecipeKindButton(
                    this.leftPos + 7,
                    this.topPos + this.imageHeight - 25));
        }
    }

    private void cycleRecipeKind() {
        this.getMenu().selectNextRecipeKind();
        if (this.cycleRecipeKindButton != null) {
            this.cycleRecipeKindButton.refreshTooltip();
        }
        MyotusAPI.network().sendToServer(new ETCycleUnitedRecipeKindPacket());
    }

    private Component selectedRecipeKindLabel() {
        return switch (this.getMenu().getSelectedRecipeKind()) {
            case VANILLA_CRAFTING -> Component.literal("Crafting Table");
            case EXTENDED_CRAFTING -> Component.literal("Extended Crafting");
            case AVARITIA_NEO -> Component.literal("Avaritia Neo");
            case RE_AVARITIA -> Component.literal("Re:Avaritia");
        };
    }

    private ItemStack selectedRecipeKindIcon() {
        return switch (this.getMenu().getSelectedRecipeKind()) {
            case VANILLA_CRAFTING -> new ItemStack(Items.CRAFTING_TABLE);
            case EXTENDED_CRAFTING -> icon("extendedcrafting", "ultimate_table");
            case AVARITIA_NEO -> icon("avaritia", "extreme_crafting_table");
            case RE_AVARITIA -> icon("avaritia", "extreme_crafting_table");
        };
    }

    private ItemStack icon(String namespace, String path) {
        Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(namespace, path));
        if (item == Items.AIR) {
            return new ItemStack(Items.CRAFTING_TABLE);
        }
        return new ItemStack(item);
    }

    private class CycleRecipeKindButton extends Button {
        private CycleRecipeKindButton(int x, int y) {
            super(x, y, 18, 18, Component.empty(), button -> cycleRecipeKind(), DEFAULT_NARRATION);
            refreshTooltip();
        }

        private void refreshTooltip() {
            setTooltip(Tooltip.create(Component.translatable(
                    "gui.extendedterminal.united_terminal.recipe_kind",
                    selectedRecipeKindLabel())));
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            super.renderWidget(graphics, mouseX, mouseY, partialTick);
            graphics.renderItem(selectedRecipeKindIcon(), this.getX() + 1, this.getY() + 1);
        }
    }
}
