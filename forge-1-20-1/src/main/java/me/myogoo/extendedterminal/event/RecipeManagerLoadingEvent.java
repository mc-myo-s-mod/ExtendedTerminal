package me.myogoo.extendedterminal.event;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import me.myogoo.extendedterminal.ExtendedTerminal;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;

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

    public static void fireRecipeManagerLoadingEvent(RecipeManager manager,
                                                     ImmutableMultimap.Builder<RecipeType<?>, Recipe<?>> byType,
                                                     ImmutableMap.Builder<ResourceLocation, Recipe<?>> byName) {
        var recipes = new ArrayList<Recipe<?>>();

        try {
            MinecraftForge.EVENT_BUS.post(new RecipeManagerLoadingEvent(manager, recipes));
        } catch (Exception e) {
            ExtendedTerminal.LOGGER.error("An error occurred while firing RecipeManagerLoadingEvent", e);
        }

        for (var recipe : recipes) {
            byType.put(recipe.getType(), recipe);
            byName.put(recipe.getId(), recipe);
        }
    }
}
