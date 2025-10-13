package me.myogoo.extendedterminal;

import com.mojang.logging.LogUtils;
import me.myogoo.extendedterminal.config.ETConfig;
import me.myogoo.extendedterminal.init.*;
import me.myogoo.extendedterminal.init.wt.WTInit;
import me.myogoo.extendedterminal.util.mod.ModIntegrationManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(ExtendedTerminal.MODID)
public class ExtendedTerminal {
    public static final String MODID = "extendedterminal";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ExtendedTerminal(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, ETConfig.COMMON);
        ModIntegrationManager.initialize();

        ETItems.REGISTER.register(modEventBus);
        ETCreativeTab.REGISTER.register(modEventBus);
        ETParts.REGISTER.register(modEventBus);
        ETMenus.REGISTER.register(modEventBus);
        ETCondition.REGISTER.register(modEventBus);
        ETDataComponent.REGISTER.register(modEventBus);

        NeoForge.EVENT_BUS.register(ETRecipeGen.class);
        modEventBus.addListener(ETNetwork::init);

        modEventBus.addListener(WTInit::init);
        modEventBus.addListener(WTInit::initCapabilities);
    }

    public static ResourceLocation makeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID,path);
    }
}
