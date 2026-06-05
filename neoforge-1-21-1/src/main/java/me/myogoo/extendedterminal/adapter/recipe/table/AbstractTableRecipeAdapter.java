package me.myogoo.extendedterminal.adapter.recipe.table;

import appeng.api.stacks.AEItemKey;
import appeng.integration.modules.itemlists.EncodingHelper;
import me.myogoo.extendedterminal.api.adapter.recipe.table.ITableRecipeAdapter;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.Map;

import static me.myogoo.extendedterminal.integration.itemList.module.ItemListTermCraftingHelper.ENTRY_COMPARATOR;

abstract class AbstractTableRecipeAdapter implements ITableRecipeAdapter {

    @Override
    public NonNullList<ItemStack> findGoodTemplateItems(ETTerminalBaseMenu<?> menu) {
        var ingredientPriorities = EncodingHelper.getIngredientPriorities(menu, ENTRY_COMPARATOR);

        var templateItems = NonNullList.withSize(menu.getCraftingGridSize(), ItemStack.EMPTY);
        var ingredients = this.ensureFittedCraftingGrid();
        for (int i = 0; i < ingredients.size(); i++) {
            var ingredient = ingredients.get(i);
            if (!ingredient.isEmpty()) {
                var stack = ingredientPriorities.entrySet()
                        .stream()
                        .filter(e -> e.getKey() instanceof AEItemKey itemKey && itemKey.matches(ingredient))
                        .max(Comparator.comparingInt(Map.Entry::getValue))
                        .map(e -> ((AEItemKey) e.getKey()).toStack())
                        .orElse(ingredient.getItems()[0]);
                templateItems.set(i, stack);
            }
        }
        return templateItems;
    }


    @Override
    public int gridSize() {
        return this.sideLength() * this.sideLength();
    }

    @Override
    public int sideLength() {
        return this.tier() * 2 + 1;
    }
}
