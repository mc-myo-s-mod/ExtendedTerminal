package me.myogoo.extendedterminal.init.wt;

import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.wt.UnitedWTMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

import static me.myogoo.extendedterminal.init.ETMenus.REGISTER;

public class WTMenus {
    public static final Supplier<MenuType<ETWTMenu>> ET_WT = REGISTER.register(ETMenuType.ET_TERMINAL.getWTIdAsString(), () -> ETWTMenu.TYPE);
    public static final Supplier<MenuType<UnitedWTMenu>> UNITED_WT = REGISTER.register(ETMenuType.UNITED_TERMINAL.getWTIdAsString(), () -> UnitedWTMenu.TYPE);

    public static void register() {}
}
