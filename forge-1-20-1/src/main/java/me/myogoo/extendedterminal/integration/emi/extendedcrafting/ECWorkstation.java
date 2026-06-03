package me.myogoo.extendedterminal.integration.emi.extendedcrafting;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.registry.EmiRecipes;
import me.myogoo.myotus.api.annotation.itemList.RecipeCategory;
import me.myogoo.myotus.api.annotation.itemList.emi.EMI;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
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

    @MyotusSubscriber
    public static void register(EmiRegistry registry) {
        registry.addDeferredRecipes(recipe -> {
            addWorkstation(registry, getEmiCategory(BASIC_TABLE_CATEGORY_ID), EmiStack.of(ETParts.BASIC_TERMINAL_PART));
            addWorkstation(registry, getEmiCategory(ADVANCED_TABLE_CATEGORY_ID), EmiStack.of(ETParts.ADVANCED_TERMINAL_PART));
            addWorkstation(registry, getEmiCategory(ELITE_TABLE_CATEGORY_ID), EmiStack.of(ETParts.ELITE_TERMINAL_PART));
            addWorkstation(registry, getEmiCategory(ULTIMATE_TABLE_CATEGORY_ID), EmiStack.of(ETParts.ULTIMATE_TERMINAL_PART));
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
