package me.myogoo.extendedterminal;

import com.mojang.logging.LogUtils;
import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.init.*;
import me.myogoo.extendedterminal.init.wt.WTInit;
import me.myogoo.extendedterminal.init.wt.WTItems;
import me.myogoo.extendedterminal.init.wt.WTMenus;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.wt.AE2WTLib;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(ExtendedTerminal.MODID)
public class ExtendedTerminal {
    public static final String MODID = "extendedterminal";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ExtendedTerminal(IEventBus modEventBus, ModContainer modContainer) {
        ETModIntegration.initialize();
        ETConfig.initialize(modContainer);

        ETItems.REGISTER.register(modEventBus);
        ETCreativeTab.REGISTER.register(modEventBus);
        ETParts.REGISTER.register(modEventBus);
        ETMenus.REGISTER.register(modEventBus);
        ETDataComponent.REGISTER.register(modEventBus);

        if(MyotusAPI.modIntegrationManager().isLoaded(AE2WTLib.class)) {
            WTItems.register();
            WTMenus.register();
            WTInit.registerTerminal();
            modEventBus.addListener(EventPriority.LOWEST, WTInit::registerUpgradeTooltips);
            modEventBus.addListener(WTInit::initCapabilities);
        }
        if(MyotusAPI.modIntegrationManager().isLoaded(ModAccessor.InvTweaks.class)) {
            InterModComms.sendTo("invtweaks", "blacklist-screen", () -> "me.myogoo.extendedterminal.client.screen.*");
            InterModComms.sendTo("invtweaks", "blacklist-screen", () -> "me.myogoo.extendedterminal.menu.*");
        }

        NeoForge.EVENT_BUS.register(ETRecipeGen.class);
        modEventBus.addListener(ETNetwork::init);


        modEventBus.addListener(this::commonSetup);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ETConfigTab::initialize);
    }

    public static ResourceLocation makeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
