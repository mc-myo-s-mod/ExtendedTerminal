package me.myogoo.extendedterminal.integration.emi.extendedcrafting;

import dev.emi.emi.api.EmiRegistry;
import me.myogoo.extendedterminal.api.annotation.ExtendedCrafting;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.AdvancedTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.BasicTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.EliteTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.UltimateTerminalScreen;
import me.myogoo.extendedterminal.integration.emi.handler.EmiTableGuiHandler;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.itemList.emi.EMI;
import me.myogoo.myotus.api.annotation.itemList.emi.EMIGuiHandler;

@ExtendedCrafting
@EMI
@EMIGuiHandler
public class ECGuiHandler {
    @MyotusSubscriber
    public static void init(EmiRegistry registry) {
        registry.addExclusionArea(BasicTerminalScreen.class, new EmiTableGuiHandler<>());
        registry.addExclusionArea(AdvancedTerminalScreen.class, new EmiTableGuiHandler<>());
        registry.addExclusionArea(EliteTerminalScreen.class, new EmiTableGuiHandler<>());
        registry.addExclusionArea(UltimateTerminalScreen.class, new EmiTableGuiHandler<>());
    }
}
