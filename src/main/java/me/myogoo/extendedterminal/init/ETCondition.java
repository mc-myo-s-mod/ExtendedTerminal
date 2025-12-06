package me.myogoo.extendedterminal.init;

import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.datagen.condition.serializer.ETModConditionSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = ExtendedTerminal.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ETCondition {

    @SubscribeEvent
    public static void onCommonFMLSetupEvent(FMLCommonSetupEvent event) {
        event.enqueueWork(ETCondition::register);
    }

    public static void register() {
        CraftingHelper.register(ETModConditionSerializer.INSTANCE);
    }
}
