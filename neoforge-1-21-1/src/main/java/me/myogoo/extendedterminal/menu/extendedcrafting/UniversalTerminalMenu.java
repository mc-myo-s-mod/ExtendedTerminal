package me.myogoo.extendedterminal.menu.extendedcrafting;

import appeng.api.storage.ITerminalHost;
import me.myogoo.extendedterminal.api.config.IETTerminalConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

/**
 * @deprecated Use {@link UnitedTerminalMenu}. Kept so older in-progress references still compile.
 */
@Deprecated(forRemoval = false)
public class UniversalTerminalMenu extends UnitedTerminalMenu {
    public UniversalTerminalMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host, ETMenuType etMenuType, IETTerminalConfig config) {
        super(menuType, id, ip, host);
    }
}
