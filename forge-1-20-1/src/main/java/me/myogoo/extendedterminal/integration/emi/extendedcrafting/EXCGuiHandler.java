package me.myogoo.extendedterminal.integration.emi.extendedcrafting;

import dev.emi.emi.api.EmiRegistry;
import me.myogoo.extendedterminal.api.annotation.EpicExCrafting;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.EpicTerminalScreen;
import me.myogoo.extendedterminal.integration.emi.handler.EmiTableGuiHandler;
import me.myogoo.myotus.api.annotation.MyotusSubscriber;
import me.myogoo.myotus.api.annotation.itemList.emi.EMI;
import me.myogoo.myotus.api.annotation.itemList.emi.EMIGuiHandler;

@EpicExCrafting
@EMI
@EMIGuiHandler
public class EXCGuiHandler {
    @MyotusSubscriber
    public static void init(EmiRegistry registry) {
        registry.addExclusionArea(EpicTerminalScreen.class, new EmiTableGuiHandler<>());
    }
}
