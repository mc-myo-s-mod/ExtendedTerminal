package me.myogoo.extendedterminal.datagen;

import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.datagen.provider.ChargerRecipeProvider;
import me.myogoo.extendedterminal.datagen.provider.InscriberRecipeProvider;
import me.myogoo.extendedterminal.datagen.provider.TagProvider;
import net.minecraft.core.HolderLookup;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = ExtendedTerminal.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ETDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var registries = event.getLookupProvider();
        var pack = generator.getVanillaPack(true);
        var existingFileHelper = event.getExistingFileHelper();


        pack.addProvider(p -> new InscriberRecipeProvider(p, registries));
        pack.addProvider(p -> new ChargerRecipeProvider(p, registries));

        //what? ;;
        var blockTagsProvider = pack.addProvider(p -> new BlockTagsProvider(p, registries, ExtendedTerminal.MODID, existingFileHelper) {
            @Override
            protected void addTags(HolderLookup.Provider provider) {

            }
        });

        pack.addProvider(p -> new TagProvider(p, registries, blockTagsProvider.contentsGetter(), event.getExistingFileHelper()));;
    }
}
