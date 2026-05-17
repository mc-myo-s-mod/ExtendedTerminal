package me.myogoo.extendedterminal;

import com.mojang.logging.LogUtils;

import me.myogoo.extendedterminal.api.ModAccessor;
import me.myogoo.extendedterminal.init.*;
import me.myogoo.extendedterminal.init.wt.WTInits;
import me.myogoo.extendedterminal.init.wt.WTItems;
import me.myogoo.extendedterminal.init.wt.WTMenus;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.wt.AE2WTLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExtendedTerminal.MODID)
public class ExtendedTerminal {
    public static final String MODID = "extendedterminal";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ExtendedTerminal() {
        ETConfig.init();
        ETModIntegration.initialize();
        ETNetwork.register();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ETBlocks.REGISTER.register(modEventBus);
        ETItems.REGISTER.register(modEventBus);
        ETParts.REGISTER.register(modEventBus);
        ETMenus.REGISTER.register(modEventBus);
        ETCreativeTab.REGISTER.register(modEventBus);
        if (MyotusAPI.modIntegrationManager().isLoaded(AE2WTLib.class)) {
            WTItems.register();
            WTMenus.register();
            WTInits.registerTerminal();
        }
        if (MyotusAPI.modIntegrationManager().isLoaded(ModAccessor.InvTweaks.class)) {
            InterModComms.sendTo("invtweaks", "blacklist-screen",
                    () -> "me.myogoo.extendedterminal.client.screen.*");
            InterModComms.sendTo("invtweaks", "blacklist-screen",
                    () -> "me.myogoo.extendedterminal.menu.*");
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static ResourceLocation makeId(String path) {
        return new ResourceLocation(MODID, path);
    }
}
