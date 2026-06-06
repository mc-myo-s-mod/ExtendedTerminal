package me.myogoo.extendedterminal.client.screen.extendedcrafting;

import appeng.client.gui.Icon;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.IconButton;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import me.myogoo.extendedterminal.client.screen.ETTerminalBaseScreen;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

public class UnitedTerminalScreen extends ETTerminalBaseScreen<ITableRecipe, UnitedTerminalMenu> {
    private CycleRecipeKindButton cycleRecipeKindButton;

    public UnitedTerminalScreen(UnitedTerminalMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.cycleRecipeKindButton = new CycleRecipeKindButton();
        widgets.add("cycleRecipeKind", this.cycleRecipeKindButton);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.cycleRecipeKindButton.setVisibility(this.getMenu().hasMultipleRecipeKinds());
    }

    private void cycleRecipeKind() {
        this.getMenu().selectNextRecipeKind();
    }

    private Component selectedRecipeKindTooltip() {
        return Component.translatable(
                "gui.extendedterminal.united_terminal.recipe_kind",
                selectedRecipeKindLabel());
    }

    private Component selectedRecipeKindLabel() {
        return switch (this.getMenu().getSelectedRecipeKind()) {
            case VANILLA_CRAFTING -> Component.literal("Crafting Table");
            case EXTENDED_CRAFTING -> Component.literal("Extended Crafting");
            case AVARITIA_NEO -> Component.literal("Avaritia Neo");
            case RE_AVARITIA -> Component.literal("Re:Avaritia");
        };
    }

    private Item selectedRecipeKindItem() {
        return switch (this.getMenu().getSelectedRecipeKind()) {
            case VANILLA_CRAFTING -> Items.CRAFTING_TABLE;
            case EXTENDED_CRAFTING -> icon("extendedcrafting", "ultimate_table");
            case AVARITIA_NEO -> icon("avaritia", "extreme_crafting_table");
            case RE_AVARITIA -> icon("avaritia", "extreme_crafting_table");
        };
    }

    private Item icon(String namespace, String path) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(namespace, path));
        return item == Items.AIR ? Items.CRAFTING_TABLE : item;
    }

    private class CycleRecipeKindButton extends IconButton {
        private CycleRecipeKindButton() {
            super(button -> cycleRecipeKind());
        }

        @Override
        protected Icon getIcon() {
            return Icon.ARROW_RIGHT;
        }

        @Override
        protected Item getItemOverlay() {
            return selectedRecipeKindItem();
        }

        @Override
        public List<Component> getTooltipMessage() {
            return List.of(selectedRecipeKindTooltip());
        }
    }
}
