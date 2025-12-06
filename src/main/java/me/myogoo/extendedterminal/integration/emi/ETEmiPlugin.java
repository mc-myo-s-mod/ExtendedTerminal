package me.myogoo.extendedterminal.integration.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiRecipeHandler;
import me.myogoo.extendedterminal.api.integration.emi.ETEmiWorkstation;
import me.myogoo.extendedterminal.integration.ItemListModLoadHelper;

@EmiEntrypoint
public class ETEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        ItemListModLoadHelper
                .invokeItemListMod(ETEmiWorkstation.class, EmiRegistry.class, registry);
        ItemListModLoadHelper
                .invokeItemListMod(ETEmiRecipeHandler.class, EmiRegistry.class, registry);
    }
}
