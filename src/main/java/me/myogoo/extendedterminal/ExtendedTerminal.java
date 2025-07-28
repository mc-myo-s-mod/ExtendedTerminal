package me.myogoo.extendedterminal;

import com.mojang.logging.LogUtils;
import me.myogoo.extendedterminal.config.ConfigRecipeManager;
import me.myogoo.extendedterminal.config.ETConfig;
import me.myogoo.extendedterminal.event.RecipeManagerLoadingEvent;
import me.myogoo.extendedterminal.init.*;
import me.myogoo.extendedterminal.util.mod.ModLoadHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ExtendedTerminal.MODID)
public class ExtendedTerminal {
    public static final String MODID = "extendedterminal";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ExtendedTerminal(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, ETConfig.COMMON);
        ModLoadHelper.init();

        ETCreativeTab.REGISTER.register(modEventBus);
        ETItems.REGISTER.register(modEventBus);
        ETParts.REGISTER.register(modEventBus);
        ETMenus.REGISTER.register(modEventBus);

        NeoForge.EVENT_BUS.register(ConfigRecipeManager.class);
        modEventBus.addListener(ETNetwork::init);
    }


    public static ResourceLocation makeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID,path);
    }
}
