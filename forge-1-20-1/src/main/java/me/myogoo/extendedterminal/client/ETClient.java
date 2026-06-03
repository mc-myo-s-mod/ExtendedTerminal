package me.myogoo.extendedterminal.client;

import appeng.api.util.AEColor;
import appeng.client.render.StaticItemColor;
import appeng.init.client.InitScreens;
import me.myogoo.extendedterminal.client.screen.avaritiaNeo.NeoExtremeTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritiaRe.EndTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritiaRe.ExtremeTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritiaRe.NetherTerminalScreen;
import me.myogoo.extendedterminal.client.screen.avaritiaRe.SculkTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedcrafting.*;
import me.myogoo.extendedterminal.client.screen.extendedterminal.ETTerminalScreen;
import me.myogoo.extendedterminal.client.screen.extendedterminal.wt.ETWTScreen;
import me.myogoo.extendedterminal.init.ETConfigTab;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.extendedterminal.integration.polymorph.ETPolymorph;
import me.myogoo.extendedterminal.menu.avaritiaNeo.NeoExtremeTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.EndTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.ExtremeTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.NetherTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.SculkTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.*;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static me.myogoo.extendedterminal.ExtendedTerminal.MODID;
import me.myogoo.extendedterminal.api.annotation.Polymorph;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ETClient {

    @SubscribeEvent
    public static void init(RegisterColorHandlersEvent.Item event) {
        initColorParts(event);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(ETClient::initScreens);
        event.enqueueWork(ETConfigTab::initialize);

        event.enqueueWork(() -> {
            if (MyotusAPI.integrations().isLoaded(AE2WTLib.class)) {
                initWTScreen();
            }
            if (MyotusAPI.integrations().isLoaded(Polymorph.class)) {
                ETPolymorph.init();
            }
        });
    }

    public static void initScreens() {
        InitScreens.<ETTerminalMenu, ETTerminalScreen<ETTerminalMenu>>register(ETTerminalMenu.TYPE,
                (menu, playerInventory, title, style) -> new ETTerminalScreen<>(menu, playerInventory, title, style),
                "/screens/et_terminal.json");

        InitScreens.register(BasicTerminalMenu.TYPE,BasicTerminalScreen::new,"/screens/extended_terminal/basic_terminal.json");
        InitScreens.register(AdvancedTerminalMenu.TYPE,AdvancedTerminalScreen::new,"/screens/extended_terminal/advanced_terminal.json");
        InitScreens.register(EliteTerminalMenu.TYPE,EliteTerminalScreen::new,"/screens/extended_terminal/elite_terminal.json");
        InitScreens.register(UltimateTerminalMenu.TYPE,UltimateTerminalScreen::new,"/screens/extended_terminal/ultimate_terminal.json");
        InitScreens.register(EpicTerminalMenu.TYPE,EpicTerminalScreen::new,"/screens/extended_terminal/epic_terminal.json");

        InitScreens.register(SculkTerminalMenu.TYPE,SculkTerminalScreen::new,"/screens/avaritia/sculk_terminal.json");
        InitScreens.register(NetherTerminalMenu.TYPE,NetherTerminalScreen::new,"/screens/avaritia/nether_terminal.json");
        InitScreens.register(EndTerminalMenu.TYPE,EndTerminalScreen::new,"/screens/avaritia/end_terminal.json");
        InitScreens.register(ExtremeTerminalMenu.TYPE,ExtremeTerminalScreen::new,"/screens/avaritia/extreme_terminal.json");

        InitScreens.register(NeoExtremeTerminalMenu.TYPE,NeoExtremeTerminalScreen::new,"/screens/avaritia/extreme_terminal.json");
    }

    public static void initWTScreen() {
        InitScreens.register(ETWTMenu.TYPE, ETWTScreen::new, "/screens/wireless_et_terminal.json");
    }


    public static void initColorParts(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex)
                        -> (new StaticItemColor(AEColor.TRANSPARENT).getColor(stack, tintIndex) | 0xFF000000),
                ETParts.TERMINAL_PARTS.stream().map(x -> (ItemLike) x).toArray(ItemLike[]::new));
    }
}

