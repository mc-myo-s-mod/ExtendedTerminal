package me.myogoo.extendedterminal.event;

import com.blakebr0.cucumber.Cucumber;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import me.myogoo.extendedterminal.ExtendedTerminal;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.myogoo.extendedterminal.ExtendedTerminal.LOGGER;

public class RecipeManagerLoadingEvent extends Event {
    private final RecipeManager manager;
    private final List<Recipe<?>> recipes;

    public RecipeManagerLoadingEvent(RecipeManager manager, List<Recipe<?>> recipes) {
        this.manager = manager;
        this.recipes = recipes;
    }

    public RecipeManager getRecipeManager() {
        return this.manager;
    }

    public void addRecipe(Recipe<?> recipe) {
        this.recipes.add(recipe);
    }

    public static void fireRecipeManagerLoadingEvent(RecipeManager manager, Map<RecipeType<?>, Object> map, ImmutableMap.Builder<ResourceLocation, Recipe<?>> builder) {
        var recipes = new ArrayList<Recipe<?>>();

        try {
            MinecraftForge.EVENT_BUS.post(new RecipeManagerLoadingEvent(manager, recipes));
        } catch (Exception e) {
            LOGGER.error("An error occurred while firing RecipeManagerLoadingEvent", e);
        }

        for(Recipe<?> recipe : recipes) {
            RecipeType<?> recipeType = recipe.getType();
            ResourceLocation recipeId = recipe.getId();
            Object recipeMap = map.get(recipeType);
            if (recipeMap instanceof Object2ObjectLinkedOpenHashMap<?,? >) {
                @SuppressWarnings("unchecked")
                var o2oRecipeMap = (Object2ObjectLinkedOpenHashMap<ResourceLocation, Recipe<?>>) recipeMap;
                o2oRecipeMap.put(recipeId, recipe);
            } else if (recipeMap instanceof ImmutableMap.Builder<?, ?> ) {
                @SuppressWarnings("unchecked")
                var recipeMapBuilder = (ImmutableMap.Builder<ResourceLocation, Recipe<?>>) recipeMap;
                recipeMapBuilder.put(recipeId, recipe);
            } else if (recipeMap == null) {
                ImmutableMap.Builder<Object, Object> recipeMapBuilder = ImmutableMap.builder();
                recipeMapBuilder.put(recipeId, recipe);
                map.put(recipeType, recipeMapBuilder);
            } else {
                LOGGER.error("Failed to register recipe {} to map of type {}", recipeId, recipeMap.getClass());
            }
            builder.put(recipeId, recipe);
        }
        LOGGER.info("Registered {} recipes", recipes.size());
    }
}
