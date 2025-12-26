package me.myogoo.extendedterminal.integration.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import net.neoforged.fml.ModList;

@EmiEntrypoint
public class ETEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        EmiRegisterHelper.registerCategories(registry);
        EmiRegisterHelper.registerWorkStations(registry);
        EmiRegisterHelper.registerRecipeHandlers(registry);
        EmiRegisterHelper.addRecipes(registry);
    }
}
