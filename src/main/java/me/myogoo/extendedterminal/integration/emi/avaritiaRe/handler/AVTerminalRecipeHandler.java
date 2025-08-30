package me.myogoo.extendedterminal.integration.emi.avaritiaRe.handler;

import appeng.core.localization.ItemModText;
import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapedTableCraftingRecipe;
import committee.nova.mods.avaritia.init.compat.emi.category.tables.*;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import me.myogoo.extendedterminal.api.adapter.recipe.IShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.api.adapter.recipe.ITableRecipeAdapter;
import me.myogoo.extendedterminal.integration.emi.extendedcrafting.recipe.ECTableRecipe;
import me.myogoo.extendedterminal.integration.emi.handler.AbstractTableRecipeHandler;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.avaritiaRe.AvaritiaTerminalBaseMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.myogoo.extendedterminal.integration.ItemListTermCraftingHelper.ensureFittedCraftingGrid;

public class AVTerminalRecipeHandler<T extends AvaritiaTerminalBaseMenu> extends AbstractTableRecipeHandler<T> {
    private final ETMenuType menuType;
    private final EmiRecipeCategory category;
    public AVTerminalRecipeHandler(EmiRecipeCategory category, Class<T> containerClass, ETMenuType menuType) {
        super(containerClass);
        this.menuType = menuType;
        this.category = category;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().equals(this.category);
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


        if(!(recipe instanceof ITierCraftingRecipe tableRecipe)) {
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
            performTransfer(menu, adapterRecipe, craftMissing);
        }

        // No error
        return Result.createSuccessful();    }

    @Override
    protected boolean isCraftingRecipe(Recipe<?> recipe, EmiRecipe emiRecipe) {
        if(recipe instanceof ITierCraftingRecipe tableRecipe) {
            return emiRecipe.getCategory().equals(getCategory(tableRecipe.getTier()));
        }
        return false;
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, ITableRecipeAdapter recipe) {
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


    private Recipe<?> createFakeRecipe(EmiRecipe display) {
        var ingredients = NonNullList.withSize(menuType.getGridSize(), Ingredient.EMPTY);

        for (int i = 0; i < Math.min(display.getInputs().size(), ingredients.size()); i++) {
            var ingredient = Ingredient.of(display.getInputs().get(i).getEmiStacks().stream()
                    .map(EmiStack::getItemStack)
                    .filter(is -> !is.isEmpty()));
            ingredients.set(i, ingredient);
        }

        var pattern = new ShapedRecipePattern(menuType.getGridSize(), menuType.getGridSize(), ingredients, Optional.empty());
        return new ShapedTableCraftingRecipe(pattern, ItemStack.EMPTY, menuType.getTier());
    }

    private EmiRecipeCategory getCategory(int tier) {
        return switch (tier) {
            case 1 -> SculkCraftingTableCategory.CATEGORY;
            case 2 -> NetherCraftingTableCategory.CATEGORY;
            case 3 -> EndCraftingTableCategory.CATEGORY;
            case 4 -> ExtremeCraftingTableCategory.CATEGORY;
            default -> throw new IllegalArgumentException("Invalid tier: " + tier);
        };
    }
}
