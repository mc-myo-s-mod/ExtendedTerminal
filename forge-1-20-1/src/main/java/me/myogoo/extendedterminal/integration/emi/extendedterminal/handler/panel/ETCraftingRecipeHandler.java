package me.myogoo.extendedterminal.integration.emi.extendedterminal.handler.panel;

import me.myogoo.extendedterminal.adapter.recipe.TableRecipeAdapters;

import appeng.core.localization.ItemModText;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiStack;
import me.myogoo.extendedterminal.integration.emi.handler.AbstractEmiTableRecipeHandler;
import me.myogoo.extendedterminal.api.adapter.recipe.ITableRecipeAdapter;
import me.myogoo.extendedterminal.integration.module.extendedterminal.ETCraftingRecipeTransferHelper;
import me.myogoo.extendedterminal.menu.ETSlotSemantics;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.List;
import java.util.Map;

public class ETCraftingRecipeHandler<T extends ETTerminalBaseMenu<?>> extends AbstractEmiTableRecipeHandler<T> {
    public ETCraftingRecipeHandler(Class<T> containerClass) {
        super(containerClass);
    }

    @Override
    public List<Slot> getInputSources(T menu) {
        var slots = super.getInputSources(menu);
        if (menu instanceof ETTerminalMenu) {
            slots.addAll(menu.getSlots(ETSlotSemantics.STONECUTTING_INPUT));
            slots.addAll(menu.getSlots(ETSlotSemantics.SMITHING_TABLE_BASE));
            slots.addAll(menu.getSlots(ETSlotSemantics.SMITHING_TABLE_TEMPLATE));
            slots.addAll(menu.getSlots(ETSlotSemantics.SMITHING_TABLE_ADDITION));
        }
        return slots;
    }

    @Override
    protected Result transferRecipe(T menu, EmiRecipe emiRecipe, boolean doTransfer) {
        var recipeId = emiRecipe.getId();
        var recipe = resolveBackingRecipe(emiRecipe);
        boolean craftingRecipe = isCraftingRecipe(recipe, emiRecipe);
        if (!craftingRecipe) {
            return Result.createNotApplicable();
        }
        if (recipe != null && !recipe.canCraftInDimensions(3, 3)) {
            return Result.createFailed(ItemModText.RECIPE_TOO_LARGE.text());
        }
        if (recipe == null) {
            recipe = createFakeRecipe(emiRecipe, menu instanceof UnitedTerminalMenu);
        }
        if (!(recipe instanceof CraftingRecipe cRecipe)) {
            return Result.createFailed(ItemModText.INCOMPATIBLE_RECIPE.text());
        }

        var adapterRecipe = TableRecipeAdapters.of(cRecipe);
        var slotToIngredientMap = getGuiSlotToIngredientMap(menu, adapterRecipe);
        var inputSlotKeys = slotToIngredientMap.keySet();
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);

        if (missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            return Result.createFailed(ItemModText.NO_ITEMS.text(), missingSlots.missingSlots(), inputSlotKeys);
        }

        if (!doTransfer) {
            if (missingSlots.anyMissingOrCraftable()) {
                return new Result.PartiallyCraftable(missingSlots, inputSlotKeys);
            }
        } else {
            boolean craftingMissing = AbstractContainerScreen.hasControlDown();
            if (menu instanceof UnitedTerminalMenu) {
                performTransfer(menu, recipeId, adapterRecipe, craftingMissing,
                        UnitedTerminalMenu.UnitedRecipeKind.VANILLA);
            } else if (menu instanceof ETTerminalMenu etMenu) {
                ETCraftingRecipeTransferHelper.performTransfer(etMenu, cRecipe, craftingMissing);
            }
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
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, ITableRecipeAdapter<?> recipe) {
        if (menu instanceof UnitedTerminalMenu unitedMenu) {
            return ETCraftingRecipeTransferHelper.getGuiSlotToIngredientMap(unitedMenu, recipe);
        }
        return ETCraftingRecipeTransferHelper.getGuiSlotToIngredientMap((ETTerminalMenu) menu,
                recipe.unwrap(CraftingRecipe.class).orElseThrow());
    }

    private Recipe<?> createFakeRecipe(EmiRecipe recipe, boolean preserveShape) {
        var ingredients = NonNullList.withSize(3 * 3,
                Ingredient.EMPTY);

        for (int i = 0; i < Math.min(recipe.getInputs().size(), ingredients.size()); i++) {
            var ingredient = Ingredient.of(recipe.getInputs().get(i).getEmiStacks().stream()
                    .map(EmiStack::getItemStack)
                    .filter(is -> !is.isEmpty()));
            ingredients.set(i, ingredient);
        }

        var recipeId = new ResourceLocation("");
        if (preserveShape && recipe instanceof EmiCraftingRecipe craftingRecipe) {
            if (craftingRecipe.shapeless) {
                var shapelessIngredients = NonNullList.<Ingredient>create();
                ingredients.stream()
                        .filter(ingredient -> !ingredient.isEmpty())
                        .forEach(shapelessIngredients::add);
                return new ShapelessRecipe(recipeId, "", CraftingBookCategory.MISC, ItemStack.EMPTY,
                        shapelessIngredients);
            }

            int minX = 3;
            int minY = 3;
            int maxX = -1;
            int maxY = -1;
            for (int i = 0; i < ingredients.size(); i++) {
                if (!ingredients.get(i).isEmpty()) {
                    int x = i % 3;
                    int y = i / 3;
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }

            if (maxX >= minX && maxY >= minY) {
                int width = maxX - minX + 1;
                int height = maxY - minY + 1;
                var shapedIngredients = NonNullList.withSize(width * height, Ingredient.EMPTY);
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        shapedIngredients.set(y * width + x, ingredients.get((y + minY) * 3 + x + minX));
                    }
                }
                return new ShapedRecipe(recipeId, "", CraftingBookCategory.MISC, width, height,
                        shapedIngredients, ItemStack.EMPTY);
            }
        }

        return new ShapedRecipe(recipeId, "", CraftingBookCategory.MISC, 3,
                3, ingredients, ItemStack.EMPTY);
    }
}
