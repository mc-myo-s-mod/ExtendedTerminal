package me.myogoo.extendedterminal.data;

import appeng.core.definitions.AEItems;
import appeng.recipes.handlers.ChargerRecipeBuilder;
import appeng.recipes.handlers.InscriberProcessType;
import appeng.recipes.handlers.InscriberRecipeBuilder;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.myotus.init.MyoItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class ETAE2RecipeProvider extends RecipeProvider {
    public ETAE2RecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> output) {
        ChargerRecipeBuilder.charge(output, ExtendedTerminal.makeId("extendedterminal/charger/charged_ender_pearl"),
                Items.ENDER_PEARL, MyoItems.CHARGED_ENDER_PEARL.get());
        ChargerRecipeBuilder.charge(output, ExtendedTerminal.makeId("extendedterminal/charger/compat_press"),
                AEItems.ENGINEERING_PROCESSOR_PRESS.asItem(), MyoItems.COMPAT_PRESS.get());

        InscriberRecipeBuilder.inscribe(MyoItems.CHARGED_ENDER_PEARL.get(), MyoItems.PRINTED_COMPAT_PROCESSOR.get(), 1)
                .setTop(Ingredient.of(MyoItems.COMPAT_PRESS.get()))
                .setMode(InscriberProcessType.INSCRIBE)
                .save(output, ExtendedTerminal.makeId("extendedterminal/inscriber/printed_compat_processor"));
        InscriberRecipeBuilder.inscribe(Items.REDSTONE, MyoItems.COMPAT_PROCESSOR.get(), 1)
                .setTop(Ingredient.of(MyoItems.PRINTED_COMPAT_PROCESSOR.get()))
                .setBottom(Ingredient.of(AEItems.SILICON_PRINT.asItem()))
                .setMode(InscriberProcessType.PRESS)
                .save(output, ExtendedTerminal.makeId("extendedterminal/inscriber/compat_processor"));
    }

    @Override
    public @NotNull String getName() {
        return "Extended Terminal AE2 recipes";
    }
}
