package me.myogoo.extendedterminal.adapter.recipe.smithing;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;

import java.util.List;
import java.util.Optional;

public class SmithingTrimRecipeAdapter extends SmithingRecipeBaseAdapter {
    private final SmithingTrimRecipe recipe;
    public SmithingTrimRecipeAdapter(SmithingTrimRecipe recipe) {
        super(recipe);
        this.recipe = recipe;
    }

    @Override
    public List<Optional<Ingredient>> getIngredients() {
        return List.of(
                this.recipe.templateIngredient(),
                Optional.of(this.recipe.baseIngredient()),
                this.recipe.additionIngredient());
    }

    @Override
    public <R extends Recipe<?>> R get() {
        return (R) recipe;
    }
}
