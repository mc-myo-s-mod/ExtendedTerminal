package me.myogoo.extendedterminal.util.extendedcrafting;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import committee.nova.mods.avaritia.common.crafting.recipe.ITierCraftingRecipe;
import committee.nova.mods.avaritia.common.crafting.recipe.ShapedTableCraftingRecipe;
import me.myogoo.extendedterminal.api.RecipeHolder;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static net.minecraft.core.registries.BuiltInRegistries.*;

public class ShapedTableRecipeBuilder extends ShapedRecipeBuilder {

    private int tier = 0;
    private ShapedTableRecipeBuilder(ItemLike result, int count) {
        super(RecipeCategory.MISC, result, count);
    }

    public static ShapedTableRecipeBuilder shaped(ItemLike result, int count) {
        return new ShapedTableRecipeBuilder(result, count);
    }

    public ShapedTableRecipeBuilder define(Character c, ItemLike item) {
        super.define(c, item);
        return this;
    }

    public ShapedTableRecipeBuilder define(Character c, Ingredient ingredient) {
        super.define(c, ingredient);
        return this;
    }

    public ShapedTableRecipeBuilder pattern(String row) {
        super.pattern(row);
        return this;
    }

    public ShapedTableRecipeBuilder setTier(int tier) {
        if (tier < 0 || tier > 4) {
            throw new IllegalArgumentException("Tier must be between 0 and 4");
        }
        this.tier = tier;
        return this;
    }


    private void ensureValid(ResourceLocation recipeId) {
        if(this.rows.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + recipeId + "!");
        }
        Set<Character> set = Sets.newHashSet(this.key.keySet());
        set.remove(' ');

        for(String s : this.rows) {
            for(int i = 0; i < s.length(); ++i) {
                char c0 = s.charAt(i);
                if (!this.key.containsKey(c0) && c0 != ' ') {
                    throw new IllegalStateException("Pattern in recipe " + recipeId + " uses undefined symbol '" + c0 + "'");
                }

                set.remove(c0);
            }
        }

        if (!set.isEmpty()) {
            throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + recipeId);
        } else if (this.rows.size() == 1 && this.rows.get(0).length() == 1) {
            throw new IllegalStateException("Shaped recipe " + recipeId + " only takes in a single item - should it be a shapeless recipe instead?");
        }
    }

    @Override
    public void save(Consumer<FinishedRecipe> recipeOutput, ResourceLocation id) {
        this.ensureValid(id);
        recipeOutput.accept(new Result(id));
    }

    class Result implements FinishedRecipe {
        private final ResourceLocation id;
        public Result(ResourceLocation id) {
            this.id = id;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {}

        public @NotNull JsonObject serializeRecipe() {
            JsonObject json = new JsonObject();
            json.addProperty("type","extendedcrafting:shaped_table");
            if(tier != 0) {
                json.addProperty("tier", tier);
            }
            JsonArray jsonArray = new JsonArray();
            for(String s : rows) {
                jsonArray.add(s);
            }

            json.add("pattern",jsonArray);
            JsonObject jsonObject = new JsonObject();

            for(Map.Entry<Character,Ingredient> entry : key.entrySet()) {
                jsonObject.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
            }

            json.add("key",jsonObject);
            JsonObject resultObject = new JsonObject();
            resultObject.addProperty("item", ITEM.getKey(result).toString());
            if (count > 1) {
                resultObject.addProperty("count", count);
            }

            json.add("result", resultObject);

            return json;
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return new ShapedTableRecipe.Serializer(); // not work :(
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
