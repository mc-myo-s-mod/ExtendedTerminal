package me.myogoo.extendedterminal.adapter.recipe.smithing;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

import java.util.List;
import java.util.Optional;

public class SmithingTransformRecipeAdapter extends SmithingRecipeBaseAdapter {
    private final SmithingTransformRecipe recipe;
    public SmithingTransformRecipeAdapter(SmithingTransformRecipe recipe) {
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
        return null;
    }
}
