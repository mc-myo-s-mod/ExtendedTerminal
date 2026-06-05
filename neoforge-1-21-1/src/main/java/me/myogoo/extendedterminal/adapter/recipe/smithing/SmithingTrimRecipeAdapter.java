package me.myogoo.extendedterminal.adapter.recipe.smithing;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;

public class SmithingTrimRecipeAdapter extends SmithingRecipeBaseAdapter {
    private final SmithingTrimRecipe recipe;
    public SmithingTrimRecipeAdapter(SmithingTrimRecipe recipe) {
        super(recipe);
        this.recipe = recipe;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        var list = NonNullList.withSize(3, Ingredient.EMPTY);
        list.set(0, this.recipe.template);
        list.set(1, this.recipe.base);
        list.set(2, this.recipe.addition);
        return list;
    }

    @Override
    public <R extends Recipe<?>> R get() {
        return (R) recipe;
    }
}
