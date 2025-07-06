package me.myogoo.extendedterminal.client;

import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.client.provider.ExtendedCraftingRecipeProvider;
import me.myogoo.extendedterminal.client.provider.InscriberRecipeProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = ExtendedTerminal.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ETDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var registries = event.getLookupProvider();
        var pack = generator.getVanillaPack(true);

        pack.addProvider((p) -> new InscriberRecipeProvider(p,registries));
        pack.addProvider((p) -> new ExtendedCraftingRecipeProvider(p,registries));
    }
}
