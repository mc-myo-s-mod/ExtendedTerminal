package me.myogoo.extendedterminal.menu.recipe;

import java.util.List;
import java.util.Optional;
import me.myogoo.extendedterminal.integration.itemList.module.ItemListTermCraftingHelper;
import me.myogoo.extendedterminal.util.TableCraftingHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

import static me.myogoo.extendedterminal.network.serverbound.FillTableCraftingGridFromRecipePacket.NOT_SET_RECIPE_SIZE;

/** Plans ExtendedTerminal recipe-transfer ingredient placement before the packet mutates a grid. */
public final class ETRecipeTransferPlanner {
    private ETRecipeTransferPlanner() {
    }

    public static NonNullList<Optional<Ingredient>> desiredIngredients(
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

        var ingredients = NonNullList.<Optional<Ingredient>>withSize(ingredientTemplates.size(), Optional.empty());

        if (recipeWidth == NOT_SET_RECIPE_SIZE || recipeHeight == NOT_SET_RECIPE_SIZE) {
            for (int i = 0; i < ingredients.size(); i++) {
                var template = ingredientTemplates.get(i);
                if (!template.isEmpty()) {
                    ingredients.set(i, Optional.of(Ingredient.of(template.getItem())));
                }
            }
        } else {
            int cursor = 0;
            var coordinator = TableCraftingHelper.indexToCoordinate(ingredientTemplates.size(), recipeWidth, recipeHeight);

            for (int i = 0; i < ingredients.size(); i++) {
                if (coordinator.test(i)) {
                    var template = ingredientTemplates.get(cursor++);
                    if (!template.isEmpty()) {
                        ingredients.set(i, Optional.of(Ingredient.of(template.getItem())));
                    }
                }
            }
        }
        return ingredients;
    }
}
