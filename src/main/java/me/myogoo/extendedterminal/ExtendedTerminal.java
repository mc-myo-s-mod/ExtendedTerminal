package me.myogoo.extendedterminal;

import com.mojang.logging.LogUtils;
import me.myogoo.extendedterminal.client.ETClient;
import me.myogoo.extendedterminal.client.ETConfig;
import me.myogoo.extendedterminal.init.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExtendedTerminal.MODID)
public class ExtendedTerminal {
    public static final String MODID = "extendedterminal";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ExtendedTerminal() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ETBlocks.REGISTER.register(modEventBus);
        ETItems.REGISTER.register(modEventBus);
        ETParts.REGISTER.register(modEventBus);
        ETMenus.REGISTER.register(modEventBus);
        ETCreativeTab.REGISTER.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        ETClient.init(modEventBus);

        //ModLoadingContext.get().registerConfig(ETConfig.Type.COMMON, Config.SPEC);
    }

    public static ResourceLocation makeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
