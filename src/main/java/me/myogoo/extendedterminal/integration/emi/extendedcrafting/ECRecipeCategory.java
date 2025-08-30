package me.myogoo.extendedterminal.integration.emi.extendedcrafting;

import com.blakebr0.extendedcrafting.init.ModBlocks;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiCategory;

@ModAccessor.ExtendedCrafting
@ETEmiCategory
public class ECRecipeCategory {
    public static final EmiRecipeCategory BASIC_TABLE_CRAFTING_CATEGORY = new EmiRecipeCategory(
            ExtendedTerminal.makeId("basic_crafting_table"),
            EmiStack.of(ModBlocks.BASIC_TABLE.get().asItem().getDefaultInstance()));

    public static final EmiRecipeCategory ADVANCED_TABLE_CRAFTING_CATEGORY = new EmiRecipeCategory(
            ExtendedTerminal.makeId("advanced_crafting_table"),
            EmiStack.of(ModBlocks.ADVANCED_TABLE.get().asItem().getDefaultInstance()));

    public static final EmiRecipeCategory ELITE_TABLE_CRAFTING_CATEGORY = new EmiRecipeCategory(
            ExtendedTerminal.makeId("elite_crafting_table"),
            EmiStack.of(ModBlocks.ELITE_TABLE.get().asItem().getDefaultInstance()));

    public static final EmiRecipeCategory ULTIMATE_TABLE_CRAFTING_CATEGORY = new EmiRecipeCategory(
            ExtendedTerminal.makeId("ultimate_crafting_table"),
            EmiStack.of(ModBlocks.ULTIMATE_TABLE.get().asItem().getDefaultInstance()));

    @SubscribeLoadEvent
    public static void register(EmiRegistry registry) {
        registry.addCategory(BASIC_TABLE_CRAFTING_CATEGORY);
        registry.addCategory(ADVANCED_TABLE_CRAFTING_CATEGORY);
        registry.addCategory(ELITE_TABLE_CRAFTING_CATEGORY);
        registry.addCategory(ULTIMATE_TABLE_CRAFTING_CATEGORY);
    }
}
