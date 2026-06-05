package me.myogoo.extendedterminal.api.adapter.recipe;

import net.minecraft.world.item.crafting.Recipe;

public interface IRecipeAdapter {
    <R extends Recipe<?>> R get();
}
