package me.myogoo.extendedterminal.init;

import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.config.ExtendedTerminalConfig;
import me.myogoo.extendedterminal.config.avaritiaNeo.AvaritiaNeoConfig;
import me.myogoo.extendedterminal.config.avaritiaRe.AvaritiaReConfig;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ExtendedTerminal.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ETConfig {
    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ExtendedTerminalConfig.COMMON, "extendedterminal/ExtendedTerminal-common.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AvaritiaReConfig.COMMON, "extendedterminal/addons/ReAvaritia.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AvaritiaNeoConfig.COMMON, "extendedterminal/addons/AvaritiaNeo.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ExtendedCraftingConfig.COMMON, "extendedterminal/addons/ExtendedCrafting.toml");
    }

    private static final Map<Object, Runnable> BAKERS;
    static {
        IdentityHashMap<Object, Runnable> m = new IdentityHashMap<>();
        m.put(ExtendedTerminalConfig.COMMON, ExtendedTerminalConfig.INSTANCE::bake);
        m.put(AvaritiaReConfig.COMMON, AvaritiaReConfig.INSTANCE::bake);
        m.put(AvaritiaNeoConfig.COMMON, AvaritiaNeoConfig.INSTANCE::bake);
        m.put(ExtendedCraftingConfig.COMMON, ExtendedCraftingConfig.INSTANCE::bake);
        BAKERS = Collections.unmodifiableMap(m);
    }

    @SubscribeEvent
    static void bake(final ModConfigEvent event) {
        Runnable baker = BAKERS.get(event.getConfig().getSpec());
        if (baker != null) {
            baker.run();
        }
    }
}
