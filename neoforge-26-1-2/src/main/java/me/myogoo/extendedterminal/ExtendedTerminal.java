package me.myogoo.extendedterminal;

import com.mojang.logging.LogUtils;
import me.myogoo.extendedterminal.api.annotation.ExtendedCrafting;
import me.myogoo.extendedterminal.api.annotation.ReAvaritia;
import me.myogoo.extendedterminal.integration.ae2helpers.AE2HelpersCompat;
import me.myogoo.extendedterminal.integration.ae2helpers.AE2HelpersUpgradeRegistration;
import me.myogoo.extendedterminal.init.*;
import me.myogoo.extendedterminal.init.wt.WTInit;
import me.myogoo.extendedterminal.init.wt.WTItems;
import me.myogoo.extendedterminal.init.wt.WTMenus;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import org.slf4j.Logger;

@Mod(ExtendedTerminal.MODID)
public class ExtendedTerminal {
    public static final String MODID = "extendedterminal";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ExtendedTerminal(IEventBus modEventBus, ModContainer modContainer) {
        ETModIntegration.initialize();
        AE2HelpersCompat.logDetectedState(LOGGER);
        ETConfig.initialize(modContainer);

        ETItems.REGISTER.register(modEventBus);
        ETCreativeTab.REGISTER.register(modEventBus);
        ETParts.REGISTER.register(modEventBus);
        ETMenus.REGISTER.register(modEventBus);
        ETDataComponent.REGISTER.register(modEventBus);
        modEventBus.addListener(EventPriority.LOWEST, AE2HelpersUpgradeRegistration::registerTerminalPartUpgrades);

        if(MyotusAPI.integrations().isLoaded(AE2WTLib.class)) {
            WTItems.register();
            WTMenus.register();
            WTInit.registerTerminal();
            modEventBus.addListener(EventPriority.LOWEST, WTInit::registerUpgradeTooltips);
            modEventBus.addListener(WTInit::initCapabilities);
        }

        modEventBus.addListener(ETNetwork::init);
        NeoForge.EVENT_BUS.addListener(ExtendedTerminal::synchronizeTableRecipes);

    }

    private static void synchronizeTableRecipes(OnDatapackSyncEvent event) {
        if (MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
            event.sendRecipes(com.blakebr0.extendedcrafting.init.ModRecipeTypes.TABLE.get());
        }
        if (MyotusAPI.integrations().isLoaded(ReAvaritia.class)) {
            event.sendRecipes(committee.nova.mods.avaritia.init.registry.ModRecipeTypes.CRAFTING_TABLE_RECIPE.get());
        }
    }

    public static Identifier makeId(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
