package me.myogoo.extendedterminal.adapter.recipe.smithing;

import me.myogoo.extendedterminal.api.adapter.recipe.smithing.ISmithingRecipeAdapter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingRecipe;

abstract class SmithingRecipeBaseAdapter implements ISmithingRecipeAdapter {
    private final SmithingRecipe recipe;

    public SmithingRecipeBaseAdapter(SmithingRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack item) {
        return recipe.isTemplateIngredient(item);
    }

    @Override
    public boolean isBaseIngredient(ItemStack item) {
        return recipe.isBaseIngredient(item);
    }

    @Override
    public boolean isAdditionIngredient(ItemStack item) {
        return recipe.isAdditionIngredient(item);
    }

    @Override
    public boolean isInComplete() {
        return recipe.isIncomplete();
    }
}
