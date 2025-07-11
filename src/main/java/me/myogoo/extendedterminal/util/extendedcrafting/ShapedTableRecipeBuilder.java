package me.myogoo.extendedterminal.util.extendedcrafting;

import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.CraftingRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ShapedTableRecipeBuilder extends ShapedRecipeBuilder {
    private int tier = 0;
    public ShapedTableRecipeBuilder(ItemLike result, int count) {
        super(RecipeCategory.MISC, result, count);
    }

    public static ShapedTableRecipeBuilder shaped(ItemLike result, int count) {
        return new ShapedTableRecipeBuilder(result, count);
    }

    public void setTier(int tier) {
        if (tier < 0 || tier > 4) {
            throw new IllegalArgumentException("Tier must be between 0 and 4");
        }
        this.tier = tier;
    }

    @Override
    public void save(Consumer<FinishedRecipe> recipeOutput, ResourceLocation id) {
        this.ensureValid(id);
        recipeOutput.accept(new Reuslt(
                id,
                this.result,
                this.count,
                this.group,
                this.rows,
                this.key,
                this.showNotification
        ));

    }

    private static class Reuslt extends CraftingResult {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final String group;
        private final List<String> pattern;
        private final Map<Character, Ingredient> key;
        private final boolean showNotification;

        protected Reuslt(
                ResourceLocation id,
                Item result,
                int count,
                String group,
                List<String> pattern,
                Map<Character,Ingredient> key,
                boolean showNotification
        ) {
            super(CraftingBookCategory.MISC);
            this.id = id;
            this.result = result;
            this.count = count;
            this.group = group;
            this.pattern = pattern;
            this.key = key;
            this.showNotification = showNotification;
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return new ShapedTableRecipe.Serializer();
        }

        @Override
        public @Nullable JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        public @Nullable ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
