package com.myogoo.extendedterminal.util.extendedcrafting;

import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;

public class ShapedTableRecipeBuilder extends ShapedRecipeBuilder {
    private int tier = 0;
    public ShapedTableRecipeBuilder(ItemLike result, int count) {
        super(RecipeCategory.MISC, result, count);
    }

    public static ShapedTableRecipeBuilder shaped(ItemLike result, int count) {
        return new ShapedTableRecipeBuilder(result, count);
    }

    public void setTier(int tier) {
        if (tier < 0 || tier > 4) {
            throw new IllegalArgumentException("Tier must be between 0 and 4");
        }
        this.tier = tier;
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.of(this.key, this.rows);
        ShapedTableRecipe shapedTableRecipe = new ShapedTableRecipe(shapedrecipepattern,getResult().getDefaultInstance(),tier);
        recipeOutput.accept(id, shapedTableRecipe, null);

    }
}
