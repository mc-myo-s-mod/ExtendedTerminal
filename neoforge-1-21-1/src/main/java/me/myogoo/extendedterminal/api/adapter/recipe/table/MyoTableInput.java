package me.myogoo.extendedterminal.api.adapter.recipe.table;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MyoTableInput implements RecipeInput {
    private final int width;
    private final int height;
    private final List<ItemStack> items;
    private final int tier;

    private final CraftingInput.Positioned positioned;

    private MyoTableInput(int width, int height, List<ItemStack> items, int tier) {
        positioned = CraftingInput.ofPositioned(width,height,items);
        this.width = width;
        this.height = height;
        this.items = items;
        this.tier = tier;
    }

    public CraftingInput cast() {
        return CraftingInput.of(width(), height(), items());
    }

    public static MyoTableInput create(int width, int height, List<ItemStack> items, int tier) {
        return new MyoTableInput(width, height, items, tier);
    }

    public static MyoTableInput create(int width, int height, List<ItemStack> items) {
        return create(width, height, items, 0);
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public List<ItemStack> items() {
        return this.items;
    }

    public int tier() {
        return this.tier;
    }

    public int top() {
        return this.positioned.top();
    }

    public int left() {
        return this.positioned.left();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof MyoTableInput that)) return false;

        if (width != that.width) return false;
        if (height != that.height) return false;
        if (tier != that.tier) return false;
        if (items.size() != that.items.size()) return false;
        return ItemStack.listMatches(items, that.items);
    }

    @Override
    public @NotNull ItemStack getItem(int i) {
        return (ItemStack) this.items.get(i);
    }

    @Override
    public int size() {
        return items.size();
    }
}
