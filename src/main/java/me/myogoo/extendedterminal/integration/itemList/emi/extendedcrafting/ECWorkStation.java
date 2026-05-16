package me.myogoo.extendedterminal.integration.itemList.emi.extendedcrafting;

import com.blakebr0.extendedcrafting.ExtendedCrafting;
import com.blakebr0.extendedcrafting.init.ModBlocks;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.registry.EmiRecipes;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiWorkStation;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.itemList.jei.JEI;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@ModAccessor.ExtendedCrafting
@ETEmiWorkStation
public class ECWorkStation {
    public static final ResourceLocation BASIC_TABLE_CATEGORY_ID = ExtendedCrafting.resource("basic_crafting");
    public static final ResourceLocation ADVANCED_TABLE_CATEGORY_ID = ExtendedCrafting.resource("advanced_crafting");
    public static final ResourceLocation ELITE_TABLE_CATEGORY_ID = ExtendedCrafting.resource("elite_crafting");
    public static final ResourceLocation ULTIMATE_TABLE_CATEGORY_ID = ExtendedCrafting.resource("ultimate_crafting");

    @MyotusSubscriber
    public static void register(EmiRegistry registry) {
        if (!MyotusAPI.modIntegrationManager().isLoaded(JEI.class)) {
            return;
        }

        registry.addDeferredRecipes(recipe -> {
            registry.addWorkstation(getEmiCategory(BASIC_TABLE_CATEGORY_ID), EmiStack.of(ETParts.BASIC_TERMINAL_PART));
            registry.addWorkstation(getEmiCategory(ADVANCED_TABLE_CATEGORY_ID), EmiStack.of(ETParts.ADVANCED_TERMINAL_PART));
            registry.addWorkstation(getEmiCategory(ELITE_TABLE_CATEGORY_ID), EmiStack.of(ETParts.ELITE_TERMINAL_PART));
            registry.addWorkstation(getEmiCategory(ULTIMATE_TABLE_CATEGORY_ID), EmiStack.of(ETParts.ULTIMATE_TERMINAL_PART));
        });
    }

    public static @Nullable EmiRecipeCategory getEmiCategory(ResourceLocation id) {
        return EmiRecipes.categories
                .stream()
                .filter(category -> category.getId().equals(id))
                .findFirst().orElse(null);
    }
}
