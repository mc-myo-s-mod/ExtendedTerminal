package me.myogoo.extendedterminal.api.adapter.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Optional;

public interface ITableRecipeAdapter<I extends Container> {
    <R extends Recipe<?>> Optional<R> unwrap(Class<R> recipeClass);

    Recipe<?> recipe();

    ResourceLocation recipeId();

    int tier();
}
