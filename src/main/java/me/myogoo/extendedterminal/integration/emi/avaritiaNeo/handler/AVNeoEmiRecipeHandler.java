package me.myogoo.extendedterminal.integration.emi.avaritiaNeo.handler;

import appeng.core.localization.ItemModText;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import me.myogoo.extendedterminal.api.adapter.recipe.IShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.api.adapter.recipe.ITableRecipeAdapter;
import me.myogoo.extendedterminal.integration.emi.handler.AbstractEmiTableRecipeHandler;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.avaritiaNeo.NeoExtremeTerminalMenu;
import net.byAqua3.avaritia.recipe.RecipeExtremeCrafting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.myogoo.extendedterminal.integration.ItemListTermCraftingHelper.ensureFittedCraftingGrid;

public class AVNeoEmiRecipeHandler extends AbstractEmiTableRecipeHandler<NeoExtremeTerminalMenu> {
    private final ETMenuType menuType;
    private final EmiRecipeCategory category;

    public AVNeoEmiRecipeHandler(EmiRecipeCategory category, Class<NeoExtremeTerminalMenu> containerClass, ETMenuType menuType) {
        super(containerClass);
        this.menuType = menuType;
        this.category = category;
    }

    @Override
    protected Result transferRecipe(NeoExtremeTerminalMenu menu, EmiRecipe emiRecipe, boolean doTransfer) {
        var recipe = emiRecipe.getBackingRecipe();
        boolean craftingRecipe = isCraftingRecipe(recipe, emiRecipe);
        if (!craftingRecipe) {
            return Result.createNotApplicable();
        }

        if (!fitsInNxNGrid(recipe, emiRecipe, menuType.getGridSize())) {
            return Result.createFailed(ItemModText.RECIPE_TOO_LARGE.text());
        }

        if(!(recipe instanceof RecipeExtremeCrafting tableRecipe)) {
            return Result.createFailed(ItemModText.INCOMPATIBLE_RECIPE.text());
        }
        var adapterRecipe = ITableRecipeAdapter.of(tableRecipe);

        // Find missing ingredient
        var slotToIngredientMap = getGuiSlotToIngredientMap(menu, adapterRecipe);
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);

        if (missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            // All missing, can't do much...
            return Result.createFailed(ItemModText.NO_ITEMS.text(), missingSlots.missingSlots());
        }

        if (!doTransfer) {
            if (missingSlots.anyMissingOrCraftable()) {
                return new Result.PartiallyCraftable(missingSlots);
            }
        } else {
            boolean craftMissing = AbstractContainerScreen.hasControlDown();
            performTransfer(menu, emiRecipe.getId(), adapterRecipe, craftMissing);
        }

        // No error
        return Result.createSuccessful();
    }

    @Override
    protected boolean isCraftingRecipe(Recipe<?> recipe, EmiRecipe emiRecipe) {
        return emiRecipe.getCategory().equals(this.category);
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(NeoExtremeTerminalMenu menu, ITableRecipeAdapter recipe) {
        int gridSideLength = menu.getCraftingGridWidth();
        var raw = recipe.recipe().getIngredients();
        List<Ingredient> ingredients;

        int width = gridSideLength;
        int height = gridSideLength;
        if (recipe instanceof IShapedTableRecipeAdapter shapedRecipe) {
            ingredients = ensureFittedCraftingGrid(shapedRecipe);
            width = shapedRecipe.width();
            height = shapedRecipe.height();
        } else {
            ingredients = raw;
        }

        int max = gridSideLength * gridSideLength;
        int count = Math.min(ingredients.size(), max);
        var result = new HashMap<Integer, Ingredient>(count);
        for (int i = 0; i < count; i++) {
            int x = i % width;
            int y = i / width;

            var guiSlot = y * width + x;
            var ing = ingredients.get(i);
            if (!ing.isEmpty()) {
                result.put(guiSlot, ing);
            }
        }
        return result;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().equals(category);
    }
}
