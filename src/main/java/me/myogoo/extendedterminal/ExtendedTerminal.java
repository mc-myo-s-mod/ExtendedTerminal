package me.myogoo.extendedterminal;

import appeng.api.features.GridLinkables;
import appeng.items.tools.powered.WirelessTerminalItem;
import com.mojang.logging.LogUtils;
import de.mari_023.ae2wtlib.api.gui.Icon;
import de.mari_023.ae2wtlib.api.registration.AddTerminalEvent;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;
import de.mari_023.ae2wtlib.api.registration.WTDefinitionBuilder;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import me.myogoo.extendedterminal.config.ETConfig;
import me.myogoo.extendedterminal.init.*;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import me.myogoo.extendedterminal.util.mod.ModIntegrationManager;
import me.myogoo.extendedterminal.util.mod.SupportedMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
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

        modEventBus.addListener((RegisterEvent e) -> {
            if (e.getRegistryKey().equals(Registries.ITEM)) {
                    AddTerminalEvent.register(
                            event -> event.builder("et_wt", ETWTHost::new, ETWTMenu.TYPE, (ItemWT) ETItems.ET_WT.asItem(), Icon.CRAFTING)
                                    .addTerminal());
                    GridLinkables.register(ETItems.ET_WT, WirelessTerminalItem.LINKABLE_HANDLER);
            }
        });
    }

    void onCommonSetup(FMLCommonSetupEvent setupEvent) {
    }

    public static ResourceLocation makeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID,path);
    }
}
