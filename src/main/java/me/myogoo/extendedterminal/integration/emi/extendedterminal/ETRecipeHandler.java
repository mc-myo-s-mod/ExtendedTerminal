package me.myogoo.extendedterminal.integration.emi.extendedterminal;

import dev.emi.emi.api.EmiRegistry;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiRecipeHandler;
import me.myogoo.extendedterminal.config.ExtendedTerminalConfig;
import me.myogoo.extendedterminal.integration.emi.extendedterminal.handler.ETCraftingRecipeHandler;
import me.myogoo.extendedterminal.integration.emi.extendedterminal.handler.ETSmithingRecipeHandler;
import me.myogoo.extendedterminal.integration.emi.extendedterminal.handler.ETStonecutterRecipeHandler;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;

@ETEmiRecipeHandler
public class ETRecipeHandler {
    @MyotusSubscriber
    public static void init(EmiRegistry registry) {
        var config = ExtendedTerminalConfig.INSTANCE.getExtendedTerminalConfig();
        if (!config.enableTerminal()) {
            return;
        }

        if (config.enableCraftingPanel()) {
            registry.addRecipeHandler(ETTerminalMenu.TYPE, new ETCraftingRecipeHandler());
        }
        if (config.enableSmithingPanel()) {
            registry.addRecipeHandler(ETTerminalMenu.TYPE, new ETSmithingRecipeHandler());
        }
        if (config.enableStonecutterPanel()) {
            registry.addRecipeHandler(ETTerminalMenu.TYPE, new ETStonecutterRecipeHandler());
        }
    }
}
