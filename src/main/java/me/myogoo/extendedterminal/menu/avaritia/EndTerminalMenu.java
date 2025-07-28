package me.myogoo.extendedterminal.menu.avaritia;

import appeng.api.storage.ITerminalHost;
import appeng.menu.implementations.MenuTypeBuilder;
import me.myogoo.extendedterminal.config.ETConfig;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class EndTerminalMenu extends AvaritiaTerminalBaseMenu {
    public static final MenuType<EndTerminalMenu> TYPE = MenuTypeBuilder
            .create(EndTerminalMenu::new, ITerminalHost.class)
            .buildUnregistered(ETMenuType.END_TERMINAL.getId());

    public EndTerminalMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host) {
        super(menuType, id, ip, host, ETMenuType.ET_TERMINAL, ETConfig.END_TERMINAL_CONFIG);
    }
}
