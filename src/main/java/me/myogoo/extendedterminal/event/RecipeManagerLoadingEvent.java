package me.myogoo.extendedterminal.event;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import me.myogoo.extendedterminal.ExtendedTerminal;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.List;

public class RecipeManagerLoadingEvent extends Event {
    private final RecipeManager manager;
    private final List<RecipeHolder<?>> recipes;

    public RecipeManagerLoadingEvent(RecipeManager manager, List<RecipeHolder<?>> recipes) {
        this.manager = manager;
        this.recipes = recipes;
    }

    public RecipeManager getRecipeManager() {
        return this.manager;
    }

    public void addRecipe(RecipeHolder<?> recipe) {
        this.recipes.add(recipe);
    }

    public static void fireRecipeManagerLoadingEvent(RecipeManager manager, ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> map,
                                                     ImmutableMap.Builder<ResourceLocation, RecipeHolder<?>> builder) {
        var recipes = new ArrayList<RecipeHolder<?>>();

        try {
            NeoForge.EVENT_BUS.post(new RecipeManagerLoadingEvent(manager, recipes));
        } catch (Exception e) {
            ExtendedTerminal.LOGGER.error("An error occurred while firing RecipeManagerLoadingEvent", e);
        }

        for (var recipe : recipes) {
            map.put(recipe.value().getType(), recipe);
            builder.put(recipe.id(), recipe);
        }
    }
}
