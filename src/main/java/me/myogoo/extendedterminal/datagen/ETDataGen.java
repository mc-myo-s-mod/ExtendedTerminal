package me.myogoo.extendedterminal.datagen;

import appeng.datagen.providers.localization.LocalizationProvider;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.datagen.provider.ExtendedCraftingRecipeProvider;
import me.myogoo.extendedterminal.datagen.provider.InscriberRecipeProvider;
import me.myogoo.extendedterminal.datagen.provider.ReAvaritiaCraftingRecipeProvider;
import me.myogoo.extendedterminal.util.mod.ModLoadHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExtendedTerminal.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ETDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        ModLoadHelper.init();
        var registryAccess = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        var registries = new LocalizationProvider(event.getGenerator());

        var generator = event.getGenerator();
        var pack = generator.getVanillaPack(true);
        pack.addProvider(InscriberRecipeProvider::new);
        pack.addProvider(ExtendedCraftingRecipeProvider::new);
        pack.addProvider(ReAvaritiaCraftingRecipeProvider::new);
    }
}