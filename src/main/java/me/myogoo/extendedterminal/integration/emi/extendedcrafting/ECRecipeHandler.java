package me.myogoo.extendedterminal.integration.emi.extendedcrafting;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiRecipeHandler;
import me.myogoo.extendedterminal.integration.emi.extendedcrafting.handler.ECTerminalRecipeHandler;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.AdvancedTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.EliteTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.UltimateTerminalMenu;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;

import java.util.function.Consumer;

import static me.myogoo.extendedterminal.integration.emi.extendedcrafting.ECWorkstation.*;

@ModAccessor.ExtendedCrafting
@ETEmiRecipeHandler
public class ECRecipeHandler {
    @MyotusSubscriber
    public static void register(EmiRegistry registry) {
        registry.addDeferredRecipes(recipe -> {
            addTerminalHandler(getEmiCategory(BASIC_TABLE_CATEGORY_ID),
                    category -> registry.addRecipeHandler(BasicTerminalMenu.TYPE,
                            new ECTerminalRecipeHandler<>(category, BasicTerminalMenu.class, ETMenuType.BASIC_TERMINAL)));
            addTerminalHandler(getEmiCategory(ADVANCED_TABLE_CATEGORY_ID),
                    category -> registry.addRecipeHandler(AdvancedTerminalMenu.TYPE,
                            new ECTerminalRecipeHandler<>(category, AdvancedTerminalMenu.class, ETMenuType.ADVANCED_TERMINAL)));
            addTerminalHandler(getEmiCategory(ELITE_TABLE_CATEGORY_ID),
                    category -> registry.addRecipeHandler(EliteTerminalMenu.TYPE,
                            new ECTerminalRecipeHandler<>(category, EliteTerminalMenu.class, ETMenuType.ELITE_TERMINAL)));
            addTerminalHandler(getEmiCategory(ULTIMATE_TABLE_CATEGORY_ID),
                    category -> registry.addRecipeHandler(UltimateTerminalMenu.TYPE,
                            new ECTerminalRecipeHandler<>(category, UltimateTerminalMenu.class, ETMenuType.ULTIMATE_TERMINAL)));
        });
    }

    private static void addTerminalHandler(EmiRecipeCategory category, Consumer<EmiRecipeCategory> registrar) {
        if (category != null) {
            registrar.accept(category);
        }
    }
}
