package me.myogoo.extendedterminal.integration;

import appeng.api.stacks.AEItemKey;
import appeng.core.AELog;
import appeng.core.sync.network.NetworkHandler;
import appeng.integration.modules.jeirei.EncodingHelper;
import appeng.menu.me.common.GridInventoryEntry;
import appeng.util.CraftingRecipeUtil;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapelessTableRecipe;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.menu.extendedcrafting.ExtendedTerminalBaseMenu;
import me.myogoo.extendedterminal.network.serverbound.ETFillCraftingGridFromRecipePacket;
import me.myogoo.extendedterminal.util.extendedcrafting.ExtendedCraftingHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemListTermCraftingHelper {
    private static final Comparator<GridInventoryEntry> ENTRY_COMPARATOR = Comparator
            .comparing(GridInventoryEntry::getStoredAmount);

    private ItemListTermCraftingHelper() {
    }

    public static void performTransfer(ExtendedTerminalBaseMenu menu, @Nullable ResourceLocation recipeId, Recipe<?> recipe,
                                       boolean craftMissing) {

        // We send the items in the recipe in any case to serve as a fallback in case the recipe is transient
        var templateItems = findGoodTemplateItems(recipe, menu);
        // Don't transmit a recipe id to the server in case the recipe is not actually resolvable
        // this is the case for recipes synthetically generated for JEI
        if (recipeId != null && menu.getPlayer().level().getRecipeManager().byKey(recipeId).isEmpty()) {
            AELog.debug("Cannot send recipe id %s to server because it's transient", recipeId);
            recipeId = null;
        }

        //disabled shapeless crafting for now, because it doesn't work with the new crafting grid
        //var message = new ETFillCraftingGridFromRecipePacket(recipeId, templateItems, craftMissing);
        //NetworkHandler.instance().sendToServer(message);
    }

    public static void performTransfer(ExtendedTerminalBaseMenu menu, Recipe<?> recipe, boolean craftMissing,
                                       int recipeWidth, int recipeHeight) {
        var templateItems = findGoodTemplateItems(recipe, menu);

        var message =new ETFillCraftingGridFromRecipePacket(recipe.getId(),templateItems,craftMissing,
                recipeWidth,recipeHeight);
        NetworkHandler.instance().sendToServer(message);
    }

    private static NonNullList<ItemStack> findGoodTemplateItems(Recipe<?> recipe, ExtendedTerminalBaseMenu menu) {
        var ingredientPriorities = EncodingHelper.getIngredientPriorities(menu, ENTRY_COMPARATOR);

        var templateItems = NonNullList.withSize(menu.getCraftingGridSize(), ItemStack.EMPTY);
        var ingredients = ensureNxNCraftingMatrix(recipe);
        for (int i = 0; i < ingredients.size(); i++) {
            var ingredient = ingredients.get(i);
            if (!ingredient.isEmpty()) {
                // Try to find the best item. In case the ingredient is a tag, it might contain versions the
                // player doesn't actually have
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

    public static Map<Integer, Ingredient> getGuiSlotToIngredientMap(ITableRecipe recipe, int gridSideLength) {
        var raw = recipe.getIngredients();
        List<Ingredient> ingredients;

        int offsetX = 0;
        int offsetY = 0;
        int width = gridSideLength;
        int height = gridSideLength;
        if (recipe instanceof ShapedTableRecipe tableRecipe) {
            ingredients = fittedCraftingMatrix(tableRecipe);
            width = tableRecipe.getWidth();
            height = tableRecipe.getHeight();
            offsetX = Math.floorDiv(gridSideLength - tableRecipe.getWidth(),2);
            offsetY = Math.floorDiv(gridSideLength - tableRecipe.getHeight(),2);
        } else {
            ingredients = raw;
        }

        int max = gridSideLength * gridSideLength;
        int count = Math.min(ingredients.size(), max);
        var result = new HashMap<Integer, Ingredient>(count);
        for (int i = 0; i < count; i++) {
            int x = i % width;
            int y = i / width;

            var guiSlot = (y + offsetY) * gridSideLength + (x + offsetX);
            var ing = ingredients.get(i);
            if (!ing.isEmpty()) {
                result.put(guiSlot, ing);
            }
        }
         return result;
    }

    public static NonNullList<Ingredient> fittedCraftingMatrix(Recipe<?> recipe) {
        var ingredients = recipe.getIngredients();
        NonNullList<Ingredient> expandedIngredients;
        if(recipe instanceof ShapedTableRecipe shapedTableRecipe) {
            int width = shapedTableRecipe.getWidth();
            int height = shapedTableRecipe.getHeight();
            int matrixSize = width * height;

            expandedIngredients = NonNullList.withSize(matrixSize, Ingredient.EMPTY);

            for(int h = 0; h < height; h++) {
                for(int w = 0; w < width; w++) {
                    int index = w + h * width;
                    if(index < ingredients.size()) {
                        expandedIngredients.set(index, ingredients.get(index));
                    } else {
                        expandedIngredients.set(index, Ingredient.EMPTY);
                    }
                }
            }
            return  expandedIngredients;
        } else if (recipe instanceof ShapelessTableRecipe shapelessTableRecipe) {
            expandedIngredients = ExtendedCraftingHelper.makeNxNIngredients(shapelessTableRecipe);
            for(int i = 0; i < ingredients.size(); i++) {
                expandedIngredients.set(i, ingredients.get(i));
            }
        }
        else {
            return CraftingRecipeUtil.ensure3by3CraftingMatrix(recipe);
        }

        return expandedIngredients = NonNullList.withSize(9, Ingredient.EMPTY);
    }

    public static NonNullList<Ingredient> ensureNxNCraftingMatrix(Recipe<?> recipe) {
        var ingredients = recipe.getIngredients();
        NonNullList<Ingredient> expandedIngredients;
        if(recipe instanceof ITableRecipe tableRecipe) {
            int size = ExtendedCraftingHelper.getCraftingGridSize(tableRecipe);
            expandedIngredients = ExtendedCraftingHelper.makeNxNIngredients(tableRecipe);

            if(tableRecipe instanceof ShapedTableRecipe shapedTableRecipe) {
                var tier = shapedTableRecipe.getTier();
                var width = shapedTableRecipe.getWidth();
                var height = shapedTableRecipe.getHeight();
                int matrixWidth = ExtendedCraftingHelper.getCraftingGridWidth(shapedTableRecipe);
                // Map shaped recipe into center of NxN matrix
                for(int h = 0; h < height; h++) {
                    for(int w = 0; w < width; w++) {
                        var source = w + h * width;
                        var target = w + h * matrixWidth;
                        var ing = ingredients.get(source);
                        expandedIngredients.set(target, ing);
                    }
                }
            } else if (tableRecipe instanceof ShapelessTableRecipe) {
                for(int i = 0; i < ingredients.size(); i++) {
                    expandedIngredients.set(i, ingredients.get(i));
                }
            }
        } else {
            return CraftingRecipeUtil.ensure3by3CraftingMatrix(recipe);
        }
        return expandedIngredients;
    }

}
