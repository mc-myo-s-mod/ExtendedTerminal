package me.myogoo.extendedterminal.api.adapter.recipe;

import net.minecraft.world.Container;

public interface IShapedTableRecipeAdapter<I extends Container> extends ITableRecipeAdapter<I> {
    int width();

    int height();
}
