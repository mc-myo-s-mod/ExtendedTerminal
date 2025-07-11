package me.myogoo.extendedterminal.init;

import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.AdvancedTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.EliteTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.UltimateTerminalMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ETMenus {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(Registries.MENU, ExtendedTerminal.MODID);

    public static final Supplier<MenuType<BasicTerminalMenu>> BASIC_TERMINAL = REGISTER.register(ETMenuType.BASIC_TERMINAL.getIdAsString(), () -> BasicTerminalMenu.TYPE);
    public static final Supplier<MenuType<AdvancedTerminalMenu>> ADVANCED_TERMINAL = REGISTER.register(ETMenuType.ADVANCED_TERMINAL.getIdAsString(), () -> AdvancedTerminalMenu.TYPE);
    public static final Supplier<MenuType<EliteTerminalMenu>> ELITE_TERMINAL = REGISTER.register(ETMenuType.ELITE_TERMINAL.getIdAsString(), () -> EliteTerminalMenu.TYPE);
    public static final Supplier<MenuType<?>> ULTIMATE_TERMINAL = REGISTER.register(ETMenuType.ULTIMATE_TERMINAL.getIdAsString(), () -> UltimateTerminalMenu.TYPE);
}
