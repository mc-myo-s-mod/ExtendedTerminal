package me.myogoo.extendedterminal.integration.emi.extendedterminal.handler.panel;

import appeng.core.localization.ItemModText;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;
import me.myogoo.extendedterminal.api.adapter.recipe.ITableRecipeAdapter;
import me.myogoo.extendedterminal.integration.emi.extendedterminal.handler.ETTerminalBaseRecipeHandler;
import me.myogoo.extendedterminal.integration.module.extendedterminal.ETCraftingRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.Map;

public class ETCraftingRecipeHandler<T extends ETTerminalMenu> extends ETTerminalBaseRecipeHandler<T> {
    public ETCraftingRecipeHandler(Class<T> containerClass) {
        super(containerClass);
    }

    @Override
    protected Result transferRecipe(T menu, EmiRecipe emiRecipe, boolean doTransfer) {
        var recipeId = emiRecipe.getId();
        var recipe = emiRecipe.getBackingRecipe();
        boolean craftingRecipe = isCraftingRecipe(recipe, emiRecipe);
        if (!craftingRecipe) {
            return Result.createNotApplicable();
        }
        if (recipe != null && !recipe.canCraftInDimensions(3, 3)) {
            return Result.createFailed(ItemModText.RECIPE_TOO_LARGE.text());
        }
        if (recipe == null) {
            recipe = createFakeRecipe(emiRecipe);
        }
        if (!(recipe instanceof CraftingRecipe cRecipe)) {
            return Result.createFailed(ItemModText.INCOMPATIBLE_RECIPE.text());
        }

        var slotToIngredientMap = getGuiSlotToIngredientMap(menu, ITableRecipeAdapter.of(cRecipe));
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);

        if (missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            return Result.createFailed(ItemModText.NO_ITEMS.text(), missingSlots.missingSlots());
        }

        if (!doTransfer) {
            if (missingSlots.anyMissingOrCraftable()) {
                return new Result.PartiallyCraftable(missingSlots);
            }
        } else {
            boolean craftingMissing = AbstractContainerScreen.hasControlDown();
            ETCraftingRecipeTransferHelper.performTransfer(menu, cRecipe, craftingMissing);
        }
        return Result.createSuccessful();
    }

    @Override
    protected boolean isCraftingRecipe(Recipe<?> recipe, EmiRecipe emiRecipe) {
        return emiRecipe.getCategory().equals(VanillaEmiRecipeCategories.CRAFTING);
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().equals(VanillaEmiRecipeCategories.CRAFTING);
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, ITableRecipeAdapter recipe) {
        return ETCraftingRecipeTransferHelper.getGuiSlotToIngredientMap(menu, recipe.recipe());
    }

    private Recipe<?> createFakeRecipe(EmiRecipe recipe) {
        var ingredients = NonNullList.withSize(3 * 3,
                Ingredient.EMPTY);

        for (int i = 0; i < Math.min(recipe.getInputs().size(), ingredients.size()); i++) {
            var ingredient = Ingredient.of(recipe.getInputs().get(i).getEmiStacks().stream()
                    .map(EmiStack::getItemStack)
                    .filter(is -> !is.isEmpty()));
            ingredients.set(i, ingredient);
        }

        return new ShapedRecipe(new ResourceLocation(""), "", CraftingBookCategory.MISC, 3,
                3, ingredients, ItemStack.EMPTY);
    }
}
