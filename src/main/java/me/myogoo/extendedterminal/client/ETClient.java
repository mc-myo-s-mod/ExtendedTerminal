package me.myogoo.extendedterminal.client;

import appeng.api.util.AEColor;
import appeng.client.render.StaticItemColor;
import appeng.init.client.InitScreens;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.AdvancedTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.BasicTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.EliteTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.UltimateTerminalScreen;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.extendedterminal.menu.extendedcrafting.AdvancedTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.EliteTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.UltimateTerminalMenu;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;

import static me.myogoo.extendedterminal.ExtendedTerminal.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ETClient {
    public ETClient(IEventBus eventBus) {
        eventBus.addListener(ETClient::initColorParts);
        initScreens();
    }

    public static void initScreens() {
        InitScreens.register(BasicTerminalMenu.TYPE, BasicTerminalScreen::new, "/screens/extended_terminal/basic_terminal.json");
        InitScreens.register(AdvancedTerminalMenu.TYPE, AdvancedTerminalScreen::new, "/screens/extended_terminal/advanced_terminal.json");
        InitScreens.register(EliteTerminalMenu.TYPE, EliteTerminalScreen::new, "/screens/extended_terminal/elite_terminal.json");
        InitScreens.register(UltimateTerminalMenu.TYPE, UltimateTerminalScreen::new, "/screens/extended_terminal/ultimate_terminal.json");
    }

    public static void initColorParts(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex)
                        -> (new StaticItemColor(AEColor.TRANSPARENT).getColor(stack, tintIndex) | 0xFF000000),
                            ETParts.TERMINAL_PARTS.stream().map(x -> (ItemLike)x).toArray(ItemLike[]::new));
    }
}

