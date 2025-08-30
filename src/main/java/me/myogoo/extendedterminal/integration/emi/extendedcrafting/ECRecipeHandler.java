package me.myogoo.extendedterminal.integration.emi.extendedcrafting;

import com.blakebr0.extendedcrafting.init.ModMenuTypes;
import dev.emi.emi.api.EmiRegistry;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.api.SubscribeLoadEvent;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiRecipeHandler;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.extendedterminal.integration.emi.extendedcrafting.handler.ECTableRecipeHandler;
import me.myogoo.extendedterminal.integration.emi.extendedcrafting.handler.ECTerminalRecipeHandler;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.AdvancedTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.EliteTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.UltimateTerminalMenu;

@ModAccessor.ExtendedCrafting
@ETEmiRecipeHandler
public class ECRecipeHandler {
    @SubscribeLoadEvent
    public static void register(EmiRegistry registry) {
        registry.addRecipeHandler(ModMenuTypes.BASIC_TABLE.get(), new ECTableRecipeHandler<>(ECRecipeCategory.BASIC_TABLE_CRAFTING_CATEGORY, ETMenuType.BASIC_TERMINAL.getGridSize()));
        registry.addRecipeHandler(ModMenuTypes.ADVANCED_TABLE.get(), new ECTableRecipeHandler<>(ECRecipeCategory.ADVANCED_TABLE_CRAFTING_CATEGORY, ETMenuType.ADVANCED_TERMINAL.getGridSize()));
        registry.addRecipeHandler(ModMenuTypes.ELITE_TABLE.get(), new ECTableRecipeHandler<>(ECRecipeCategory.ELITE_TABLE_CRAFTING_CATEGORY, ETMenuType.ELITE_TERMINAL.getGridSize()));
        registry.addRecipeHandler(ModMenuTypes.ULTIMATE_TABLE.get(), new ECTableRecipeHandler<>(ECRecipeCategory.ULTIMATE_TABLE_CRAFTING_CATEGORY, ETMenuType.ULTIMATE_TERMINAL.getGridSize()));

        registry.addRecipeHandler(ModMenuTypes.BASIC_AUTO_TABLE.get(), new ECTableRecipeHandler<>(ECRecipeCategory.BASIC_TABLE_CRAFTING_CATEGORY, ETMenuType.BASIC_TERMINAL.getGridSize()));
        registry.addRecipeHandler(ModMenuTypes.ADVANCED_AUTO_TABLE.get(), new ECTableRecipeHandler<>(ECRecipeCategory.ADVANCED_TABLE_CRAFTING_CATEGORY, ETMenuType.ADVANCED_TERMINAL.getGridSize()));
        registry.addRecipeHandler(ModMenuTypes.ELITE_AUTO_TABLE.get(), new ECTableRecipeHandler<>(ECRecipeCategory.ELITE_TABLE_CRAFTING_CATEGORY, ETMenuType.ELITE_TERMINAL.getGridSize()));
        registry.addRecipeHandler(ModMenuTypes.ULTIMATE_AUTO_TABLE.get(), new ECTableRecipeHandler<>(ECRecipeCategory.ULTIMATE_TABLE_CRAFTING_CATEGORY, ETMenuType.ULTIMATE_TERMINAL.getGridSize()));

        registry.addRecipeHandler(BasicTerminalMenu.TYPE, new ECTerminalRecipeHandler<>(ECRecipeCategory.BASIC_TABLE_CRAFTING_CATEGORY, BasicTerminalMenu.class, ETMenuType.BASIC_TERMINAL));
        registry.addRecipeHandler(AdvancedTerminalMenu.TYPE, new ECTerminalRecipeHandler<>(ECRecipeCategory.ADVANCED_TABLE_CRAFTING_CATEGORY, AdvancedTerminalMenu.class, ETMenuType.ADVANCED_TERMINAL));
        registry.addRecipeHandler(EliteTerminalMenu.TYPE, new ECTerminalRecipeHandler<>(ECRecipeCategory.ELITE_TABLE_CRAFTING_CATEGORY, EliteTerminalMenu.class, ETMenuType.ELITE_TERMINAL));
        registry.addRecipeHandler(UltimateTerminalMenu.TYPE, new ECTerminalRecipeHandler<>(ECRecipeCategory.ULTIMATE_TABLE_CRAFTING_CATEGORY, UltimateTerminalMenu.class, ETMenuType.ULTIMATE_TERMINAL));
    }
}
