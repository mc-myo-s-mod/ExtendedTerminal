package me.myogoo.extendedterminal.api.adapter.recipe.table;

import appeng.util.CraftingRecipeUtil;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapelessTableRecipe;
import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapedTableCraftingRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapelessTableCraftingRecipe;
import me.myogoo.extendedterminal.adapter.recipe.table.ShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.adapter.recipe.table.ShapelessTableRecipeAdapter;
import me.myogoo.extendedterminal.api.adapter.recipe.MyoBaseRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import me.myogoo.extendedterminal.api.annotation.ExtendedCrafting;
import me.myogoo.extendedterminal.api.annotation.ReAvaritia;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public interface MyoTableRecipe extends MyoBaseRecipe {
    int tier();

    int gridSize();

    int sideLength();

    List<Optional<Ingredient>> ensureFittedCraftingGrid();

    NonNullList<ItemStack> findGoodTemplateItems(ETTerminalBaseMenu<?> menu);

    ResourceKey<Recipe<?>> id();

    boolean matches(MyoTableInput input, Level level, MyoRecipeType recipeType);

    ItemStack assemble(MyoTableInput input, Level level, MyoRecipeType recipeType);

    NonNullList<ItemStack> getRemainingItems(MyoTableInput input, MyoRecipeType recipeType);

    RecipeHolder<Recipe<?>> castHolderRecipe();

    static List<Optional<Ingredient>> positionedIngredients(Recipe<?> recipe) {
        if (MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            if (recipe instanceof ShapedTableRecipe shaped) {
                return shaped.getPositionedIngredients();
            } else if (recipe instanceof ShapelessTableRecipe shapeless) {
                return shapeless.getIngredients().stream().map(Optional::of).toList();
            } else if (recipe instanceof ITableRecipe tableRecipe) {
                var positioned = tableRecipe.getPositionedIngredients();
                if (!positioned.isEmpty()) {
                    return positioned;
                }
                return tableRecipe.getIngredients().stream().map(Optional::of).toList();
            }
        }
        return CraftingRecipeUtil.getIngredients(recipe);
    }

    static MyoTableRecipe of(CraftingRecipe recipe, ResourceKey<Recipe<?>> id) {
        if (recipe instanceof ShapedRecipe shaped) {
            return new ShapedTableRecipeAdapter(shaped, id);
        } else if (recipe instanceof ShapelessRecipe shapeless) {
            return new ShapelessTableRecipeAdapter(shapeless, id);
        } else {
            if (hasCraftingLayout(recipe) && recipe.display().getFirst() instanceof ShapedCraftingRecipeDisplay shaped) {
                return new ShapedTableRecipeAdapter(recipe, 1, shaped.width(), shaped.height(), id);
            }
            return new ShapelessTableRecipeAdapter(recipe, id);
        }
    }

    static boolean hasCraftingLayout(CraftingRecipe recipe) {
        if (recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe) {
            return true;
        }
        var displays = recipe.display();
        if (displays.size() != 1) {
            return false;
        }
        int slots = recipe.placementInfo().slotsToIngredientIndex().size();
        return switch (displays.getFirst()) {
            case ShapedCraftingRecipeDisplay shaped -> shaped.width() > 0 && shaped.width() <= 9
                    && shaped.height() > 0 && shaped.height() <= 9
                    && shaped.width() * shaped.height() == slots;
            case ShapelessCraftingRecipeDisplay shapeless -> shapeless.ingredients().size() == slots;
            default -> false;
        };
    }

    @ExtendedCrafting
    static MyoTableRecipe of(ITableRecipe recipe, ResourceKey<Recipe<?>> id) {
        if (recipe instanceof ShapedTableRecipe shaped) {
            return new ShapedTableRecipeAdapter(shaped, id);
        } else if (recipe instanceof ShapelessTableRecipe shapeless) {
            return new ShapelessTableRecipeAdapter(shapeless, id);
        }
        throw new IllegalArgumentException("Unknown ITableRecipe implementation: " + recipe.getClass().getName());
    }

    @ReAvaritia
    static MyoTableRecipe of(ITierCraftingRecipe recipe, ResourceKey<Recipe<?>> id) {
        if (recipe instanceof ShapedTableCraftingRecipe shaped) {
            return new ShapedTableRecipeAdapter(shaped, id);
        } else if (recipe instanceof ShapelessTableCraftingRecipe shapeless) {
            return new ShapelessTableRecipeAdapter(shapeless, id);
        }
        throw new IllegalArgumentException("Unknown ITierCraftingRecipe implementation: " + recipe.getClass().getName());
    }

}
