package me.myogoo.extendedterminal.util.extendedcrafting;

import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static net.minecraft.core.registries.BuiltInRegistries.*;

public class ShapedTableRecipeBuilder extends CraftingRecipeBuilder implements RecipeBuilder {
    private final RecipeCategory category;
    private final Item result;
    private final int count;
    private final List<String> rows = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final Advancement.Builder advancement = Advancement.Builder.recipeAdvancement();
    private String group;
    private int tier = 0;
    public ShapedTableRecipeBuilder(ItemLike result, int count) {
        this.category = RecipeCategory.MISC;
        this.result = result.asItem();
        this.count = count;
    }

    public static ShapedTableRecipeBuilder shaped(ItemLike result, int count) {
        return new ShapedTableRecipeBuilder(result, count);
    }

    public ShapedTableRecipeBuilder define(Character c, ItemLike item) {
        return this.define(c, Ingredient.of(item));
    }

    public ShapedTableRecipeBuilder define(Character c, Ingredient ingredient) {
        if (c == ' ') {
            throw new IllegalArgumentException("Symbol ' ' is reserved and cannot be defined");
        } else if (this.key.containsKey(c)) {
            throw new IllegalArgumentException("Symbol '" + c + "' is already defined!");
        } else {
            this.key.put(c, ingredient);
            return this;
        }
    }

    public ShapedTableRecipeBuilder pattern(String row) {
        if(!row.isEmpty() && !this.rows.isEmpty() && row.length() != this.rows.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be rectangular! Row length does not match previous rows.");
        } else {
            this.rows.add(row);
            return this;
        }
    }

    public void setTier(int tier) {
        if (tier < 0 || tier > 4) {
            throw new IllegalArgumentException("Tier must be between 0 and 4");
        }
        this.tier = tier;
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
    public RecipeBuilder unlockedBy(String a, CriterionTriggerInstance b) {
        this.advancement.addCriterion(a,b);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result;
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

        public JsonObject serializeRecipe() {
            JsonObject json = new JsonObject();
            json.addProperty("type","extendedcrafting:shaped_table");
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
