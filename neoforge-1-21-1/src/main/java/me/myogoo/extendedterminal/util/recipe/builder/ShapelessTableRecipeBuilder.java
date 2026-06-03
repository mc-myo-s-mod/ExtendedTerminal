package me.myogoo.extendedterminal.util.recipe.builder;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapelessTableRecipe;
import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapelessTableCraftingRecipe;
import net.byAqua3.avaritia.recipe.RecipeExtremeCrafting;
import net.byAqua3.avaritia.recipe.RecipeExtremeShapeless;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

public class ShapelessTableRecipeBuilder extends ShapelessRecipeBuilder {
    private final List<Ingredient> tableIngredients = new ArrayList<>();
    private int tier = 0;

    public ShapelessTableRecipeBuilder(ItemLike result, int count) {
        super(RecipeCategory.MISC, result, count);
    }

    public static ShapelessTableRecipeBuilder shapeless(ItemLike result, int count) {
        return new ShapelessTableRecipeBuilder(result, count);
    }

    public ShapelessTableRecipeBuilder tier(int tier) {
        if (tier < 0 || tier > 4) {
            throw new IllegalArgumentException("Tier must be between 0 and 4");
        }
        this.tier = tier;
        return this;
    }

    public ShapelessTableRecipeBuilder requires(ItemLike item) {
        super.requires(item);
        this.tableIngredients.add(Ingredient.of(item));
        return this;
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        var recipe = new ShapelessTableRecipe(ingredients(), getResult().getDefaultInstance(), tier);
        recipeOutput.accept(id, recipe, null);
    }

    public RecipeHolder<ITableRecipe> buildEC(ResourceLocation id) {
        return new RecipeHolder<>(id, new ShapelessTableRecipe(ingredients(), getResult().getDefaultInstance(), tier));
    }

    public RecipeHolder<ITierCraftingRecipe> buildReAV(ResourceLocation id) {
        return new RecipeHolder<>(id, new ShapelessTableCraftingRecipe(ingredients(), getResult().getDefaultInstance(), tier));
    }

    public RecipeHolder<RecipeExtremeCrafting> buildAVNeo(ResourceLocation id) {
        return new RecipeHolder<>(id, new RecipeExtremeShapeless("", getResult().getDefaultInstance(), ingredients(), false));
    }

    private NonNullList<Ingredient> ingredients() {
        return NonNullList.copyOf(this.tableIngredients);
    }
}
