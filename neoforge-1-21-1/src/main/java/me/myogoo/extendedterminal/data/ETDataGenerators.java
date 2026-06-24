package me.myogoo.extendedterminal.data;

import me.myogoo.extendedterminal.ExtendedTerminal;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = ExtendedTerminal.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ETDataGenerators {
    private ETDataGenerators() {
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var pack = event.getGenerator().getVanillaPack(true);
        var registries = event.getLookupProvider();
        pack.addProvider(ETRecipeDataProvider::new);
        pack.addProvider(packOutput -> new ETAE2RecipeProvider(packOutput, registries));
    }
}
