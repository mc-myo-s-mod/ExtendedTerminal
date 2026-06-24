package me.myogoo.extendedterminal.data;

import me.myogoo.extendedterminal.ExtendedTerminal;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExtendedTerminal.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ETDataGenerators {
    private ETDataGenerators() {
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();
        generator.addProvider(event.includeServer(), new ETRecipeDataProvider(output));
        generator.addProvider(event.includeServer(), new ETAE2RecipeProvider(output));
    }
}
