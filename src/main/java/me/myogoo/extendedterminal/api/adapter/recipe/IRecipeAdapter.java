package me.myogoo.extendedterminal.api.adapter.recipe;

import net.minecraft.world.item.crafting.Recipe;

import java.util.Optional;

public interface IRecipeAdapter {
    <R extends Recipe<?>> R recipe();
}
