package me.myogoo.extendedterminal.datagen.provider;

import appeng.core.definitions.AEItems;
import appeng.datagen.providers.recipes.ChargerRecipes;
import appeng.recipes.handlers.ChargerRecipeBuilder;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.init.ETItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ChargerRecipeProvider extends ChargerRecipes {
    public ChargerRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        ChargerRecipeBuilder
                .charge(consumer,
                        ExtendedTerminal.makeId("charger/charged_ender_pearl"),
                        Items.ENDER_PEARL, ETItems.CHARGED_ENDER_PEARL);

        ChargerRecipeBuilder
                .charge(consumer,
                        ExtendedTerminal.makeId("charger/compat_press"),
                        AEItems.ENGINEERING_PROCESSOR_PRESS, ETItems.COMPAT_PRESS);
    }
}
