package me.myogoo.extendedterminal.integration.emi.extendedcrafting;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.registry.EmiRecipes;
import me.myogoo.myotus.api.annotation.itemList.RecipeCategory;
import me.myogoo.myotus.api.annotation.itemList.emi.EMI;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.extendedterminal.init.wt.WTItems;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import me.myogoo.extendedterminal.api.annotation.ExtendedCrafting;

@ExtendedCrafting
@EMI
@RecipeCategory
public class ECWorkstation {
    private static final String EXTENDED_CRAFTING = "extendedcrafting";
    public static final ResourceLocation BASIC_TABLE_CATEGORY_ID =
            new ResourceLocation(EXTENDED_CRAFTING, "basic_crafting");
    public static final ResourceLocation ADVANCED_TABLE_CATEGORY_ID =
            new ResourceLocation(EXTENDED_CRAFTING, "advanced_crafting");
    public static final ResourceLocation ELITE_TABLE_CATEGORY_ID =
            new ResourceLocation(EXTENDED_CRAFTING, "elite_crafting");
    public static final ResourceLocation ULTIMATE_TABLE_CATEGORY_ID =
            new ResourceLocation(EXTENDED_CRAFTING, "ultimate_crafting");
    public static final ResourceLocation LEGENDARY_TABLE_CATEGORY_ID =
            new ResourceLocation(EXTENDED_CRAFTING, "legendary_crafting");

    @MyotusSubscriber
    public static void register(EmiRegistry registry) {
        registry.addDeferredRecipes(recipe -> {
            addWorkstation(registry, getEmiCategory(BASIC_TABLE_CATEGORY_ID), EmiStack.of(ETParts.BASIC_TERMINAL_PART));
            addWorkstation(registry, getEmiCategory(ADVANCED_TABLE_CATEGORY_ID), EmiStack.of(ETParts.ADVANCED_TERMINAL_PART));
            addWorkstation(registry, getEmiCategory(ELITE_TABLE_CATEGORY_ID), EmiStack.of(ETParts.ELITE_TERMINAL_PART));
            addWorkstation(registry, getEmiCategory(ULTIMATE_TABLE_CATEGORY_ID), EmiStack.of(ETParts.ULTIMATE_TERMINAL_PART));
            addWorkstation(registry, getEmiCategory(LEGENDARY_TABLE_CATEGORY_ID), EmiStack.of(ETParts.LEGENDARY_TERMINAL_PART));
            addWorkstation(registry, getEmiCategory(BASIC_TABLE_CATEGORY_ID), EmiStack.of(ETParts.UNITED_TERMINAL_PART));
            addWorkstation(registry, getEmiCategory(ADVANCED_TABLE_CATEGORY_ID), EmiStack.of(ETParts.UNITED_TERMINAL_PART));
            addWorkstation(registry, getEmiCategory(ELITE_TABLE_CATEGORY_ID), EmiStack.of(ETParts.UNITED_TERMINAL_PART));
            addWorkstation(registry, getEmiCategory(ULTIMATE_TABLE_CATEGORY_ID), EmiStack.of(ETParts.UNITED_TERMINAL_PART));
            if (MyotusAPI.integrations().isLoaded(AE2WTLib.class)) {
                var wirelessUnited = EmiStack.of(WTItems.WIRELESS_UNITED_TERMINAL.asItem());
                addWorkstation(registry, getEmiCategory(BASIC_TABLE_CATEGORY_ID), wirelessUnited);
                addWorkstation(registry, getEmiCategory(ADVANCED_TABLE_CATEGORY_ID), wirelessUnited);
                addWorkstation(registry, getEmiCategory(ELITE_TABLE_CATEGORY_ID), wirelessUnited);
                addWorkstation(registry, getEmiCategory(ULTIMATE_TABLE_CATEGORY_ID), wirelessUnited);
            }
        });
    }

    private static void addWorkstation(EmiRegistry registry, @Nullable EmiRecipeCategory category,
            EmiStack workstation) {
        if (category != null) {
            registry.addWorkstation(category, workstation);
        }
    }

    public static @Nullable EmiRecipeCategory getEmiCategory(ResourceLocation id) {
        return EmiRecipes.categories
                .stream()
                .filter(category -> category.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
