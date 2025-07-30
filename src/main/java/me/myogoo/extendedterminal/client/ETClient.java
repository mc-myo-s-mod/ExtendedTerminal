package me.myogoo.extendedterminal.client;

import appeng.api.util.AEColor;
import appeng.client.render.StaticItemColor;
import appeng.init.client.InitScreens;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.client.screen.avaritia.EndTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritia.ExtremeTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritia.NetherTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritia.SculkTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.AdvancedTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.BasicTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.EliteTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.UltimateTerminalScreen;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.extendedterminal.menu.avaritia.EndTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritia.ExtremeTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritia.NetherTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritia.SculkTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.AdvancedTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.EliteTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.UltimateTerminalMenu;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = ExtendedTerminal.MODID, dist = Dist.CLIENT)
public class ETClient {
    public ETClient(IEventBus eventBus) {
        eventBus.addListener(RegisterColorHandlersEvent.Item.class, ETClient::initColorParts);
        eventBus.addListener(ETClient::initScreens);
    }

    public static void initScreens(RegisterMenuScreensEvent event) {
        // extended crafting terminals
        InitScreens.register(event, BasicTerminalMenu.TYPE, BasicTerminalScreen::new, "/screens/extended_terminal/basic_terminal.json");
        InitScreens.register(event, AdvancedTerminalMenu.TYPE, AdvancedTerminalScreen::new, "/screens/extended_terminal/advanced_terminal.json");
        InitScreens.register(event, EliteTerminalMenu.TYPE, EliteTerminalScreen::new, "/screens/extended_terminal/elite_terminal.json");
        InitScreens.register(event, UltimateTerminalMenu.TYPE, UltimateTerminalScreen::new, "/screens/extended_terminal/ultimate_terminal.json");

        // avaritia terminals
        InitScreens.register(event, SculkTerminalMenu.TYPE, SculkTerminalScreen::new, "/screens/avaritia/sculk_terminal.json");
        InitScreens.register(event, NetherTerminalMenu.TYPE, NetherTerminalScreen::new, "/screens/avaritia/nether_terminal.json");
        InitScreens.register(event, EndTerminalMenu.TYPE, EndTerminalScreen::new, "/screens/avaritia/end_terminal.json");
        InitScreens.register(event, ExtremeTerminalMenu.TYPE, ExtremeTerminalScreen::new, "/screens/avaritia/extreme_terminal.json");
    }

    public static void initColorParts(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex)
                        -> (new StaticItemColor(AEColor.TRANSPARENT).getColor(stack, tintIndex) | 0xFF000000),
                            ETParts.TERMINAL_PARTS.stream().map(x -> (ItemLike)x).toArray(ItemLike[]::new));
    }
}

