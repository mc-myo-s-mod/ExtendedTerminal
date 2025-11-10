package me.myogoo.extendedterminal.util.recipe.builder;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import committee.nova.mods.avaritia.api.common.crafting.ITierCraftingRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapedTableCraftingRecipe;
import net.byAqua3.avaritia.recipe.RecipeExtremeCrafting;
import net.byAqua3.avaritia.recipe.RecipeExtremeShaped;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;

public class ShapedTableRecipeBuilder extends ShapedRecipeBuilder {
    private int tier = 0;
    public ShapedTableRecipeBuilder(ItemLike result, int count) {
        super(RecipeCategory.MISC, result, count);
    }

    public static ShapedTableRecipeBuilder shaped(ItemLike result, int count) {
        return new ShapedTableRecipeBuilder(result, count);
    }

    public ShapedTableRecipeBuilder tier(int tier) {
        if (tier < 0 || tier > 4) {
            throw new IllegalArgumentException("Tier must be between 0 and 4");
        }
        this.tier = tier;
        return this;
    }

    public ShapedTableRecipeBuilder pattern(String pattern) {
        super.pattern(pattern);
        return this;
    }

    public ShapedTableRecipeBuilder define(char key, ItemLike item) {
        super.define(key, item);
        return this;
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.of(this.key, this.rows);
        ShapedTableRecipe shapedTableRecipe = new ShapedTableRecipe(shapedrecipepattern,getResult().getDefaultInstance(),tier);
        recipeOutput.accept(id, shapedTableRecipe, null);
    }

    public RecipeHolder<ITableRecipe> buildEC(ResourceLocation id) {
        ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.of(this.key, this.rows);
        return new RecipeHolder<>(id, new ShapedTableRecipe(shapedrecipepattern,getResult().getDefaultInstance(),tier));
    }

    public RecipeHolder<ITierCraftingRecipe> buildReAV(ResourceLocation id) {
        ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.of(this.key, this.rows);
        return new RecipeHolder<>(id, new ShapedTableCraftingRecipe(shapedrecipepattern,getResult().getDefaultInstance(),tier));
    }

    public RecipeHolder<RecipeExtremeCrafting> buildAVNeo(ResourceLocation id) {
        ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.of(this.key, this.rows);
        return new RecipeHolder<>(id, new RecipeExtremeShaped("",shapedrecipepattern,getResult().getDefaultInstance()));
    }
}
