package me.myogoo.extendedterminal.adapter.recipe;

import com.blakebr0.extendedcrafting.crafting.recipe.ShapelessTableRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.EternalSingularityCraftRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.InfinityCatalystCraftRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapelessTableCraftingRecipe;
import me.myogoo.extendedterminal.api.adapter.recipe.ITableRecipeAdapter;
import me.myogoo.extendedterminal.api.adapter.recipe.IShapelessTableRecipeAdapter;
import net.byAqua3.avaritia.recipe.RecipeExtremeShapeless;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.Optional;

public class ShapelessTableRecipeAdapter implements IShapelessTableRecipeAdapter<Container> {
    private final Recipe<?> recipe;
    private final int recipeTier;

    public ShapelessTableRecipeAdapter(Recipe<?> recipe, int tier) {
        this.recipe = recipe;
        this.recipeTier = tier;
    }
    public ShapelessTableRecipeAdapter(ShapelessRecipe recipe) {
        this(recipe, 1);
    }
    public ShapelessTableRecipeAdapter(CraftingRecipe recipe) {
        this(recipe, 1);
    }

    //Extended Crafting
    public ShapelessTableRecipeAdapter(ShapelessTableRecipe recipe) { this(recipe, recipe.getTier()); }

    //ReAvaritia
    public ShapelessTableRecipeAdapter(ShapelessTableCraftingRecipe recipe) { this(recipe, recipe.getTier()); }
    public ShapelessTableRecipeAdapter(EternalSingularityCraftRecipe recipe) { this(recipe, recipe.getTier()); }
    public ShapelessTableRecipeAdapter(InfinityCatalystCraftRecipe recipe) { this(recipe, recipe.getTier()); }

    //AvaritiaNeo
    public ShapelessTableRecipeAdapter(RecipeExtremeShapeless recipe) { this(recipe, 4); }

    @Override
    public <R extends Recipe<?>> Optional<R> unwrap(Class<R> recipeClass) {
        if(recipeClass.isInstance(this.recipe)) {
            return Optional.of(recipeClass.cast(this.recipe));
        }
        return Optional.empty();
    }

    @Override
    public Recipe<?> recipe() {
        return this.recipe;
    }

    @Override
    public ResourceLocation recipeId() {
        return recipe.getId();
    }

    @Override
    public int tier() {
        return this.recipeTier;
    }
}
