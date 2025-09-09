package me.myogoo.extendedterminal.api;

import net.minecraft.world.item.crafting.Recipe;

public interface RecipeHolder<R extends Recipe<?>> {
    R get();

    static <R extends Recipe<?>> RecipeHolder<R> of(R recipe) {
        return () -> recipe;
    }
}
