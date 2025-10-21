package me.myogoo.extendedterminal.integration.emi.extendedcrafting.handler;

import appeng.core.localization.ItemModText;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import me.myogoo.extendedterminal.api.adapter.recipe.table.IShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.api.adapter.recipe.table.ITableRecipeAdapter;
import me.myogoo.extendedterminal.integration.emi.extendedcrafting.recipe.ECTableRecipe;
import me.myogoo.extendedterminal.integration.emi.handler.AbstractTableRecipeHandler;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.myogoo.extendedterminal.integration.ItemListTermCraftingHelper.ensureFittedCraftingGrid;

public class ECTerminalRecipeHandler<T extends ExtendedTerminalBaseMenu> extends AbstractTableRecipeHandler<T> {
    private final ETMenuType menuType;
    private final EmiRecipeCategory category;
    public ECTerminalRecipeHandler(EmiRecipeCategory category, Class<T> containerClass, ETMenuType menuType) {
        super(containerClass);
        this.menuType = menuType;
        this.category = category;
    }
    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().equals(this.category);
    }

    @Override
    protected boolean isCraftingRecipe(Recipe<?> recipe, EmiRecipe emiRecipe) {
        if(recipe instanceof ITableRecipe tableRecipe) {
            return emiRecipe.getCategory().equals(ECTableRecipe.getCategory(tableRecipe.getTier()));
        }
        return false;
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, ITableRecipeAdapter recipe) {
        int gridSideLength = menu.getCraftingGridWidth();
        var raw = recipe.recipe().getIngredients();
        List<Ingredient> ingredients;

        int offsetX = 0;
        int offsetY = 0;
        int width = gridSideLength;
        int height = gridSideLength;
        if (recipe instanceof IShapedTableRecipeAdapter shapedRecipe) {
            ingredients = ensureFittedCraftingGrid(shapedRecipe);
            width = shapedRecipe.width();
            height = shapedRecipe.height();
            offsetX = Math.floorDiv(gridSideLength - shapedRecipe.width(),2);
            offsetY = Math.floorDiv(gridSideLength - shapedRecipe.height(),2);
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
    @Override
    protected Result transferRecipe(T menu, @Nullable RecipeHolder<?> holder, EmiRecipe emiRecipe, boolean doTransfer) {
        var recipeId = holder != null ? holder.id() : null;
        var recipe = holder != null ? holder.value() : null;
        boolean craftingRecipe = isCraftingRecipe(recipe, emiRecipe);
        if (!craftingRecipe) {
            return Result.createNotApplicable();
        }

        if (!fitsInNxNGrid(recipe, emiRecipe, menuType.getGridSize())) {
            return Result.createFailed(ItemModText.RECIPE_TOO_LARGE.text());
        }

        if (recipe == null) {
            recipe = createFakeRecipe(emiRecipe);
        }


        if(!(recipe instanceof ITableRecipe tableRecipe)) {
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
                // Highlight the slots with missing ingredients
                return new Result.PartiallyCraftable(missingSlots);
            }
        } else {
            // Thank you RS for pioneering this amazing feature! :)
            boolean craftMissing = AbstractContainerScreen.hasControlDown();
            performTransfer(menu, holder.id(),adapterRecipe, craftMissing);
        }

        // No error
        return Result.createSuccessful();
    }

    private Recipe<?> createFakeRecipe(EmiRecipe display) {
        var ingredients = NonNullList.withSize(menuType.getGridSize(), Ingredient.EMPTY);

        for (int i = 0; i < Math.min(display.getInputs().size(), ingredients.size()); i++) {
            var ingredient = Ingredient.of(display.getInputs().get(i).getEmiStacks().stream()
                    .map(EmiStack::getItemStack)
                    .filter(is -> !is.isEmpty()));
            ingredients.set(i, ingredient);
        }

        var pattern = new ShapedRecipePattern(menuType.getGridSize(), menuType.getGridSize(), ingredients, Optional.empty());
        return new ShapedTableRecipe(pattern, ItemStack.EMPTY, menuType.getTier());
    }
}
