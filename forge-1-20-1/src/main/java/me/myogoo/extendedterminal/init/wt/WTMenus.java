package me.myogoo.extendedterminal.init.wt;

import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.wt.ExtendedCraftingWTMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.wt.UnitedWTMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

import static me.myogoo.extendedterminal.init.ETMenus.REGISTER;

public final class WTMenus {
    public static final Supplier<MenuType<ETWTMenu>> ET_WT = REGISTER.register(
            ETMenuType.ET_TERMINAL.getWTIdAsString(),
            () -> ETWTMenu.TYPE
    );
    public static final Supplier<MenuType<UnitedWTMenu>> UNITED_WT = REGISTER.register(
            ETMenuType.UNITED_TERMINAL.getWTIdAsString(),
            () -> UnitedWTMenu.TYPE
    );
    public static final Supplier<MenuType<ExtendedCraftingWTMenu>> EPIC_WT = REGISTER.register(
            ETMenuType.EPIC_TERMINAL.getWTIdAsString(), () -> ExtendedCraftingWTMenu.EPIC_TYPE);
    public static final Supplier<MenuType<ExtendedCraftingWTMenu>> LEGENDARY_WT = REGISTER.register(
            ETMenuType.LEGENDARY_TERMINAL.getWTIdAsString(), () -> ExtendedCraftingWTMenu.LEGENDARY_TYPE);

    private WTMenus() {
    }

    public static void register() {
    }
}
