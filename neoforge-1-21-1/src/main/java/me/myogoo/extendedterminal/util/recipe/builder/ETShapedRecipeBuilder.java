package me.myogoo.extendedterminal.util.recipe.builder;

import appeng.core.definitions.AEParts;
import me.myogoo.extendedterminal.init.ETItems;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

public class ETShapedRecipeBuilder extends ShapedRecipeBuilder {
    private ETShapedRecipeBuilder(ItemLike result, int count) {
        super(RecipeCategory.MISC, result, count);
    }

    public static ETShapedRecipeBuilder shaped(ItemLike result, int count) {
        return new ETShapedRecipeBuilder(result, count);
    }

    public static ETShapedRecipeBuilder terminal(ItemLike result, Ingredient input) {
        return new ETShapedRecipeBuilder(result, 1)
                .pattern("IT")
                .pattern("P ")
                .define('I', input)
                .define('T', AEParts.CRAFTING_TERMINAL)
                .define('P', ETItems.COMPAT_PROCESSOR)
                .showNotification(false);
    }

    public static ETShapedRecipeBuilder terminal(ItemLike result, ItemLike input) {
        return new ETShapedRecipeBuilder(result, 1)
                .pattern("IT")
                .pattern("P ")
                .define('I', input)
                .define('T', AEParts.CRAFTING_TERMINAL)
                .define('P', ETItems.COMPAT_PROCESSOR)
                .showNotification(false);
    }

    public ETShapedRecipeBuilder pattern(String pattern) {
        super.pattern(pattern);
        return this;
    }

    public ETShapedRecipeBuilder define(char key, Ingredient ingredient) {
        super.define(key, ingredient);
        return this;
    }

    public ETShapedRecipeBuilder define(char key, ItemLike item) {
        super.define(key, item);
        return this;
    }

    public ETShapedRecipeBuilder showNotification(boolean show) {
        super.showNotification(show);
        return this;
    }

    public ETShapedRecipeBuilder condition(Class<?> ...modClasses) {

        return this;
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.of(this.key, this.rows);
        ShapedRecipe shapedrecipe = new ShapedRecipe((String)Objects.requireNonNullElse(this.group, ""), RecipeBuilder.determineBookCategory(this.category), shapedrecipepattern, this.resultStack, this.showNotification);
        recipeOutput.accept(id, shapedrecipe,null);
    }

    public CraftingRecipe build() {
        ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.of(this.key, this.rows);
        return new ShapedRecipe("", CraftingBookCategory.MISC, shapedrecipepattern, getResult().getDefaultInstance(), false);
    }
}
