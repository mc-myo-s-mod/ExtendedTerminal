package me.myogoo.extendedterminal.util.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.ItemLike;

/** @deprecated Use {@link ShapedRecipeBuilder} for new data generation; legacy fluent calls remain supported. */
@Deprecated(forRemoval = true)
public class ETShapedRecipeBuilder extends ShapedRecipeBuilder {
    private ETShapedRecipeBuilder(ItemLike result, int count) {
        super(RecipeCategory.MISC, result, count);
    }

    public static ETShapedRecipeBuilder shaped(ItemLike result, int count) {
        return new ETShapedRecipeBuilder(result, count);
    }

    public ETShapedRecipeBuilder define(Character c, ItemLike item) {
        super.define(c, item);
        return this;
    }

    public ETShapedRecipeBuilder define(Character c, Ingredient ingredient) {
        super.define(c, ingredient);
        return this;
    }

    public ETShapedRecipeBuilder pattern(String row) {
        super.pattern(row);
        return this;
    }

    public CraftingRecipe build(ResourceLocation recipeId) {
        if (this.rows.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + recipeId);
        }
        NonNullList<Ingredient> ingredients = NonNullList.withSize(this.rows.get(0).length() * this.rows.size(), Ingredient.EMPTY);
        int i = 0;

        for (String row : this.rows) {
            for (char symbol : row.toCharArray()) {
                Ingredient ingredient = symbol == ' ' ? Ingredient.EMPTY : this.key.get(symbol);
                if (ingredient == null) {
                    throw new IllegalStateException("Undefined symbol '" + symbol + "' in shaped recipe " + recipeId);
                }
                ingredients.set(i++, ingredient);
            }
        }
        return new ShapedRecipe(recipeId, "", CraftingBookCategory.MISC, this.rows.get(0).length(), this.rows.size(), ingredients, new ItemStack(this.result, this.count));
    }
}
