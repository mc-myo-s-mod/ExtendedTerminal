package me.myogoo.extendedterminal.integration.emi.extendedterminal.wt;

import dev.emi.emi.api.EmiRegistry;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiRecipeHandler;
import me.myogoo.extendedterminal.integration.emi.extendedterminal.handler.panel.ETCraftingRecipeHandler;
import me.myogoo.extendedterminal.integration.emi.extendedterminal.handler.panel.ETSmithingRecipeHandler;
import me.myogoo.extendedterminal.integration.emi.extendedterminal.handler.panel.ETStonecutterRecipeHandler;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.wt.AE2WTLib;

@ETEmiRecipeHandler
@AE2WTLib
public class ETWTRecipeHandler {
    @MyotusSubscriber
    public static void init(EmiRegistry registry) {
        registry.addRecipeHandler(ETWTMenu.TYPE, new ETCraftingRecipeHandler<>(ETWTMenu.class));
        registry.addRecipeHandler(ETWTMenu.TYPE, new ETSmithingRecipeHandler<>(ETWTMenu.class));
        registry.addRecipeHandler(ETWTMenu.TYPE, new ETStonecutterRecipeHandler<>(ETWTMenu.class));
    }
}
