package me.myogoo.extendedterminal.integration.itemList.emi.extendedterminal;

import dev.emi.emi.api.EmiRegistry;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiRecipeHandler;
import me.myogoo.extendedterminal.integration.itemList.emi.extendedterminal.handler.pnael.ETCraftingRecipeHandler;
import me.myogoo.extendedterminal.integration.itemList.emi.extendedterminal.handler.pnael.ETSmithingRecipeHandler;
import me.myogoo.extendedterminal.integration.itemList.emi.extendedterminal.handler.pnael.ETStonecutterRecipeHandler;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;

@ETEmiRecipeHandler
public class ETRecipeHandler {
    @MyotusSubscriber
    public static void init(EmiRegistry registry) {
        registry.addRecipeHandler(ETTerminalMenu.TYPE, new ETCraftingRecipeHandler<>(ETTerminalMenu.class));
        registry.addRecipeHandler(ETTerminalMenu.TYPE, new ETSmithingRecipeHandler<>(ETTerminalMenu.class));
        registry.addRecipeHandler(ETTerminalMenu.TYPE, new ETStonecutterRecipeHandler<>(ETTerminalMenu.class));
    }
}
