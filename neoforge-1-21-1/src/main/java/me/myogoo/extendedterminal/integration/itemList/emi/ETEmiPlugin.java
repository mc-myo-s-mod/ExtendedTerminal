package me.myogoo.extendedterminal.integration.itemList.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

@EmiEntrypoint
public class ETEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        EmiRegisterHelper.registerCategories(registry);
        EmiRegisterHelper.registerWorkStations(registry);
        EmiRegisterHelper.registerRecipeHandlers(registry);
    }
}
