package me.myogoo.extendedterminal.client;

import appeng.api.util.AEColor;
import appeng.client.render.StaticItemColor;
import appeng.init.client.InitScreens;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.client.screen.avaritiaNeo.NeoExtremeTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritiaRe.EndTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritiaRe.ExtremeTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritiaRe.NetherTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritiaRe.SculkTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.*;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.extendedterminal.menu.avaritiaNeo.NeoExtremeTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.EndTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.ExtremeTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.NetherTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.SculkTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static me.myogoo.extendedterminal.ExtendedTerminal.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ETClient {

    @SubscribeEvent
    public static void init(RegisterColorHandlersEvent.Item event) {
        initColorParts(event);
        initScreens();
    }

    public static void initScreens() {
        InitScreens.register(BasicTerminalMenu.TYPE, BasicTerminalScreen::new, "/screens/extended_terminal/basic_terminal.json");
        InitScreens.register(AdvancedTerminalMenu.TYPE, AdvancedTerminalScreen::new, "/screens/extended_terminal/advanced_terminal.json");
        InitScreens.register(EliteTerminalMenu.TYPE, EliteTerminalScreen::new, "/screens/extended_terminal/elite_terminal.json");
        InitScreens.register(UltimateTerminalMenu.TYPE, UltimateTerminalScreen::new, "/screens/extended_terminal/ultimate_terminal.json");
        InitScreens.register(EpicTerminalMenu.TYPE, EpicTerminalScreen::new, "/screens/extended_terminal/epic_terminal.json");

        InitScreens.register(SculkTerminalMenu.TYPE, SculkTerminalScreen::new, "/screens/avaritia/sculk_terminal.json");
        InitScreens.register(NetherTerminalMenu.TYPE, NetherTerminalScreen::new, "/screens/avaritia/nether_terminal.json");
        InitScreens.register(EndTerminalMenu.TYPE, EndTerminalScreen::new, "/screens/avaritia/end_terminal.json");
        InitScreens.register(ExtremeTerminalMenu.TYPE,ExtremeTerminalScreen::new, "/screens/avaritia/extreme_terminal.json");

        InitScreens.register(NeoExtremeTerminalMenu.TYPE, NeoExtremeTerminalScreen::new, "/screens/avaritia/extreme_terminal.json");
    }

    public static void initColorParts(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex)
                        -> (new StaticItemColor(AEColor.TRANSPARENT).getColor(stack, tintIndex) | 0xFF000000),
                            ETParts.TERMINAL_PARTS.stream().map(x -> (ItemLike)x).toArray(ItemLike[]::new));
    }
}

