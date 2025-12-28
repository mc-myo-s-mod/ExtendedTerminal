package me.myogoo.extendedterminal.integration.itemList.emi.extendedcrafting;

import com.blakebr0.extendedcrafting.init.ModBlocks;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.ETSubscribeEvent;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiWorkStation;
import me.myogoo.extendedterminal.init.ETParts;

@ModAccessor.ExtendedCrafting
@ETEmiWorkStation
public class ECWorkStation {
    @ETSubscribeEvent
    public static void register(EmiRegistry registry) {
        registry.addWorkstation(ECRecipeCategory.BASIC_TABLE_CRAFTING_CATEGORY, EmiStack.of(ModBlocks.BASIC_TABLE.get()));
        registry.addWorkstation(ECRecipeCategory.BASIC_TABLE_CRAFTING_CATEGORY, EmiStack.of(ModBlocks.BASIC_AUTO_TABLE.get()));
        registry.addWorkstation(ECRecipeCategory.BASIC_TABLE_CRAFTING_CATEGORY, EmiStack.of(ETParts.BASIC_TERMINAL_PART.get()));

        registry.addWorkstation(ECRecipeCategory.ADVANCED_TABLE_CRAFTING_CATEGORY, EmiStack.of(ModBlocks.ADVANCED_TABLE.get()));
        registry.addWorkstation(ECRecipeCategory.ADVANCED_TABLE_CRAFTING_CATEGORY, EmiStack.of(ModBlocks.ADVANCED_AUTO_TABLE.get()));
        registry.addWorkstation(ECRecipeCategory.ADVANCED_TABLE_CRAFTING_CATEGORY, EmiStack.of(ETParts.ADVANCED_TERMINAL_PART.get()));

        registry.addWorkstation(ECRecipeCategory.ELITE_TABLE_CRAFTING_CATEGORY, EmiStack.of(ModBlocks.ELITE_TABLE.get()));
        registry.addWorkstation(ECRecipeCategory.ELITE_TABLE_CRAFTING_CATEGORY, EmiStack.of(ModBlocks.ELITE_AUTO_TABLE.get()));
        registry.addWorkstation(ECRecipeCategory.ELITE_TABLE_CRAFTING_CATEGORY, EmiStack.of(ETParts.ELITE_TERMINAL_PART.get()));

        registry.addWorkstation(ECRecipeCategory.ULTIMATE_TABLE_CRAFTING_CATEGORY, EmiStack.of(ModBlocks.ULTIMATE_TABLE.get()));
        registry.addWorkstation(ECRecipeCategory.ULTIMATE_TABLE_CRAFTING_CATEGORY, EmiStack.of(ModBlocks.ULTIMATE_AUTO_TABLE.get()));
        registry.addWorkstation(ECRecipeCategory.ULTIMATE_TABLE_CRAFTING_CATEGORY, EmiStack.of(ETParts.ULTIMATE_TERMINAL_PART.get()));
    }
}
