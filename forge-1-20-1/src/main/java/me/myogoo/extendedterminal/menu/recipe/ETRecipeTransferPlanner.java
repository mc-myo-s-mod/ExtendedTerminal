package me.myogoo.extendedterminal.menu.recipe;

import java.util.List;
import me.myogoo.extendedterminal.integration.ItemListTermCraftingHelper;
import me.myogoo.extendedterminal.util.extendedcrafting.TableCraftingHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import static me.myogoo.extendedterminal.network.serverbound.ETFillCraftingGridFromRecipePacket.NOT_SET_RECIPE_SIZE;

/** Plans ExtendedTerminal recipe-transfer ingredient placement before the packet mutates a grid. */
public final class ETRecipeTransferPlanner {
    private ETRecipeTransferPlanner() {
    }

    public static NonNullList<Ingredient> desiredIngredients(
            @Nullable Recipe<?> recipe,
            List<ItemStack> ingredientTemplates,
            int recipeWidth,
            int recipeHeight
    ) {
        if (recipe != null) {
            return ItemListTermCraftingHelper.ensureNxNTableCraftingGrid(
                    recipe,
                    ingredientTemplates.size(),
                    recipeWidth,
                    recipeHeight);
        }

        var ingredients = NonNullList.withSize(ingredientTemplates.size(), Ingredient.EMPTY);

        if (recipeWidth == NOT_SET_RECIPE_SIZE || recipeHeight == NOT_SET_RECIPE_SIZE) {
            for (int i = 0; i < ingredients.size(); i++) {
                var template = ingredientTemplates.get(i);
                if (!template.isEmpty()) {
                    ingredients.set(i, Ingredient.of(template));
                }
            }
        } else {
            int cursor = 0;
            var coordinator = TableCraftingHelper.indexToCoordinate(ingredientTemplates.size(), recipeWidth, recipeHeight);

            for (int i = 0; i < ingredients.size(); i++) {
                if (coordinator.test(i)) {
                    ingredients.set(i, Ingredient.of(ingredientTemplates.get(cursor++)));
                }
            }
        }
        return ingredients;
    }
}
