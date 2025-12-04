package me.myogoo.extendedterminal.init;

import appeng.core.AppEng;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.avaritiaRe.EndTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.ExtremeTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.NetherTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.SculkTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.AdvancedTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.EliteTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.UltimateTerminalMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ETMenus {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(Registries.MENU, AppEng.MOD_ID);

    public static final Supplier<MenuType<BasicTerminalMenu>> BASIC_TERMINAL = REGISTER.register(ETMenuType.BASIC_TERMINAL.getIdAsString(), () -> BasicTerminalMenu.TYPE);
    public static final Supplier<MenuType<AdvancedTerminalMenu>> ADVANCED_TERMINAL = REGISTER.register(ETMenuType.ADVANCED_TERMINAL.getIdAsString(), () -> AdvancedTerminalMenu.TYPE);
    public static final Supplier<MenuType<EliteTerminalMenu>> ELITE_TERMINAL = REGISTER.register(ETMenuType.ELITE_TERMINAL.getIdAsString(), () -> EliteTerminalMenu.TYPE);
    public static final Supplier<MenuType<?>> ULTIMATE_TERMINAL = REGISTER.register(ETMenuType.ULTIMATE_TERMINAL.getIdAsString(), () -> UltimateTerminalMenu.TYPE);

    // Re:Avaritia terminal
    public static final Supplier<MenuType<SculkTerminalMenu>> SCULK_TERMINAL = REGISTER.register(ETMenuType.SCULK_TERMINAL.getIdAsString(), () -> SculkTerminalMenu.TYPE);
    public static final Supplier<MenuType<NetherTerminalMenu>> NETHER_TERMINAL = REGISTER.register(ETMenuType.NETHER_TERMINAL.getIdAsString(), () -> NetherTerminalMenu.TYPE);
    public static final Supplier<MenuType<EndTerminalMenu>> END_TERMINAL = REGISTER.register(ETMenuType.END_TERMINAL.getIdAsString(), () -> EndTerminalMenu.TYPE);
    public static final Supplier<MenuType<ExtremeTerminalMenu>> EXTREME_TERMINAL = REGISTER.register(ETMenuType.EXTREME_TERMINAL.getIdAsString(), () -> ExtremeTerminalMenu.TYPE);

    //public static final Supplier<MenuType<NeoExtremeTerminalMenu>> NEO_EXTREME_TERMINAL = REGISTER.register(ETMenuType.NEO_EXTREME_TERMINAL.getIdAsString(), () -> NeoExtremeTerminalMenu.TYPE);
}
